package org.willy.crypto.connexion.coinbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.willy.crypto.connexion.coinbase.objects.account.AccountCB;
import org.willy.crypto.connexion.coinbase.objects.transaction.TransactionCB;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/coinbase")
public class CoinbaseController {

    Logger logger = LoggerFactory.getLogger(CoinbaseController.class);

    private final CoinbaseService api;

    @Autowired
    public CoinbaseController(CoinbaseService coinbaseApi) { this.api = coinbaseApi; }

    @GetMapping(path = "/accounts")
    public List<AccountCB> readAccounts(@RequestParam(required = false) Boolean refresh) {

        logger.info(String.valueOf(refresh));

        if (refresh == null) {
            refresh = false;
        }
        return api.readAccounts(refresh);
    }

    @GetMapping(path = "/accounts/{id}")
    public AccountCB getAccount(@PathVariable String id) {
        logger.info("get account {}", id);

        return api.getAccount(id);
    }

    @GetMapping(path = "/accounts/{id}/transactions")
    public List<TransactionCB> readTransactions(@PathVariable String id) {
        logger.info("Read transactions for account : " + id);

        return api.readTransactionsOfAAccount(id, false);
    }

    @GetMapping(path = "/accounts/{accountId}/transactions/{transactionId}")
    public TransactionCB getTransaction(@PathVariable String accountId, @PathVariable String transactionId) {
        logger.info("Get transaction account {} transaction {}", accountId, transactionId);

        return api.getTransaction(accountId, transactionId);
    }

}
