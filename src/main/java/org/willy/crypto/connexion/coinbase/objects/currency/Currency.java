package org.willy.crypto.connexion.coinbase.objects.currency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Embeddable
public class Currency {

    @Column(name = "currency_code")
    private String code;

    @Column(name = "currency_name")
    private String name;

    private String color;
    private int sort_index;
    private int exponent;

    @Column(name = "currency_type")
    private String type;

    private String address_regex;
    private String asset_id;
    private String slugs;
}
