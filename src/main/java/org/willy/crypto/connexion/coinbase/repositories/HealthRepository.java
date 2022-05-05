package org.willy.crypto.connexion.coinbase.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.willy.crypto.connexion.coinbase.objects.health.AccountHealth;

import java.util.List;

public interface HealthRepository extends JpaRepository<AccountHealth, String> {

    @Query("SELECT a from AccountHealth a where a.amount <> 0")
    List<AccountHealth> findAllNotEmptyAccounts();
}
