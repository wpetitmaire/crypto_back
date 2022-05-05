package org.willy.crypto.connexion.coinbase.objects.price.input;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.willy.crypto.connexion.coinbase.objects.price.Price;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceResponseFromCB {
    Price data;
}
