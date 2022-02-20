package org.willy.crypto.connexion.coinbase.objects.errors;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiError {
    LocalDateTime dateTime;
    String message;
}
