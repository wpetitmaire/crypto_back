package org.willy.crypto.connexion.coinbase.objects.health.output;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.willy.crypto.connexion.coinbase.objects.health.AccountHealth;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountHealthOuput {
    List<AccountHealth> data;
    LocalDateTime retrieve_time;
}
