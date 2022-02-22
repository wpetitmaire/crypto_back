package org.willy.crypto.connexion.coinbase.objects.errors;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorFromCB {
    String id;
    String message;
}
