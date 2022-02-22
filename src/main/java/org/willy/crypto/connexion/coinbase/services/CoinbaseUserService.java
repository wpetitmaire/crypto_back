package org.willy.crypto.connexion.coinbase.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.willy.crypto.connexion.coinbase.exceptions.CoinbaseApiException;
import org.willy.crypto.connexion.coinbase.objects.account.AccountCB;
import org.willy.crypto.connexion.coinbase.objects.buy.BuyResponseCB;
import org.willy.crypto.connexion.coinbase.objects.user.UserCB;
import org.willy.crypto.connexion.coinbase.objects.user.UserRepository;
import org.willy.crypto.connexion.coinbase.objects.user.UserResponseCB;
import org.willy.crypto.helpers.gsonadapter.GsonLocalDateTime;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@Scope("singleton")
public class CoinbaseUserService {

    final static Logger logger = LogManager.getLogger(CoinbaseUserService.class);
    final CoinbaseConnexionService connexionService;
    final UserRepository userRepository;

    public UserCB getUser() throws CoinbaseApiException {

        List<UserCB> users = userRepository.findAll();
        if (users.size() > 0) {
            return users.get(0);
        }

        final String ressourceUrl = "/v2/user";
        HttpResponse<String> response = connexionService.getRequest(ressourceUrl);
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime()).create();

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new CoinbaseApiException("User ressource not found.", HttpStatus.BAD_REQUEST);
        }

//        JsonObject debugStringResponse = gson.fromJson(response.body(), JsonObject.class);
//        logger.info(gson.toJson(debugStringResponse));

        UserCB user = gson.fromJson(response.body(), UserResponseCB.class).getData();

        userRepository.save(user);

        return user;
    }

}
