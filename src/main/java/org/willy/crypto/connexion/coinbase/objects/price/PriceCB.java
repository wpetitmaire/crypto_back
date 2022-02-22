package org.willy.crypto.connexion.coinbase.objects.price;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceCB {
    String base;
    String currency;
    Float amount;
}
