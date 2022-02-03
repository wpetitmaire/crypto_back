package org.willy.crypto.connexion.coinbase.objects.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountCB, String> {
}
