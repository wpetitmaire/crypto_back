package org.willy.crypto.connexion.coinbase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.willy.crypto.connexion.coinbase.objects.account.Account;
import org.willy.crypto.connexion.coinbase.objects.account.AccountsResponse;

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
import java.util.ArrayList;
import java.util.List;

@Service
@Scope("singleton")
public class CoinbaseApi {

   private final static String SECRET_KEY = System.getProperty("cbSecretKey");
   private final static String API_KEY = System.getProperty("cbApiKey");
   private final static String BASE_URL = "https://api.coinbase.com";
   private final static String API_VERSION_URL = "/v2";
   private final static String API_VERSION = "2021-06-03";
   private final static Logger logger = LogManager.getLogger(CoinbaseApi.class);
   private final static HttpClient client = HttpClient.newHttpClient();

   /**
    * Return all Coinbase accounts
    * @return
    * @throws NoSuchAlgorithmException
    * @throws IOException
    * @throws InvalidKeyException
    * @throws InterruptedException
    */
   public List<Account> readAccounts() throws NoSuchAlgorithmException, IOException, InvalidKeyException, InterruptedException {

      List<Account> accountList = new ArrayList<>();
      boolean isNextPage;
      String ressourceUrl = "/v2/accounts";
      HttpResponse<String> getRequestResponse;
      String response;
      AccountsResponse accountsResponse;

      do {
         getRequestResponse = getRequest(ressourceUrl);
         response = getRequestResponse.body();
         accountsResponse = new Gson().fromJson(response, AccountsResponse.class);
         accountList.addAll(accountsResponse.getData());

         // If there is another page, change ressource url and do it again
         isNextPage = accountsResponse.getPagination().getNext_uri() != null;
         ressourceUrl = accountsResponse.getPagination().getNext_uri();
      } while (isNextPage);

      logger.info("Account number : {}", accountList.size());

      return accountList;
   }

   /* REQUESTS METHODS */

   private HttpResponse<String> getRequest(String ressourceUrl) throws NoSuchAlgorithmException, InvalidKeyException, IOException, InterruptedException {
      return getRequest(ressourceUrl, "");
   }

   private HttpResponse<String> getRequest(String ressourceUrl, String payload) throws NoSuchAlgorithmException, InvalidKeyException, IOException, InterruptedException {

      final long timestamp = Instant.now().getEpochSecond();
      final String signature = getSignature(timestamp, "GET", ressourceUrl, "");

      HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create(BASE_URL + ressourceUrl))
              .header("CB-ACCESS-KEY", API_KEY)
              .header("CB-ACCESS-SIGN", signature)
              .header("CB-ACCESS-TIMESTAMP", String.valueOf(timestamp))
              .header("CB-VERSION", API_VERSION)
              .build();

      return client.send(request, HttpResponse.BodyHandlers.ofString());
   }

   /**
    * Create the encoded signature. Used for each request
    * @param timestamp number of seconds since Unix Epoch.
    * @param httpMethod
    * @param requestPath
    * @param payload
    * @return encoded signature
    * @throws NoSuchAlgorithmException
    * @throws InvalidKeyException
    */
   private String getSignature(long timestamp, String httpMethod, String requestPath, String payload) throws NoSuchAlgorithmException, InvalidKeyException {

      String prehash = timestamp + httpMethod.toUpperCase() + requestPath;

      if (httpMethod.toUpperCase().equals("POST") || httpMethod.toUpperCase().equals("PUT")) {
         prehash += payload;
      }

      Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
      hmacSHA256.init(secretKeySpec);
      byte[] hash = hmacSHA256.doFinal(prehash.getBytes());

      logger.info("Signature : " + Hex.encodeHexString(hash));

      return Hex.encodeHexString(hash);
   }

}
