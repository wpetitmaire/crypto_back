package org.willy.crypto.connexion.coinbase.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.willy.crypto.connexion.coinbase.objects.user.User;

public interface UserRepository extends JpaRepository<User, String> {
}
