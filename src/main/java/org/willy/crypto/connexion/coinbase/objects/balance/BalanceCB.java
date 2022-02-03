package org.willy.crypto.connexion.coinbase.objects.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class BalanceCB {

    @Column(name = "balance_amount")
    private String amount;

    @Column(name = "balance_currency")
    private String currency;
}
