package org.willy.crypto.connexion.coinbase.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Pagination {
    private String ending_before;
    private String starting_after;
    private String previous_ending_before;
    private String next_starting_after;
    private int limit;
    private String order;
    private String previous_uri;
    private String next_uri;
}
