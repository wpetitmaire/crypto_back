package org.willy.crypto.connexion.coinbase.objects.account.input;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.willy.crypto.connexion.coinbase.objects.account.Account;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponseFromCB {
    Account data;
}
