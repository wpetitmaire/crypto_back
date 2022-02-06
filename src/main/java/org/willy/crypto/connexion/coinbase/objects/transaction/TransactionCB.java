package org.willy.crypto.connexion.coinbase.objects.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.balance.BalanceCB;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table
public class TransactionCB {

    @Id
    private String id;
    private TransactionTypeCB type;
    private TransactionStatusCB status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "transaction_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "transaction_currency"))
    })
    private BalanceCB amount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "transaction_native_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "transaction_native_currency"))
    })
    private BalanceCB native_amount;

    private String description;
    private String created_at;
    private String updated_at;
    private String resource;
    private String resource_path;

    @Embedded
    private BuyTransactionCB buy;

    @Embedded
    private DetailTransactionCB details;

    @Embedded
    private NetworkTransactionCB network;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "transaction_to_party_id")),
            @AttributeOverride(name = "resource", column = @Column(name = "transaction_to_party_ressource")),
            @AttributeOverride(name = "resource_path", column = @Column(name = "transaction_to_party_ressource_path"))
    })
    private PartyTransactionCB to;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "transaction_from_party_id")),
            @AttributeOverride(name = "resource", column = @Column(name = "transaction_from_party_ressource")),
            @AttributeOverride(name = "resource_path", column = @Column(name = "transaction_from_party_ressource_path"))
    })
    private PartyTransactionCB from;

    private LocalDateTime retrieve_date;
    private String associated_account_id;

}
