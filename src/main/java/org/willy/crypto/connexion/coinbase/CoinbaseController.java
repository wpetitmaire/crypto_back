package org.willy.crypto.connexion.coinbase;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.willy.crypto.connexion.coinbase.exceptions.CoinbaseApiException;
import org.willy.crypto.connexion.coinbase.objects.account.AccountCB;
import org.willy.crypto.connexion.coinbase.objects.buy.BuyCB;
import org.willy.crypto.connexion.coinbase.objects.health.AccountHealthCB;
import org.willy.crypto.connexion.coinbase.objects.price.PriceCB;
import org.willy.crypto.connexion.coinbase.objects.sell.SellCB;
import org.willy.crypto.connexion.coinbase.objects.transaction.TransactionCB;
import org.willy.crypto.connexion.coinbase.objects.user.UserCB;
import org.willy.crypto.connexion.coinbase.services.*;

import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(path = "api/v1/coinbase")
public class CoinbaseController {

    final static Logger logger = LoggerFactory.getLogger(CoinbaseController.class);

    final CoinbaseAccountsService accountsService;
    final CoinbaseTransactionsService transactionsService;
    final CoinbaseUserService coinbaseUserService;
    final CoinbasePriceService priceService;
    final CoinbaseHealthService healthService;

    @GetMapping(path = "/accounts")
    public ResponseEntity<List<AccountCB>> readAccounts(@RequestParam(required = false) Boolean refresh) {
        logger.info(String.valueOf(refresh));

        if (refresh == null) {
            refresh = false;
        }
        List<AccountCB> accounts = accountsService.readAccounts(refresh);

        if (accounts.size() > 0) {
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/accounts/{id}")
    public ResponseEntity<AccountCB> getAccount(@PathVariable String id) throws CoinbaseApiException {
        logger.info("get account {}", id);
        return new ResponseEntity<>(accountsService.getAccount(id), HttpStatus.OK);
    }

    @GetMapping(path = "/accounts/{id}/transactions")
    public ResponseEntity<List<TransactionCB>> readTransactions(@PathVariable String id) {
        logger.info("Read transactions for account : " + id);

        List<TransactionCB> transactions = transactionsService.readTransactionsOfAAccount(id, false);

        if (transactions.size() > 0) {
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/accounts/{accountId}/transactions/{transactionId}")
    public ResponseEntity<TransactionCB> getTransaction(@PathVariable String accountId, @PathVariable String transactionId) throws CoinbaseApiException {
        logger.info("Get transaction account {} transaction {}", accountId, transactionId);

        return new ResponseEntity<>(transactionsService.getTransaction(accountId, transactionId), HttpStatus.OK);
    }

    /**
     * Read buys of an account
     * @param id Id oh the account
     * @return List of buys
     */
    @GetMapping(path = "/accounts/{id}/buys")
    public ResponseEntity<List<BuyCB>> readBuys(@PathVariable String id) {
        logger.info("read buys for account {}", id);

        List<BuyCB> buys = transactionsService.getBuys(id);

        if (buys.size() > 0) {
            return new ResponseEntity<>(buys, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Read sells of an account
     * @param id Id oh the account
     * @return List of sells
     */
    @GetMapping(path = "/accounts/{id}/sells")
    public ResponseEntity<List<SellCB>> readSells(@PathVariable String id) {
        logger.info("read sells for account {}", id);

        List<SellCB> sells = transactionsService.getSells(id);

        if (sells.size() > 0) {
            return new ResponseEntity<>(sells, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping(path = "/user")
    public ResponseEntity<UserCB> getCurrentUser() throws CoinbaseApiException {
        return new ResponseEntity<>(coinbaseUserService.getUser(), HttpStatus.OK);
    }

    @GetMapping("/price/{baseCurrency}")
    public ResponseEntity<PriceCB> getPrice(@PathVariable String baseCurrency, @RequestParam(required = false) String date) throws CoinbaseApiException {
        return new ResponseEntity<>(priceService.getPrice(baseCurrency, date), HttpStatus.OK);
    }

    @GetMapping("/health")
    public ResponseEntity<List<AccountHealthCB>> accountsHealth() throws CoinbaseApiException {
        List<AccountHealthCB> accountHealthCBList = healthService.getAccountsHealth();

        if (accountHealthCBList.size() > 0) {
            return new ResponseEntity<>(accountHealthCBList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

}
