package org.willy.crypto.connexion.coinbase.objects.health;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountHealth {
    String accountId;
    BigDecimal unitPrice;
    BigDecimal unitPriceDeltaVariation;
    BigDecimal amount;
    BigDecimal amountPrice;
    BigDecimal amountPriceDeltaVariation;
    List<PriceHistory> weekHistory;
}
