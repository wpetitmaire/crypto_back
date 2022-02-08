package org.willy.crypto.connexion.coinbase.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.willy.crypto.connexion.coinbase.objects.transaction.TransactionCB;
import org.willy.crypto.connexion.coinbase.objects.transaction.TransactionRepository;
import org.willy.crypto.connexion.coinbase.objects.transaction.TransactionResponseCB;
import org.willy.crypto.helpers.gsonadapter.GsonLocalDateTime;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Scope("singleton")
public class CoinbaseTransactionsService {

    private final static Logger logger = LogManager.getLogger(CoinbaseTransactionsService.class);
    private final CoinbaseConnexionService connexionService;
    private final TransactionRepository transactionRepository;

    public CoinbaseTransactionsService(CoinbaseConnexionService connexionService, TransactionRepository transactionRepository) {
        this.connexionService = connexionService;
        this.transactionRepository = transactionRepository;
    }

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
    public List<TransactionCB> readTransactionsOfAAccount(String accountId, boolean testIsATransaction) {
        logger.info("Read transactions from ressource : " + accountId + " - just test ? : " + testIsATransaction);

        List<TransactionCB> transactions = new ArrayList<>();
        boolean isNextPage;
        HttpResponse<String> getRequestResponse;
        String reponse;
        TransactionResponseCB transactionResponse;
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
            transactionResponse = gson.fromJson(reponse, TransactionResponseCB.class);
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
    public TransactionCB getTransaction(String accountId, String transactionId) {
        logger.info("get transaction of ressource {} with id {}", accountId, transactionId);

        TransactionCB transaction = transactionRepository.findById(transactionId).orElse(null);

        if (transaction == null) { // if not found in the DB, call the API ant then save in DB
            String ressourceUrl = "/v2/accounts/"+ accountId +"/transactions/" + transactionId;
            HttpResponse<String> response = connexionService.getRequest(ressourceUrl);

            if (response.statusCode() != HttpStatus.OK.value()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found.");
            }

            transaction = new Gson().fromJson(response.body(), TransactionCB.class);
            transaction.setRetrieve_date(LocalDateTime.now());
            transaction.setAssociated_account_id(accountId);
        }

        transactionRepository.save(transaction);

        return transaction;
    }
}
