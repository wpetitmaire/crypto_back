package org.willy.crypto.connexion.coinbase.objects.health;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "wallet_health")
public class WalletHealth {

    @Id
    @GeneratedValue
    Long id;

    BigDecimal buyTotal;
    BigDecimal sellTotal;
    BigDecimal feeTotal;

    /**
     * Total wallet amount value - buyTotal - fees
     */
    BigDecimal health;

    /**
     * Total wallet amount value - buyTotal
     */
    BigDecimal healthWithoutFees;

    /**
     * Current wallet balance
     */
    BigDecimal currentBalance;

    /**
     * Wallet balance of yesterday
     */
    BigDecimal yesterdayBalance;

    BigDecimal balanceDayEvolution;

    LocalDate atDate;

    public WalletHealth(BigDecimal buyTotal, BigDecimal sellTotal, BigDecimal feeTotal, BigDecimal health, BigDecimal healthWithoutFees, BigDecimal currentBalance, BigDecimal yesterdayBalance, BigDecimal balanceDayEvolution) {
        this.buyTotal = buyTotal;
        this.sellTotal = sellTotal;
        this.feeTotal = feeTotal;
        this.health = health;
        this.healthWithoutFees = healthWithoutFees;
        this.currentBalance = currentBalance;
        this.yesterdayBalance = yesterdayBalance;
        this.balanceDayEvolution = balanceDayEvolution;
    }
}
