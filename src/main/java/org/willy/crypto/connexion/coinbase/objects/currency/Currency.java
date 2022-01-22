package org.willy.crypto.connexion.coinbase.objects.currency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Currency {
    private String code;
    private String name;
    private String color;
    private int sort_index;
    private int exponent;
    private String type;
    private String address_regex;
    private String asset_id;
    private String slugs;
}
