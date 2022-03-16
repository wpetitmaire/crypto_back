package org.willy.crypto.connexion.coinbase.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.willy.crypto.connexion.coinbase.exceptions.CoinbaseApiException;
import org.willy.crypto.connexion.coinbase.objects.errors.ErrorResponseFrom;
import org.willy.crypto.connexion.coinbase.objects.price.Price;
import org.willy.crypto.connexion.coinbase.objects.price.PriceResponse;
import org.willy.crypto.helpers.gsonadapter.GsonLocalDateTime;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class PriceService {
    final static Logger logger = LogManager.getLogger(PriceService.class);
    final UserService userService;
    final ConnexionService connexionService;

    String currentUserNativeCurrency = null;

    public Price getPrice(String baseCurrency) throws CoinbaseApiException {
        return getPrice(baseCurrency, DateTimeFormatter.ofPattern("uuuu-MM-dd").format(LocalDate.now()));
    }

    public Price getPrice(String baseCurrency, LocalDate date) throws CoinbaseApiException {
        return getPrice(baseCurrency, DateTimeFormatter.ofPattern("uuuu-MM-dd").format(date));
    }

    public Price getPrice(String baseCurrency, String date) throws CoinbaseApiException {
        logger.info("Get price of {} at {}", baseCurrency, date);

        String userNativeCurrency = null;
        if (currentUserNativeCurrency == null) {
            currentUserNativeCurrency = userService.getUser().getNative_currency();
            userNativeCurrency = currentUserNativeCurrency;
        } else {
            userNativeCurrency = currentUserNativeCurrency;
        }

        String ressourceUrl = "/v2/prices/" + baseCurrency + "-" + userNativeCurrency + "/spot";

        HttpResponse<String> response;
        if (date != null) {
            HashMap<String,String> parameters = new HashMap<>() {{ put("date", date); }};
            response = connexionService.getRequest(ressourceUrl, parameters);
        } else {
            response = connexionService.getRequest(ressourceUrl);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime()).create();

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new CoinbaseApiException(gson.fromJson(response.body(), ErrorResponseFrom.class).getErrors().get(0).getMessage(), HttpStatus.NOT_FOUND);
        }

        return gson.fromJson(response.body(), PriceResponse.class).getData();
    }
}
