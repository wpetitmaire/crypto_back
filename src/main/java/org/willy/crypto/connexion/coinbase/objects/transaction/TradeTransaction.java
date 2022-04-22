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
public class TradeTransaction {

    @Column(name = "trade_transaction_id")
    private String id;

    @Column(name = "trade_transaction_resource")
    private String resource;

    @Column(name = "trade_transaction_resource_path")
    private String resource_path;

}
