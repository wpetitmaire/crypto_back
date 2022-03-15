package org.willy.crypto.connexion.coinbase.objects.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.PaginationCB;

import java.util.List;

/**
 *<strong>Coinbase transaction list pagination ressource</strong>
 * Description <a href="https://developers.coinbase.com/api/v2?shell#list-buys">Coinbase - transaction list</a>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransactionResponse {

    /**
     * Coinbase pagination basic structure
     * @see PaginationCB
     */
    private PaginationCB pagination;

    /**
     * Coinbase transaction list
     * @see Transaction
     */
    private List<Transaction> data;
}
