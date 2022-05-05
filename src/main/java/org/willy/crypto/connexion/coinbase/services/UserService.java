package org.willy.crypto.connexion.coinbase.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.willy.crypto.connexion.coinbase.exceptions.CoinbaseApiException;
import org.willy.crypto.connexion.coinbase.objects.user.User;
import org.willy.crypto.connexion.coinbase.objects.user.input.UserResponseFromCB;
import org.willy.crypto.connexion.coinbase.repositories.UserRepository;
import org.willy.crypto.helpers.gsonadapter.GsonLocalDateTime;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
@Service
@Scope("singleton")
public class UserService {

    final ConnexionService connexionService;
    final UserRepository userRepository;

    public User getUser() throws CoinbaseApiException {
        log.info("getUser");
        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
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

        User user = gson.fromJson(response.body(), UserResponseFromCB.class).getData();

        userRepository.save(user);

        return user;
    }

}
