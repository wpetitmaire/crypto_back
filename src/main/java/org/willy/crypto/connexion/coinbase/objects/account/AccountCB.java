package org.willy.crypto.connexion.coinbase.objects.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.balance.BalanceCB;
import org.willy.crypto.connexion.coinbase.objects.currency.CurrencyCB;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "accountcb")
public class AccountCB {

    @Column(name = "account_id")
    @Id
    private String id;

    @Column(name = "account_name")
    private String name;

    @Column(name = "account_primary")
    private Boolean primary;

    @Column(name = "account_type")
    private String type;

    @Embedded
    @Column(name = "account_currency")
    private CurrencyCB currency;

    @Embedded
    @Column(name = "account_balance")
    private BalanceCB balance;

    private String created_at;
    private String updated_at;
    private String resource;
    private String resource_path;
    private Boolean allow_deposits;
    private Boolean allow_withdrawals;

    private LocalDateTime account_retrieve_date;
}
