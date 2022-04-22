package org.willy.crypto.connexion.coinbase.objects.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.HashCB;
import org.willy.crypto.connexion.coinbase.objects.MoneyHashCB;

import javax.persistence.*;
import java.time.LocalDateTime;


/**
 *<strong>Coinbase transaction ressource</strong>
 * Description <a href="https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/api-transactions">Coinbase - transaction</a>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "transactioncb")
public class Transaction {

    /**
     * Resource ID
     */
    @Id
    private String id;

    /**
     * Transaction type
     */
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    /**
     * Amount in bitcoin, bitcoin cash, litecoin or ethereum
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "transaction_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "transaction_currency"))
    })
    private MoneyHashCB amount;

    /**
     * Amount in user's native currency
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "transaction_native_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "transaction_native_currency"))
    })
    private MoneyHashCB native_amount;

    /**
     * User defined description
     */
    private String description;

    private String created_at;
    private String updated_at;
    private String resource;
    private String resource_path;
    private Boolean instant_exchange;

//    @Embedded
//    @AttributeOverrides({
//            @AttributeOverride(name = "id", column = @Column(name = "buy_transaction_id")),
//            @AttributeOverride(name = "resource", @Column(name = "buy_transaction_ressource")),
//            @AttributeOverride(name = "resource_path", @Column(name = "buy_transaction_ressource_path"))
//    })
//    private HashCB buy;

    /**
     * Trade details for trade transactions
     */
    @Embedded
    private TradeTransaction trade;

    /**
     * Detailed information about the transaction
     */
    @Embedded
    private DetailTransaction details;

    /**
     * Information about bitcoin, bitcoin cash, litecoin or ethereum network including network transaction hash if transaction was on-blockchain. Only available for certain types of transactions
     */
    @Embedded
    private NetworkTransaction network;

    /**
     * 	The receiving party of a debit transaction. Usually another resource but can also be another type like email. Only available for certain types of transactions
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "transaction_to_party_id")),
            @AttributeOverride(name = "resource", column = @Column(name = "transaction_to_party_ressource")),
            @AttributeOverride(name = "resource_path", column = @Column(name = "transaction_to_party_ressource_path"))
    })
    private HashCB to;


    /**
     * The originating party of a credit transaction. Usually another resource but can also be another type like bitcoin network. Only available for certain types of transactions
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "transaction_from_id")),
            @AttributeOverride(name = "resource", column = @Column(name = "transaction_from_ressource")),
            @AttributeOverride(name = "resource_path", column = @Column(name = "transaction_from_ressource_path")),
            @AttributeOverride(name = "currency", column = @Column(name = "transaction_from_currency"))
    })
    private FromParty from;

    private LocalDateTime retrieve_date;
    private String associated_account_id;

}
