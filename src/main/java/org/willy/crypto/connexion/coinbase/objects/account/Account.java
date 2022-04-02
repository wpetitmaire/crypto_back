package org.willy.crypto.connexion.coinbase.objects.account;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.willy.crypto.connexion.coinbase.listeners.AccountListener;
import org.willy.crypto.connexion.coinbase.objects.MoneyHashCB;
import org.willy.crypto.connexion.coinbase.objects.currency.Currency;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <strong>Account ressource from Coinbase</strong>
 * Description <a href="https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/api-accounts#show-an-account">Coinbase - account</a>
 * Composed of :
 * <ul>
 *     <li>A currency ressource {@link Currency}</li>
 *     <li>A currency ressource {@link MoneyHashCB}</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EntityListeners(AccountListener.class)
@Table(name = "accountcb")
public class Account {


    /**
     * Resource ID
     */
    @Id
    String id;

    /**
     * User or system defined name
     */
    String name;

    /**
     * Primary account
     */
    @Column(name = "account_primary")
    Boolean primary;

    /**
     * Account’s type. Available values: wallet, fiat, vault
     */
    @Column(name = "account_type")
    String type;

    /**
     * Account’s currency
     */
    @Embedded
    @Column(name = "account_currency")
    Currency currency;

    /**
     * Balance in BTC or ETH
     */
    @Embedded
    @Column(name = "account_balance")
    MoneyHashCB balance;

    String created_at;
    String updated_at;
    String resource;
    String resource_path;
    Boolean allow_deposits;
    Boolean allow_withdrawals;

    /**
     * Date of the last retrieve
     */
    LocalDateTime account_retrieve_date;

    /**
     * Url to get the icon file of the account
     */
    String iconUrl;
}
