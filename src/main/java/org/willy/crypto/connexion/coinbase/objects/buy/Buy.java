package org.willy.crypto.connexion.coinbase.objects.buy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.HashCB;
import org.willy.crypto.connexion.coinbase.objects.MoneyHashCB;

import java.time.LocalDateTime;

/**
 *<strong>Coinbase Buy ressource</strong>
 * Description <a href="https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/api-buys#show-a-buy">Coinbase - buy</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Buy {
    /**
     * Resource ID
     */
    private String id;

    /**
     * Status of the buy. Currently available values: created, completed, canceled
     */
    private BuySellStatus status;

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
     * Fee associated to this buy
     */
    private MoneyHashCB fee;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String resource;
    private String resource_path;

    /**
     * Has this buy been committed?
     */
    private Boolean committed;

    /**
     * Was this buy executed instantly?
     */
    private Boolean instant;

    /**
     * When a buy isn’t executed instantly, it will receive a payout date for the time it will be executed
     */
    private String payout_at;
}
