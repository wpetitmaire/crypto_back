package org.willy.crypto.connexion.coinbase;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.willy.crypto.connexion.coinbase.objects.transaction.Transaction;
import org.willy.crypto.connexion.coinbase.services.TransactionsService;

import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
@RestController
@RequestMapping(path = "api/v1/coinbase/test")
@CrossOrigin
public class AccountsTestController {

    TransactionsService transactionsService;

    @GetMapping(path = "/transactions")
    ResponseEntity<List<Transaction>> getTransactions(@RequestParam String accountId) {
        log.info("[TEST] getTransactions");

        return new ResponseEntity<>(transactionsService.readTransactionsOfAAccount(accountId, true), HttpStatus.OK);
    }

    @GetMapping(path = "/nonefiattransactions")
    ResponseEntity<List<Transaction>> getNoneFiatTransactions() {
        return new ResponseEntity<>(transactionsService.getAllNoneFiatCurrencyTransactions(), HttpStatus.OK);
    }

    @GetMapping(path = "/positivestransactions")
    ResponseEntity<List<Transaction>> getPositivesTransactions(@RequestParam String accountCode) {
        log.info("accountId : {}", accountCode);
        return new ResponseEntity<>(transactionsService.getPositivesTransactions(accountCode), HttpStatus.OK);
    }
}
