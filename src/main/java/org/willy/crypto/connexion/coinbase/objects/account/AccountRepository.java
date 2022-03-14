package org.willy.crypto.connexion.coinbase.objects.account;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountCB, String> {

    @Query("SELECT a from AccountCB a where a.type <> 'fiat'")
    List<AccountCB> findAllNoneFiatAccounts(Sort sort);
}
