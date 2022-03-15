package org.willy.crypto.connexion.coinbase.objects.price;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Price {
    String base;
    String currency;
    BigDecimal amount;
}
