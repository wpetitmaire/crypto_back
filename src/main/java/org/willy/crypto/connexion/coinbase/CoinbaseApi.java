package org.willy.crypto.connexion.coinbase;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.willy.crypto.connexion.coinbase.objects.account.Account;
import org.willy.crypto.connexion.coinbase.objects.account.AccountsResponse;
import org.willy.crypto.connexion.coinbase.objects.transaction.Transaction;
import org.willy.crypto.connexion.coinbase.objects.transaction.TransactionResponse;

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
import java.util.stream.Collectors;

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

   private List<Account> userAccounts = null;
   private Instant userAccountsRefreshDate = null;

   public List<Account> getStockedAccounts() {
      return userAccounts;
   }

   /**
    * Return all Coinbase accounts
    * @return user accounts
    */
   public List<Account> readAccounts() throws NoSuchAlgorithmException, IOException, InvalidKeyException, InterruptedException {
      logger.info("Read accounts");
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

      // Check each account and keep only those who are/were used by the user
      accountList = accountList.stream()
              .filter(account -> thereIsTransactionsForTheAccount(account.getCurrency().getCode()))
              .collect(Collectors.toList());

      userAccounts = accountList;
      userAccountsRefreshDate = Instant.now();

      accountList.forEach(account -> System.out.println("NAME : " + account.getCurrency().getName() + " - CODE : " + account.getCurrency().getCode()));

      return accountList;
   }

   public boolean thereIsTransactionsForTheAccount(String accountId){
      logger.info("thereIsTransactionsForTheAccount : " + accountId);

      return readTransactionsOfAAccount(accountId, true).size() > 0;
   }

   //v2/accounts/:account_id/transactions

   /**
    * Get the list of the transactions for an account
    * @param accountId Id of the account
    * @param testIsATransaction true if we just need to know if it exists at least one transaction
    * @return List of transactions
    */
   public List<Transaction> readTransactionsOfAAccount(String accountId, boolean testIsATransaction) {
      logger.info("Read transactions from ressource : " + accountId + " - just test ? : " + testIsATransaction);

      List<Transaction> transactions = new ArrayList<>();
      boolean isNextPage;
      HttpResponse<String> getRequestResponse;
      String reponse;
      TransactionResponse transactionResponse;
      String ressourceUrl;

      if (testIsATransaction) { // retrieve only one transaction is enough for testing.
         ressourceUrl = "/v2/accounts/"+accountId+"/transactions?&limit=1";
      } else {
         ressourceUrl = "/v2/accounts/\"+accountId+\"/transactions";
      }

      do {
         getRequestResponse = getRequest(ressourceUrl);
         reponse = getRequestResponse.body();
         transactionResponse = new Gson().fromJson(reponse, TransactionResponse.class);
         transactions.addAll(transactionResponse.getData());

         // If there is another page, change ressource url and do it again
         if (!testIsATransaction) {
            isNextPage = transactionResponse.getPagination().getNext_uri() != null;
            ressourceUrl = transactionResponse.getPagination().getNext_uri();
         }
         else
            isNextPage = false;
      } while (isNextPage);

      logger.debug("Transaction number : {}", transactions.size());

      return transactions;
   }

   /* REQUESTS METHODS */

   /**
    * Send a GET request
    * @param resourceUrl API resource path
    * @return get request response
    */
   private HttpResponse<String> getRequest(String resourceUrl) {

      final long timestamp = Instant.now().getEpochSecond();
      final String signature = getSignature(timestamp, HttpMethod.GET, resourceUrl, "");

      HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create(BASE_URL + resourceUrl))
              .header("CB-ACCESS-KEY", API_KEY)
              .header("CB-ACCESS-SIGN", signature)
              .header("CB-ACCESS-TIMESTAMP", String.valueOf(timestamp))
              .header("CB-VERSION", API_VERSION)
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
   private String getSignature(long timestamp, HttpMethod httpMethod, String resourcePath, String payload) {

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

      SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
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
