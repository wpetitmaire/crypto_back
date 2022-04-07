package org.willy.crypto.connexion.coinbase.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.willy.crypto.connexion.coinbase.exceptions.CoinbaseApiException;
import org.willy.crypto.connexion.coinbase.objects.account.Account;
import org.willy.crypto.connexion.coinbase.objects.health.AccountHealth;
import org.willy.crypto.connexion.coinbase.objects.health.HealthRepository;
import org.willy.crypto.connexion.coinbase.objects.health.PriceHistory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    final HealthRepository healthRepository;

    public List<AccountHealth> getAccountsHealth(Boolean withoutEmptyAccounts) throws CoinbaseApiException {
        return getAccountsHealth(withoutEmptyAccounts, false);
    }

    public List<AccountHealth> getAccountsHealth(Boolean withoutEmptyAccounts, Boolean forceRefresh) throws CoinbaseApiException {
        log.info("Get accounts health - withoutEmptyAccounts={} forceRefresh={}", withoutEmptyAccounts, forceRefresh);

        forceRefresh = forceRefresh != null && forceRefresh;

        if (!forceRefresh) {
            // Get from DB
            List<AccountHealth> saveList;
            if (!withoutEmptyAccounts) {
                saveList = healthRepository.findAll();
            } else {
                saveList = healthRepository.findAllNotEmptyAccounts();
            }

            if (!saveList.isEmpty()) {
                return saveList;
            }
        }


        List<AccountHealth> accountHealthList = new ArrayList<>();
        List<Account> accounts = accountsService.getAllNoneFiatAccounts();

        for (Account account : accounts) {

            if (withoutEmptyAccounts && account.getBalance().getAmount().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            AccountHealth health = new AccountHealth();

            health.setAccountId(account.getCurrency().getCode());
            health.setAccountName(account.getCurrency().getName());
            health.setIconUrl(account.getIconUrl());

            BigDecimal price = priceService.getPrice(account.getCurrency().getCode()).getAmount();
            health.setUnitPrice(price);

            BigDecimal oldPrice = priceService.getPrice(account.getCurrency().getCode(), LocalDate.now().minus(1L, ChronoUnit.DAYS)).getAmount();
            BigDecimal unitPriceVariation = price.subtract(oldPrice).setScale(2, RoundingMode.HALF_UP);
            health.setUnitPriceVariation(unitPriceVariation);

            BigDecimal unitPriceVariationPourcentage;
            try{
                unitPriceVariationPourcentage = price.divide(oldPrice, RoundingMode.HALF_UP).subtract(BigDecimal.ONE).multiply(new BigDecimal(100));
            } catch (ArithmeticException e) {
                unitPriceVariationPourcentage = BigDecimal.ZERO;
            }
            health.setUnitPriceVariationPourcentage(unitPriceVariationPourcentage);

            BigDecimal amount = account.getBalance().getAmount();
            health.setAmount(amount);

            BigDecimal amountPrice = price.multiply(amount);
            health.setAmountPrice(amountPrice);

            List<PriceHistory> prices = new ArrayList<>();
            for (int i = 7; i > 0; i--) {
                LocalDate localDate = LocalDate.now().minus(i, ChronoUnit.DAYS);
                String date = DateTimeFormatter.ofPattern("dd-MM-uuuu").format(localDate);
                BigDecimal historyPrice = priceService.getPrice(account.getCurrency().getCode(), localDate).getAmount();
                prices.add(new PriceHistory(date, historyPrice));
            }
            health.setWeekHistory(prices);

            accountHealthList.add(health);
        }

        healthRepository.saveAll(accountHealthList);

        return accountHealthList;
    }


}
