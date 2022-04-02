package org.willy.crypto.icons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.willy.crypto.icons.objects.Icon;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@Log4j2
public class IconsService {

    List<Icon> icons;

    @PostConstruct
    public void transformFileToObject() {
        log.info("transformFileToObject");
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("coin_map.json");

        if (inputStream == null) {
            throw new IllegalArgumentException("Icons json file not found : ressources/coin_map.json");
        }

        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        Gson gson = new Gson();
        Type iconListType = new TypeToken<List<Icon>>(){}.getType();
        icons = gson.fromJson(reader, iconListType);
    }

    public String getIconUrl(String tokenId) {
        log.info("getIconUrl : {}", tokenId);

        if (icons.isEmpty())
            return null;

        Icon neededIcon = icons.stream().filter(icon -> icon.getSymbol().equals(tokenId)).findFirst().orElse(null);
        String url = neededIcon == null ? "" : neededIcon.getImg_url();

        log.info("url : {}", url);

        return url;
    }

}
