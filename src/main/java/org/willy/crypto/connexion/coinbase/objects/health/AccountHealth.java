package org.willy.crypto.connexion.coinbase.objects.health;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "account_health")
public class AccountHealth {

    @Id
    String accountId;

    String accountName;
    BigDecimal unitPrice;
    BigDecimal unitPriceVariation;
    BigDecimal unitPriceVariationPourcentage;
    BigDecimal amount;
    BigDecimal amountPrice;
    BigDecimal amountYesterdayPrice;

    @ElementCollection
    @CollectionTable(name = "my_week_history", joinColumns = @JoinColumn(name = "accountId"))
    @Column(name = "weekHistory")
    List<PriceHistory> weekHistory;

    String iconUrl;
    BigDecimal health;
    BigDecimal earns;
}
