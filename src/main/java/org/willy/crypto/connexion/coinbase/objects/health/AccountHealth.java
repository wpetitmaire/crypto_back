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
    String accountName;
    BigDecimal unitPrice;
    BigDecimal unitPriceVariation;
    BigDecimal unitPriceVariationPourcentage;
    BigDecimal amount;
    BigDecimal amountPrice;
    BigDecimal amountPriceVariation;
    BigDecimal amountPriceVariationPourcentage;
    List<PriceHistory> weekHistory;
    String iconUrl;
}
