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
import org.willy.crypto.connexion.coinbase.objects.errors.ErrorResponseFromCB;
import org.willy.crypto.connexion.coinbase.objects.price.PriceCB;
import org.willy.crypto.connexion.coinbase.objects.price.PriceResponseCB;
import org.willy.crypto.helpers.gsonadapter.GsonLocalDateTime;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class CoinbasePriceService {
    final static Logger logger = LogManager.getLogger(CoinbasePriceService.class);
    final CoinbaseUserService userService;
    final CoinbaseConnexionService connexionService;

    public PriceCB getPrice(String baseCurrency) throws CoinbaseApiException {
        logger.info("Get price of {}", baseCurrency);
        String userNativeCurrency = userService.getUser().getNative_currency();
        String ressourceUrl = "/v2/prices/" + baseCurrency + "-" + userNativeCurrency + "/spot";
        HttpResponse<String> response = connexionService.getRequest(ressourceUrl);
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime()).create();

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new CoinbaseApiException(gson.fromJson(response.body(), ErrorResponseFromCB.class).getErrors().get(0).getMessage(), HttpStatus.NOT_FOUND);
        }

        return gson.fromJson(response.body(), PriceResponseCB.class).getData();
    }
}
