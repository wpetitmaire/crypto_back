package org.willy.crypto.icons.objects;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Icon {
    String name;
    String symbol;
    String slug;
    String img_url;
}
