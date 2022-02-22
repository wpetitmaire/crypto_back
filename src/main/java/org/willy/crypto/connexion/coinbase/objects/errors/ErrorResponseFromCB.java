package org.willy.crypto.connexion.coinbase.objects.errors;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponseFromCB {
    List<ErrorFromCB> errors;
}
