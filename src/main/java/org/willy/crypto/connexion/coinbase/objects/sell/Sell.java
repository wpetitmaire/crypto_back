package org.willy.crypto.connexion.coinbase.objects.sell;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.HashCB;
import org.willy.crypto.connexion.coinbase.objects.MoneyHashCB;
import org.willy.crypto.connexion.coinbase.objects.buy.input.BuySellStatusFromCB;

import java.time.LocalDateTime;

/**
 * <h1>Sell ressource from Coinbase</h1>
 * Description <a href="https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/api-sells#show-a-sell">Coinbase - sell</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Sell {

    /**
     * Resource ID
     */
    private String id;

    /**
     * Status of the sell. Currently available values: created, completed, canceled
     */
    private BuySellStatusFromCB status;

    /**
     * Associated payment method (e.g. a bank, fiat account)
     */
    private HashCB payment_method;

    /**
     * Associated transaction (e.g. a bank, fiat account)
     */
    private HashCB transaction;

    /**
     * Amount in bitcoin, bitcoin cash, litecoin or ethereum
     */
    private MoneyHashCB amount;

    /**
     * Fiat amount with fees
     */
    private MoneyHashCB total;

    /**
     * Fiat amount without fees
     */
    private MoneyHashCB subtotal;

    /**
     * Fees associated to this sell
     */
    private MoneyHashCB fee;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    private final String resource = "sell";

    private String resource_path;

    /**
     * Has this sell been committed?
     */
    private Boolean committed;

    /**
     * Was this sell executed instantly?
     */
    private Boolean instant;

    /**
     * When a sell isn’t executed instantly, it will receive a payout date for the time it will be executed
     */
    private LocalDateTime payout_at;

}
