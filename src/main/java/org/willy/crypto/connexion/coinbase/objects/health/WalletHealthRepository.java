package org.willy.crypto.connexion.coinbase.objects.health;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WalletHealthRepository extends JpaRepository<WalletHealth, Long> {
}
