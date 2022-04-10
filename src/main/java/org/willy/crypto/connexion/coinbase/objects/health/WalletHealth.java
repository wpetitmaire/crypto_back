package org.willy.crypto.connexion.coinbase.objects.health;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

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
    BigDecimal walletBalance;

    /**
     * Total wallet amount value - buyTotal
     */
    BigDecimal walletBalanceWithoutFees;

    public WalletHealth(BigDecimal buyTotal, BigDecimal sellTotal, BigDecimal feeTotal, BigDecimal walletBalance, BigDecimal walletBalanceWithoutFees) {
        this.buyTotal = buyTotal;
        this.sellTotal = sellTotal;
        this.feeTotal = feeTotal;
        this.walletBalance = walletBalance;
        this.walletBalanceWithoutFees = walletBalanceWithoutFees;
    }
}
