package org.willy.crypto.connexion.coinbase.objects.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Balance {
    private String amount;
    private String currency;
}
