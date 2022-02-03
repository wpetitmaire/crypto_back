package org.willy.crypto.connexion.coinbase.objects.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.balance.BalanceCB;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransactionCB {
    private String id;
    private TransactionTypeCB type;
    private TransactionStatusCB status;
    private BalanceCB amount;
    private BalanceCB native_amount;
    private String description;
    private String created_at;
    private String updated_at;
    private String resource;
    private String resource_path;
    private BuyTransactionCB buy;
    private DetailsTransactionCB details;
    private NetworkTransactionCB network;
    private PartyTransactionCB to;
    private PartyTransactionCB from;

}
