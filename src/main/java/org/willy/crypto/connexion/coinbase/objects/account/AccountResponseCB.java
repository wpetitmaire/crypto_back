package org.willy.crypto.connexion.coinbase.objects.account;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponseCB {
    AccountCB data;
}
