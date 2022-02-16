package org.willy.crypto.connexion.coinbase.objects.buy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.PaginationCB;

import java.util.List;

/**
 *<strong>Coinbase buy list pagination ressource</strong>
 * Description <a href="https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/api-buys">Coinbase - buy list</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BuyResponseCB {

    /**
     * Coinbase pagination basic structure
     * @see PaginationCB
     */
    private PaginationCB pagination;

    /**
     * Coinbase buy list
     * @see BuyCB
     */
    private List<BuyCB> data;
}
