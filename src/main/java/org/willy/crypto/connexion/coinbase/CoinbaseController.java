package org.willy.crypto.connexion.coinbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.willy.crypto.connexion.coinbase.objects.account.AccountCB;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/coinbase")
public class CoinbaseController {

    Logger logger = LoggerFactory.getLogger(CoinbaseController.class);

    private final CoinbaseApi api;

    @Autowired
    public CoinbaseController(CoinbaseApi coinbaseApi) { this.api = coinbaseApi; }

    @GetMapping(path = "/accounts")
    public List<AccountCB> readAccounts(@RequestParam(required = false) Boolean refresh) {

        logger.info(String.valueOf(refresh));

        if (refresh == null) {
            refresh = false;
        }
        return api.readAccounts(refresh);
    }

    @GetMapping(path = "/test")
    public List<AccountCB> testSave() {
        return api.testCreation();
    }
}
