package org.willy.crypto.connexion.coinbase.objects.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.willy.crypto.connexion.coinbase.objects.PaginationCB;

import java.util.List;

/**
 * <strong>Account list response from Coinbase</strong>
 * To get list of Coinbase accounts with pagination.
 * <a href="https://docs.cloud.coinbase.com/sign-in-with-coinbase/docs/api-accounts">Coinbase - account list</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountsPaginationResponseCB {

    /**
     * Coinbase pagination basic structure
     * @see PaginationCB
     */
    private PaginationCB pagination;

    /**
     * List of accounts
     * @see AccountCB
     */
    private List<AccountCB> data;
}
