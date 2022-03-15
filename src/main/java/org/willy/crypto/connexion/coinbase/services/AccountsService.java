package org.willy.crypto.connexion.coinbase.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.willy.crypto.connexion.coinbase.exceptions.CoinbaseApiException;
import org.willy.crypto.connexion.coinbase.objects.account.Account;
import org.willy.crypto.connexion.coinbase.objects.account.AccountRepository;
import org.willy.crypto.connexion.coinbase.objects.account.AccountResponse;
import org.willy.crypto.connexion.coinbase.objects.account.AccountsPaginationResponse;
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
@Log4j2
public class AccountsService {

    final ConnexionService connexionService;
    final TransactionsService transactionsService;

    final AccountRepository accountRepository;

    LocalDateTime accountsRetrieveDate;

    /**
     * Return all Coinbase accounts
     * @return user accounts
     * @param refresh force refresh
     */
    public List<Account> readAccounts(Boolean refresh) {
        log.info("Read accounts");

        List<Account> accountList = new ArrayList<>();

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
        AccountsPaginationResponse accountsResponse;
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime()).create();

        do {
            getRequestResponse = connexionService.getRequest(ressourceUrl);
            response = getRequestResponse.body();
            accountsResponse = gson.fromJson(response, AccountsPaginationResponse.class);
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
    public Account getAccount(String id) throws CoinbaseApiException {

        Account account = accountRepository.findById(id).orElse(null);
        JsonObject debugStringResponse = null;

        log.info(account);

        // If no account found, calls the API to see if we need to add the ressource to the database
        if (account == null) {
            final String ressourceUrl = "/v2/accounts/" + id;
            HttpResponse<String> response = connexionService.getRequest(ressourceUrl);
            Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime()).create();

            log.info("après requête");

            debugStringResponse = gson.fromJson(response.body(), JsonObject.class);
            log.info(gson.toJson(debugStringResponse));

            if (response.statusCode() != HttpStatus.OK.value()) {
                throw new CoinbaseApiException("Account ressource not found", HttpStatus.BAD_REQUEST);
            }

            account = gson.fromJson(response.body(), AccountResponse.class).getData();

            if (transactionsService.thereIsTransactionsForTheAccount(account.getCurrency().getCode())) {
                accountRepository.save(account);
            } else {
                throw new CoinbaseApiException("Account ressource not used by the user.", HttpStatus.METHOD_NOT_ALLOWED);
            }
        }

        return account;
    }

    /**
     * Get all none fiat accounts with the default Sort method (on currency)
     * @return non fiat accounts
     */
    public List<Account> getAllNoneFiatAccounts() {
        return getAllNoneFiatAccounts(Sort.by(Sort.Direction.ASC, "currency"));
    }

    /**
     * Get all accounts that are not fiat account (all crypto accounts)
     * @param sort sort method to apply on result
     * @return none fiat accounts
     */
    public List<Account> getAllNoneFiatAccounts(Sort sort) {
        log.info("getAllNoneFiatAccounts");
        // Get all none fiat accounts from DB
        List<Account> accounts = accountRepository.findAllNoneFiatAccounts(sort);

        // If nothing in DB, call API to fill the DB and try again
        if (accounts.isEmpty()) {
            readAccounts(true);
            return getAllNoneFiatAccounts(sort);
        }

        return accounts;
    }

}
