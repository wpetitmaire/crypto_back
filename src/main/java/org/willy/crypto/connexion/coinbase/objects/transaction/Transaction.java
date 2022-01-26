package org.willy.crypto.connexion.coinbase.objects.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.balance.Balance;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transaction {
    private String id;
    private TransactionType type;
    private TransactionStatus status;
    private Balance amount;
    private Balance native_amount;
    private String description;
    private String created_at;
    private String updated_at;
    private String resource;
    private String resource_path;
    private BuyTransaction buy;
    private DetailsTransaction details;
    private NetworkTransaction network;
    private PartyTransaction to;
    private PartyTransaction from;

}
