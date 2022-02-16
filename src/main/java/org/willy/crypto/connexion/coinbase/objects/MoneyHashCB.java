package org.willy.crypto.connexion.coinbase.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Embeddable;

/**
 * <strong>Basic Coinbase MoneyHash structure</strong>
 * Description <a href="https://developers.coinbase.com/api/v2?shell#fields">Coinbase - fields</a>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class MoneyHashCB {

    /**
     * <strong>Amount of money</strong>
     * <p>Formatted with currency decimals</p>
     */
    private String amount;
    private String currency;
}
