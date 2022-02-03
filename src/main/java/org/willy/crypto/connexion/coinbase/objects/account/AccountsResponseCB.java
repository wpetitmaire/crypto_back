package org.willy.crypto.connexion.coinbase.objects.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.PaginationCB;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountsResponseCB {
    private PaginationCB pagination;
    private List<AccountCB> data;
}
