package org.willy.crypto.connexion.coinbase.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.willy.crypto.connexion.coinbase.objects.account.AccountCB;

import javax.persistence.PrePersist;
import java.time.LocalDateTime;

public class AccountListener {
    private final static Logger logger = LogManager.getLogger(AccountListener.class);

    /**
     * Define the retrieve date before persist account
     * @param account
     */
    @PrePersist
    public void prePersist(AccountCB account) {
        account.setAccount_retrieve_date(LocalDateTime.now());
    }
}
