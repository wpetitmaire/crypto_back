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
import org.willy.crypto.connexion.coinbase.objects.transaction.Transaction;
import org.willy.crypto.connexion.coinbase.objects.transaction.TransactionRepository;

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
    final TransactionsService transactionsService;
    final HealthRepository healthRepository;
    final WalletHealthRepository walletHealthRepository;
    final TransactionRepository transactionRepository;

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

        log.info("saveList.isEmpty() - true");

        // refresh account list
        accountsService.readAccounts(true);

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

            // Get the current price
            BigDecimal price = priceService.getPrice(account.getCurrency().getCode()).getAmount();
            health.setUnitPrice(price);

            // Get the price of yesterday
            BigDecimal oldPrice = priceService.getPrice(account.getCurrency().getCode(), LocalDate.now().minus(1L, ChronoUnit.DAYS)).getAmount();
            BigDecimal unitPriceVariation = price.subtract(oldPrice).setScale(2, RoundingMode.HALF_UP);
            health.setUnitPriceVariation(unitPriceVariation);

            // Delta % variation calculation
            BigDecimal unitPriceVariationPourcentage;
            try{
                unitPriceVariationPourcentage = price.divide(oldPrice, RoundingMode.HALF_UP).subtract(BigDecimal.ONE).multiply(new BigDecimal(100));
            } catch (ArithmeticException e) {
                unitPriceVariationPourcentage = BigDecimal.ZERO;
            }
            health.setUnitPriceVariationPourcentage(unitPriceVariationPourcentage);

            // Get amount of account
            BigDecimal amount = account.getBalance().getAmount();
            health.setAmount(amount);

            // Amount price calculation
            BigDecimal amountPrice = price.multiply(amount);
            health.setAmountPrice(amountPrice);

            // Calculation of the last week unit price evolution
            List<PriceHistory> prices = new ArrayList<>();
            for (int i = 7; i > 0; i--) {
                LocalDate localDate = LocalDate.now().minus(i, ChronoUnit.DAYS);
                String date = DateTimeFormatter.ofPattern("dd-MM-uuuu").format(localDate);
                BigDecimal historyPrice = priceService.getPrice(account.getCurrency().getCode(), localDate).getAmount();
                prices.add(new PriceHistory(date, historyPrice));
            }
            health.setWeekHistory(prices);

            // Calculation of the account health
            log.info("___________Calculation of the account health___________");
            BigDecimal walletHealth = BigDecimal.ZERO;
            List<Buy> walletBuys = transactionsService.getBuys(account.getId());
            for (Buy buy : walletBuys) {
                System.out.println(buy);
                log.info("-->BUY : {}", buy.getCreated_at());
                BigDecimal buyPrice = buy.getSubtotal().getAmount(); // Buy price without fees
                log.info("Buy without fees : {} €", buyPrice);
                BigDecimal actualAmountValue = buy.getAmount().getAmount().multiply(price);
                log.info("buy actual value : {}", actualAmountValue);
                BigDecimal valueDelta = actualAmountValue.subtract(buyPrice);
                log.info("Delta value : {}", valueDelta);
                walletHealth = walletHealth.add(valueDelta);
                log.info("current walletHealth : {}", walletHealth);
            }

            // We retrieve all the earning from stacking or elsewhere + calcul earnings price
            List<Transaction> positivesTransactions = transactionRepository.findAllPositivesTransactions(account.getCurrency().getCode());
            BigDecimal earnings = BigDecimal.ZERO;
            log.info("----TRANSACTIONS");
            for (Transaction transaction: positivesTransactions) {
                log.info("Amount : {}", transaction.getAmount());
                earnings = earnings.add(transaction.getAmount().getAmount());
            }
            BigDecimal earningPrice = earnings.multiply(price);
            health.setEarns(earnings);

            // add earning price to the health
            log.info("earnings : {} - price : {}", earnings, earningPrice);
            walletHealth = walletHealth.add(earningPrice);

            health.setHealth(walletHealth);

            accountHealthList.add(health);

//            break;
        }

        healthRepository.saveAll(accountHealthList);

        return accountHealthList;
    }

    /**
     * Get the sum of all buys
     * @return sum of all buys
     */
    public BigDecimal getWalletBuys() {
        log.info("getWalletBuys");

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
     * @return sum of all sells
     */
    public BigDecimal getWalletSells() {
        log.info("getWalletSells");

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

    public BigDecimal getWalletValue() {
        log.info("getWalletValue");

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

        if (forceRefresh) {
            accountsService.readAccounts(true);
        }

        WalletHealth walletHealth;
        if (forceRefresh || walletHealthRepository.count() == Long.parseLong("0")) {
            BigDecimal buys = getWalletBuys();
            BigDecimal sells = getWalletSells();
            BigDecimal value = getWalletValue();
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
