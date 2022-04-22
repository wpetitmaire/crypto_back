package org.willy.crypto.connexion.coinbase.objects.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Embeddable;

/**
 * <strong>Basic Coinbase Hash structure</strong>
 * Description <a href="https://developers.coinbase.com/api/v2?shell#fields">Coinbase - fields</a>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class FromParty {

    /**
     * Ressource UUID
     */
    private String id;

    /**
     * Represents the resource type
     */
    private String resource;

    /**
     * resource_path for the location under api.coinbase.com
     */
    private String resource_path;

    private String currency;
}
