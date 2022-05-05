package org.willy.crypto.connexion.coinbase.objects.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_cb")
public class User {
    @Id
    String id;

    String name;
    String native_currency;
    String avatar_url;
}
