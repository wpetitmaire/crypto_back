package org.willy.crypto.connexion.coinbase.objects.user.input;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.willy.crypto.connexion.coinbase.objects.user.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseFromCB {
    User data;
}
