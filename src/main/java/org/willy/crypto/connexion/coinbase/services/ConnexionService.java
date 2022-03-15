package org.willy.crypto.connexion.coinbase.services;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@Scope("singleton")
public class ConnexionService {

    public final static String SECRET_KEY = System.getProperty("cbSecretKey");
    public final static String API_KEY = System.getProperty("cbApiKey");
    public final static String BASE_URL = "https://api.coinbase.com";
    private final static HttpClient client = HttpClient.newHttpClient();
    private final static Logger logger = LogManager.getLogger(ConnexionService.class);

    /* REQUESTS METHODS */
    public HttpResponse<String> getRequest(String resourceUrl) {
        return getRequest(resourceUrl, null);
    }

    /**
     * Send a GET request
     * @param resourceUrl API resource path
     * @return get request response
     */
    public HttpResponse<String> getRequest(String resourceUrl, HashMap<String,String> queryParameters) {

        final long timestamp = Instant.now().getEpochSecond();
        final String signature = getSignature(timestamp, HttpMethod.GET, resourceUrl, "");

        if (queryParameters != null && !queryParameters.isEmpty()) {
            Iterator<Map.Entry<String, String>> it = queryParameters.entrySet().iterator();
            StringBuilder resourceUrlBuilder = new StringBuilder(resourceUrl);
            while (it.hasNext()){
                Map.Entry<String, String> elem = it.next();
                resourceUrlBuilder.append("?").append(elem.getKey()).append("=").append(elem.getValue());
            }
            resourceUrl = resourceUrlBuilder.toString();
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ConnexionService.BASE_URL + resourceUrl))
                .header("CB-ACCESS-KEY", ConnexionService.API_KEY)
                .header("CB-ACCESS-SIGN", signature)
                .header("CB-ACCESS-TIMESTAMP", String.valueOf(timestamp))
                .header("CB-VERSION", "2021-06-03")
                .header("Accept-Language", "fr")
                .build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error sending request to CB");
        }
    }

    /**
     * Create the encoded signature. Used for each request
     * @param timestamp number of seconds since Unix Epoch.
     * @param httpMethod HttpMethod
     * @param resourcePath api ressource path
     * @param payload request body
     * @return encoded signature
     */
    public String getSignature(long timestamp, HttpMethod httpMethod, String resourcePath, String payload) {

        String prehash = timestamp + httpMethod.toString() + resourcePath;

        if (httpMethod.equals(HttpMethod.POST) || httpMethod.equals(HttpMethod.PUT)) {
            prehash += payload;
        }

        Mac hmacSHA256 = null;
        try {
            hmacSHA256 = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Algorithm HmacSHA256 not found in Mac.", e);
        }

        SecretKeySpec secretKeySpec = new SecretKeySpec(ConnexionService.SECRET_KEY.getBytes(), "HmacSHA256");
        try {
            hmacSHA256.init(secretKeySpec);
        } catch (InvalidKeyException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "invalid secretKey.", e);
        }
        byte[] hash = hmacSHA256.doFinal(prehash.getBytes());

        logger.debug("Signature : " + Hex.encodeHexString(hash));

        return Hex.encodeHexString(hash);
    }
}
