package org.willy.crypto.connexion.coinbase.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.willy.crypto.connexion.coinbase.objects.health.WalletHealth;

public interface WalletHealthRepository extends JpaRepository<WalletHealth, Long> {
}
