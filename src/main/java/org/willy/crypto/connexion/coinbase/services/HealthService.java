package org.willy.crypto.connexion.coinbase.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.willy.crypto.connexion.coinbase.exceptions.CoinbaseApiException;
import org.willy.crypto.connexion.coinbase.objects.account.Account;
import org.willy.crypto.connexion.coinbase.objects.buy.Buy;
import org.willy.crypto.connexion.coinbase.objects.health.*;
import org.willy.crypto.connexion.coinbase.objects.sell.Sell;

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
    final TransactionsService transactionsService;
    final WalletHealthRepository walletHealthRepository;

    /**
     * Return the list of each (not empty) account health
     * @param withoutEmptyAccounts to exclude used account but empty
     * @param forceRefresh Force refresh db datas by calling API
     * @return List of health of each account
     * @throws CoinbaseApiException
     */
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

    /**
     * Get the sum of all buys
     * @param forceRefresh refresh datas in DB
     * @return sum of all buys
     */
    public BigDecimal getWalletBuys(Boolean forceRefresh) {
        log.info("getWalletBuys");

        forceRefresh = forceRefresh != null && forceRefresh;

        if (forceRefresh) {
            accountsService.readAccounts(true);
        }

        BigDecimal buyAmount = new BigDecimal(0);
        List<Account> accounts = accountsService.getAllNoneFiatAccounts();

        for (Account account : accounts) {
            List<Buy> buys = transactionsService.getBuys(account.getId());
            for (Buy buy : buys) {
                buyAmount = buyAmount.add(buy.getTotal().getAmount());
            }
        }

        return buyAmount;
    }

    /**
     * Get the sum of all sells
     * @param forceRefresh
     * @return sum of all sells
     */
    public BigDecimal getWalletSells(Boolean forceRefresh) {
        log.info("getWalletSells");

        forceRefresh = forceRefresh != null && forceRefresh;

        if (forceRefresh) {
            accountsService.readAccounts(true);
        }

        BigDecimal sellAmount = new BigDecimal(0);
        List<Account> accounts = accountsService.getAllNoneFiatAccounts();

        for (Account account : accounts) {
            List<Sell> sells = transactionsService.getSells(account.getId());
            for (Sell sell : sells) {
                sellAmount = sellAmount.add(sell.getTotal().getAmount());
            }
        }

        return sellAmount;
    }

    public BigDecimal getTotalFees() {
        log.info("getTotalFees");

        BigDecimal feesAmount = new BigDecimal(0);
        List<Account> accounts = accountsService.getAllNoneFiatAccounts();

        for (Account account : accounts) {
            List<Buy> buys = transactionsService.getBuys(account.getId());
            for (Buy buy : buys) {
                feesAmount = feesAmount.add(buy.getFee().getAmount());
            }
        }

        return feesAmount;
    }

    public BigDecimal getWalletValue(Boolean forceRefresh) {
        log.info("getWalletValue");

        forceRefresh = forceRefresh != null && forceRefresh;

        if (forceRefresh) {
            accountsService.readAccounts(true);
        }

        BigDecimal walletValue = new BigDecimal(0);
        List<AccountHealth> accountHealthList = healthRepository.findAll();

        for (AccountHealth accountHealth: accountHealthList) {
            walletValue = walletValue.add(accountHealth.getAmountPrice());
        }

        return walletValue;
    }

    public WalletHealth getWalletHealth(Boolean forceRefresh) {
        log.info("Wallet health");

        forceRefresh = forceRefresh != null;

        WalletHealth walletHealth;
        if (forceRefresh || walletHealthRepository.count() == Long.parseLong("0")) {
            BigDecimal buys = getWalletBuys(forceRefresh);
            BigDecimal sells = getWalletSells(forceRefresh);
            BigDecimal value = getWalletValue(forceRefresh);
            BigDecimal balanceWithoutFees = value.subtract(buys);
            BigDecimal fees = getTotalFees();
            BigDecimal balance = balanceWithoutFees.subtract(fees);

            walletHealth = new WalletHealth(buys, sells, fees, balance, balanceWithoutFees);

            walletHealthRepository.deleteAll();
            walletHealthRepository.save(walletHealth);
        } else {
            walletHealth = walletHealthRepository.findAll().get(0);
        }

        return walletHealth;
    }
}
