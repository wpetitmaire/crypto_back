package org.willy.crypto.connexion.coinbase.objects.health;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.willy.crypto.connexion.coinbase.listeners.AccountListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "accounthealthcb")
public class AccountHealth {

    @Id
    String accountId;

    String accountName;
    BigDecimal unitPrice;
    BigDecimal unitPriceVariation;
    BigDecimal unitPriceVariationPourcentage;
    BigDecimal amount;
    BigDecimal amountPrice;

    @ElementCollection
    @CollectionTable(name = "my_weekHistory", joinColumns = @JoinColumn(name = "accountId"))
    @Column(name = "weekHistory")
    List<PriceHistory> weekHistory;

    String iconUrl;
}
