package org.willy.crypto.connexion.coinbase.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.willy.crypto.connexion.coinbase.objects.transaction.Transaction;

import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query("SELECT t FROM Transaction t WHERE t.amount.currency <> 'EUR'")
    List<Transaction> findAllNoneFiatCurrencyTransactions();

    @Query("SELECT t FROM Transaction t WHERE t.type <> 'BUY' AND t.type <> 'TRADE' AND t.amount.currency = ?1 AND t.from.id IS NOT NULL ")
    List<Transaction> findAllPositivesTransactions(String currencyId);
}
