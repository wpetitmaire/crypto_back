package org.willy.crypto.connexion.coinbase.objects.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.Pagination;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountsResponse {
    private Pagination pagination;
    private List<Account> data;
}
