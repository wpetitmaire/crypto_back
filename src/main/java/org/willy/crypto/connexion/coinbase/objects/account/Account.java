package org.willy.crypto.connexion.coinbase.objects.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.balance.Balance;
import org.willy.crypto.connexion.coinbase.objects.currency.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Account {
    private String id;
    private String name;
    private boolean primary;
    private String type;
    private Currency currency;
    private Balance balance;
    private String created_at;
    private String updated_at;
    private String resource;
    private String resource_path;
    private boolean allow_deposits;
    private boolean allow_withdrawals;
}
