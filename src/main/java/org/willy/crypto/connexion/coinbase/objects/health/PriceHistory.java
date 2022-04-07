package org.willy.crypto.connexion.coinbase.objects.health;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
public class PriceHistory {
    String date;
    BigDecimal price;
}
