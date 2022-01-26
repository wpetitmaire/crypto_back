package org.willy.crypto.connexion.coinbase.objects.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.Pagination;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransactionResponse {
    private Pagination pagination;
    private List<Transaction> data;
}
