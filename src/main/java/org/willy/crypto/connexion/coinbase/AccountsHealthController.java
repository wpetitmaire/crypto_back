package org.willy.crypto.connexion.coinbase;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.willy.crypto.connexion.coinbase.exceptions.CoinbaseApiException;
import org.willy.crypto.connexion.coinbase.objects.health.AccountHealth;
import org.willy.crypto.connexion.coinbase.services.HealthService;

import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
@RestController
@RequestMapping(path = "api/v1/coinbase/health")
public class AccountsHealthController {

    HealthService healthService;

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountHealth>> accountsHealth() throws CoinbaseApiException {
        log.info("accountsHealth");
        List<AccountHealth> accountHealthCBList = healthService.getAccountsHealth();

        if (accountHealthCBList.size() > 0) {
            return new ResponseEntity<>(accountHealthCBList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

}
