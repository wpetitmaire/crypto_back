package org.willy.crypto.connexion.coinbase.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.willy.crypto.connexion.coinbase.exceptions.CoinbaseApiException;
import org.willy.crypto.connexion.coinbase.objects.buy.Buy;
import org.willy.crypto.connexion.coinbase.objects.buy.BuyResponse;
import org.willy.crypto.connexion.coinbase.objects.sell.Sell;
import org.willy.crypto.connexion.coinbase.objects.sell.SellResponse;
import org.willy.crypto.connexion.coinbase.objects.transaction.Transaction;
import org.willy.crypto.connexion.coinbase.objects.transaction.TransactionRepository;
import org.willy.crypto.connexion.coinbase.objects.transaction.TransactionResponse;
import org.willy.crypto.helpers.gsonadapter.GsonLocalDateTime;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class TransactionsService {

    final static Logger logger = LogManager.getLogger(TransactionsService.class);
    final ConnexionService connexionService;
    final TransactionRepository transactionRepository;

    /**
     * Test is there is at least one transaction or not for an account
     * @param accountId id of the account
     * @return true if there is at least one transaction
     */
    public boolean thereIsTransactionsForTheAccount(String accountId){
        logger.info("thereIsTransactionsForTheAccount : " + accountId);
        return readTransactionsOfAAccount(accountId, true).size() > 0;
    }

    /**
     * Get the list of the transactions for an account
     * @param accountId Id of the account
     * @param testIsATransaction true if we just need to know if it exists at least one transaction
     * @return List of transactions
     */
    public List<Transaction> readTransactionsOfAAccount(String accountId, boolean testIsATransaction) {
        logger.info("Read transactions from ressource : " + accountId + " - just test ? : " + testIsATransaction);

        List<Transaction> transactions = new ArrayList<>();
        boolean isNextPage;
        HttpResponse<String> getRequestResponse;
        String reponse;
        TransactionResponse transactionResponse;
        String ressourceUrl;
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime()).create();

        if (testIsATransaction) { // retrieve only one transaction is enough for testing.
            ressourceUrl = "/v2/accounts/"+accountId+"/transactions?&limit=1";
        } else {
            ressourceUrl = "/v2/accounts/"+accountId+"/transactions";
        }

        do {
            getRequestResponse = connexionService.getRequest(ressourceUrl);
            reponse = getRequestResponse.body();
            transactionResponse = gson.fromJson(reponse, TransactionResponse.class);
            transactions.addAll(transactionResponse.getData());

            // If there is another page, change ressource url and do it again
            if (!testIsATransaction) {
                isNextPage = transactionResponse.getPagination().getNext_uri() != null;
                ressourceUrl = transactionResponse.getPagination().getNext_uri();
            }
            else
                isNextPage = false;
        } while (isNextPage);

        transactions.forEach(transaction -> {
            transaction.setRetrieve_date(LocalDateTime.now());
            transaction.setAssociated_account_id(accountId);
        });

        transactionRepository.saveAll(transactions);

        logger.debug("Transaction number : {}", transactions.size());

        return transactions;
    }

    /**
     * Get a specific transaction
     * @param transactionId id of the needed transaction
     * @return the transaction
     */
    public Transaction getTransaction(String accountId, String transactionId) throws CoinbaseApiException {
        logger.info("get transaction of ressource {} with id {}", accountId, transactionId);

        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);

        if (transaction == null) { // if not found in the DB, call the API ant then save in DB
            String ressourceUrl = "/v2/accounts/"+ accountId +"/transactions/" + transactionId;
            HttpResponse<String> response = connexionService.getRequest(ressourceUrl);

            if (response.statusCode() != HttpStatus.OK.value()) {
                throw new CoinbaseApiException("Transaction with id " + transactionId, HttpStatus.BAD_REQUEST);
            }

            transaction = new Gson().fromJson(response.body(), Transaction.class);
            transaction.setRetrieve_date(LocalDateTime.now());
            transaction.setAssociated_account_id(accountId);
        }

        transactionRepository.save(transaction);

        return transaction;
    }

    public List<Buy> getBuys(String accountId) {
        logger.info("get buys for account {}", accountId);

//        JsonObject debugStringResponse = null;

        List<Buy> buys = new ArrayList<>();
        boolean isNextPage;
        HttpResponse<String> response;
        String responseBody;
        BuyResponse buyResponse;
        String ressourceUrl;
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime()).create();

        ressourceUrl = "/v2/accounts/" + accountId + "/buys";

        do {
            response = connexionService.getRequest(ressourceUrl);
            responseBody = response.body();

//            debugStringResponse = gson.fromJson(responseBody, JsonObject.class);
//            logger.info(gson.toJson(debugStringResponse));

            buyResponse = gson.fromJson(responseBody, BuyResponse.class);
            buys.addAll(buyResponse.getData());

            // If there is another page, change ressource url and do it again
            isNextPage = buyResponse.getPagination().getNext_uri() != null;
            ressourceUrl = buyResponse.getPagination().getNext_uri();
        } while (isNextPage);

        return buys;
    }

    public List<Sell> getSells(String accountId) {
        logger.info("get sell for account {}", accountId);

        List<Sell> sells = new ArrayList<>();
        boolean isNextPage;
        HttpResponse<String> response;
        String responseBody;
        SellResponse sellResponse;
        String ressourceUrl;
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime()).create();

        ressourceUrl = "/v2/accounts/" + accountId + "/sells";

        do {
            response = connexionService.getRequest(ressourceUrl);
            responseBody = response.body();

            sellResponse = gson.fromJson(responseBody, SellResponse.class);
            sells.addAll(sellResponse.getData());

            // If there is another page, change ressource url and do it again
            isNextPage = sellResponse.getPagination().getNext_uri() != null;
            ressourceUrl = sellResponse.getPagination().getNext_uri();
        } while (isNextPage);

        return sells;
    }



}
