package org.willy.crypto.connexion.coinbase.objects.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.MoneyHashCB;
import org.willy.crypto.connexion.coinbase.objects.currency.CurrencyCB;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <strong>Account ressource from Coinbase</strong>
 * Description <a href="https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/api-accounts#show-an-account">Coinbase - account</a>
 * Composed of :
 * <ul>
 *     <li>A currency ressource {@link CurrencyCB}</li>
 *     <li>A currency ressource {@link MoneyHashCB}</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "accountcb")
public class AccountCB {


    /**
     * Resource ID
     */
    @Id
    private String id;

    /**
     * User or system defined name
     */
    private String name;

    /**
     * Primary account
     */
    @Column(name = "account_primary")
    private Boolean primary;

    /**
     * Account’s type. Available values: wallet, fiat, vault
     */
    @Column(name = "account_type")
    private String type;

    /**
     * Account’s currency
     */
    @Embedded
    @Column(name = "account_currency")
    private CurrencyCB currency;

    /**
     * Balance in BTC or ETH
     */
    @Embedded
    @Column(name = "account_balance")
    private MoneyHashCB balance;

    private String created_at;
    private String updated_at;
    private String resource;
    private String resource_path;
    private Boolean allow_deposits;
    private Boolean allow_withdrawals;

    /**
     * Date of the last retrieve
     */
    private LocalDateTime account_retrieve_date;
}
