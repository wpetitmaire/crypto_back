package org.willy.crypto.connexion.coinbase.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.willy.crypto.connexion.coinbase.exceptions.CoinbaseApiException;
import org.willy.crypto.connexion.coinbase.objects.account.Account;
import org.willy.crypto.connexion.coinbase.objects.health.AccountHealth;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@Log4j2
public class HealthService {
    final AccountsService accountsService;
    final PriceService priceService;

    public List<AccountHealth> getAccountsHealth() throws CoinbaseApiException {
        log.info("Get accounts health");

        List<AccountHealth> accountHealthList = new ArrayList<>();
        List<Account> accounts = accountsService.getAllNoneFiatAccounts();

        for (Account account : accounts) {

            AccountHealth health = new AccountHealth();

            health.setAccountId(account.getCurrency().getCode());

            BigDecimal price = priceService.getPrice(account.getCurrency().getCode()).getAmount();
            health.setUnitPrice(price);

            BigDecimal deltaPrice = priceService.getPrice(account.getCurrency().getCode(), LocalDate.now().minus(1L, ChronoUnit.DAYS)).getAmount();
            health.setUnitPriceDeltaVariation(price.subtract(deltaPrice).setScale(2, RoundingMode.HALF_UP));

            BigDecimal amount = account.getBalance().getAmount();
            health.setAmount(amount);

            BigDecimal amountPrice = price.multiply(amount);
            health.setAmountPrice(amountPrice);

            BigDecimal amountPriceDelta = deltaPrice.multiply(amount);
            BigDecimal amountPriceDeltaVariation = amountPrice.subtract(amountPriceDelta);
            health.setAmountPriceDeltaVariation(amountPriceDeltaVariation.setScale(2, RoundingMode.HALF_UP));

            accountHealthList.add(health);
        }

        return accountHealthList;
    }
}
