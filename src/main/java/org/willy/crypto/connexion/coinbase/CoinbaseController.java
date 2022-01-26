package org.willy.crypto.connexion.coinbase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.willy.crypto.connexion.coinbase.objects.account.Account;
import org.willy.crypto.connexion.coinbase.objects.account.AccountsResponse;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/coinbase")
public class CoinbaseController {

    @Autowired
    CoinbaseApi api;

    @GetMapping(path = "/accounts")
    public List<Account> readAccounts() throws NoSuchAlgorithmException, IOException, InvalidKeyException, InterruptedException {
        return api.readAccounts();
    }

    @GetMapping(path = "/sotcked_accounts")
    public List<Account> getAccounts() {
        return api.getStockedAccounts();
    }

}
