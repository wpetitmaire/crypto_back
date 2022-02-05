package org.willy.crypto.connexion.coinbase.objects.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Embeddable
public class BuyTransactionCB {

    @Column(name = "buy_transaction_id")
    private String id;

    @Column(name = "buy_transaction_ressource")
    private String resource;

    @Column(name = "buy_transaction_ressource_path")
    private String resource_path;
}
