package org.willy.crypto.connexion.coinbase.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.willy.crypto.connexion.coinbase.exceptions.CoinbaseApiException;
import org.willy.crypto.connexion.coinbase.objects.account.AccountCB;
import org.willy.crypto.connexion.coinbase.objects.account.AccountRepository;
import org.willy.crypto.connexion.coinbase.objects.account.AccountsResponseCB;
import org.willy.crypto.helpers.gsonadapter.GsonLocalDateTime;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@Scope("singleton")
public class CoinbaseAccountsService {

    final static Logger logger = LogManager.getLogger(CoinbaseAccountsService.class);

    final CoinbaseConnexionService connexionService;
    final CoinbaseTransactionsService transactionsService;

    final AccountRepository accountRepository;

    LocalDateTime accountsRetrieveDate;

    /**
     * Return all Coinbase accounts
     * @return user accounts
     * @param refresh force refresh
     */
    public List<AccountCB> readAccounts(Boolean refresh) {
        logger.info("Read accounts");

        List<AccountCB> accountList = new ArrayList<>();

        LocalDateTime timeLimit = LocalDateTime.now().minus(1L, ChronoUnit.DAYS);
        if (accountsRetrieveDate != null && (accountsRetrieveDate.compareTo(timeLimit) > 0 || !refresh)) {
            return accountRepository.findAll();
        }

        // Start from scratch
        accountRepository.deleteAll();

        boolean isNextPage;
        String ressourceUrl = "/v2/accounts";
        HttpResponse<String> getRequestResponse;
        String response;
        AccountsResponseCB accountsResponse;
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime()).create();

        do {
            getRequestResponse = connexionService.getRequest(ressourceUrl);
            response = getRequestResponse.body();
            accountsResponse = gson.fromJson(response, AccountsResponseCB.class);
            accountList.addAll(accountsResponse.getData());

            // If there is another page, change ressource url and do it again
            isNextPage = accountsResponse.getPagination().getNext_uri() != null;
            ressourceUrl = accountsResponse.getPagination().getNext_uri();
        } while (isNextPage);

        accountsRetrieveDate = LocalDateTime.now();

        // Check each account and keep only those who are/were used by the user
        accountList = accountList.stream()
                .filter(account -> transactionsService.thereIsTransactionsForTheAccount(account.getCurrency().getCode()))
                .collect(Collectors.toList());

        // save the result in the DB and save the retrieve data
        accountRepository.saveAll(accountList);

        return accountList;
    }

    /**
     * Get an account ressource that is used or has been used by the user
     * @param id id of the needed account
     * @return the account
     */
    public AccountCB getAccount(String id) throws CoinbaseApiException {

        AccountCB account = accountRepository.findById(id).orElse(null);

        logger.info(account);

        // If no account found, calls the API to see if we need to add the ressource to the database
        if (account == null) {
            final String ressourceUrl = "/v2/accounts/" + id;
            HttpResponse<String> response = connexionService.getRequest(ressourceUrl);

            if (response.statusCode() != HttpStatus.OK.value()) {
                throw new CoinbaseApiException("Account ressource not found", HttpStatus.BAD_REQUEST);
            }

            account = new Gson().fromJson(response.body(), AccountCB.class);

            if (transactionsService.thereIsTransactionsForTheAccount(account.getCurrency().getCode())) {
                accountRepository.save(account);
            } else {
                throw new CoinbaseApiException("Account ressource not used by the user.", HttpStatus.METHOD_NOT_ALLOWED);
            }
        }

        return account;
    }


}
