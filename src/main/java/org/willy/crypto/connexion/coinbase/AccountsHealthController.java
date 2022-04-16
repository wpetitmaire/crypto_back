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
import org.willy.crypto.connexion.coinbase.objects.health.WalletHealth;
import org.willy.crypto.connexion.coinbase.objects.sell.Sell;
import org.willy.crypto.connexion.coinbase.services.AccountsService;
import org.willy.crypto.connexion.coinbase.services.HealthService;
import org.willy.crypto.connexion.coinbase.services.TransactionsService;
import org.willy.crypto.icons.IconsService;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
@RestController
@RequestMapping(path = "api/v1/coinbase/health")
@CrossOrigin
public class AccountsHealthController {

    HealthService healthService;
    AccountsService accountsService;
    TransactionsService transactionsService;
//    IconsService iconsService;

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountHealth>> accountsHealth(
            @RequestParam(required = false) Boolean withoutEmptyAccounts,
            @RequestParam(required = false) Boolean forceRefresh
    ) throws CoinbaseApiException {

        withoutEmptyAccounts = withoutEmptyAccounts != null;
        forceRefresh = forceRefresh != null;

        log.info("accountsHealth : withoutEmptyAccounts = {} - forceRefresh = {}", withoutEmptyAccounts, forceRefresh);

        List<AccountHealth> accountHealthCBList = healthService.getAccountsHealth(withoutEmptyAccounts, forceRefresh);

        if (accountHealthCBList.size() > 0) {
            return new ResponseEntity<>(accountHealthCBList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/wallet")
    public WalletHealth getWalletHealth(@RequestParam(required = false) Boolean forceRefresh) {
        return healthService.getWalletHealth(forceRefresh);
    };
}
