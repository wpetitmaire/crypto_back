package org.willy.crypto.connexion.coinbase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.willy.crypto.connexion.coinbase.objects.account.AccountCB;
import org.willy.crypto.connexion.coinbase.objects.account.AccountRepository;
import org.willy.crypto.connexion.coinbase.objects.account.AccountsResponseCB;
import org.willy.crypto.connexion.coinbase.objects.transaction.TransactionCB;
import org.willy.crypto.connexion.coinbase.objects.transaction.TransactionResponseCB;
import org.willy.crypto.helpers.gsonadapter.GsonLocalDateTime;

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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Scope("singleton")
public class CoinbaseApi {

   private final static String SECRET_KEY = System.getProperty("cbSecretKey");
   private final static String API_KEY = System.getProperty("cbApiKey");
   private final static String BASE_URL = "https://api.coinbase.com";
   private final static String API_VERSION = "";
   private final static Logger logger = LogManager.getLogger(CoinbaseApi.class);
   private final static HttpClient client = HttpClient.newHttpClient();

   private LocalDateTime accountsRetrieveDate;
   private final AccountRepository accountRepository;

   @Autowired
   public CoinbaseApi(AccountRepository accountRepository) {
      this.accountRepository = accountRepository;
   }

   /**
    * Return all Coinbase accounts
    * @return user accounts
    * @param refresh force refresh
    */
   public List<AccountCB> readAccounts(Boolean refresh) {
      logger.info("Read accounts");

      List<AccountCB> accountList = new ArrayList<>();

      LocalDateTime timeLimit = LocalDateTime.now().minus(1L, ChronoUnit.DAYS);
      if (accountsRetrieveDate != null && (accountsRetrieveDate.compareTo(timeLimit) > 0 || !refresh)) {
         return accountRepository.findAll();
      }

      // Start from scratch
      accountRepository.deleteAll();

      boolean isNextPage;
      String ressourceUrl = "/v2/accounts";
      HttpResponse<String> getRequestResponse;
      String response;
      AccountsResponseCB accountsResponse;
      Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime()).create();

      do {
         getRequestResponse = getRequest(ressourceUrl);
         response = getRequestResponse.body();
         accountsResponse = gson.fromJson(response, AccountsResponseCB.class);
         accountList.addAll(accountsResponse.getData());

         // If there is another page, change ressource url and do it again
         isNextPage = accountsResponse.getPagination().getNext_uri() != null;
         ressourceUrl = accountsResponse.getPagination().getNext_uri();
      } while (isNextPage);

      accountsRetrieveDate = LocalDateTime.now();

      // Check each account and keep only those who are/were used by the user
      accountList = accountList.stream()
              .filter(account -> thereIsTransactionsForTheAccount(account.getCurrency().getCode()))
              .peek(account -> account.setAccount_retrieve_date(accountsRetrieveDate))
              .collect(Collectors.toList());

      // save the result in the DB and save the retrieve data
      accountRepository.saveAll(accountList);

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
   public List<TransactionCB> readTransactionsOfAAccount(String accountId, boolean testIsATransaction) {
      logger.info("Read transactions from ressource : " + accountId + " - just test ? : " + testIsATransaction);

      List<TransactionCB> transactions = new ArrayList<>();
      boolean isNextPage;
      HttpResponse<String> getRequestResponse;
      String reponse;
      TransactionResponseCB transactionResponse;
      String ressourceUrl;

      if (testIsATransaction) { // retrieve only one transaction is enough for testing.
         ressourceUrl = "/v2/accounts/"+accountId+"/transactions?&limit=1";
      } else {
         ressourceUrl = "/v2/accounts/\"+accountId+\"/transactions";
      }

      do {
         getRequestResponse = getRequest(ressourceUrl);
         reponse = getRequestResponse.body();
         transactionResponse = new Gson().fromJson(reponse, TransactionResponseCB.class);
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

   public List<AccountCB> testCreation() {

//      CurrencyCB currencyCB = new CurrencyCB("BTC", "Bitcoin", "yellow", 1, 8, "crypto", "^((D|A|9)[a-km-zA-HJ-NP-Z1-9]{25,34})$",
//              "d9a3edfa-1be7-589c-bd20-c034f3830b60", null);
//
//      BalanceCB balanceCB = new BalanceCB("346.97111137", "DOGE");
//
//      AccountCB accountCB = new AccountCB("abbb1d77-a818-5393-b29d-45a38230d803", "Portefeuille en DOGE", true, "wallet",
//              currencyCB, balanceCB, "2021-08-15T13:51:07Z", "2021-08-21T09:31:43Z", "account", "/v2/accounts/abbb1d77-a818-5393-b29d-45a38230d803", true, true);
//
//      accountRepository.save(accountCB);

//      accountsRetrieveDate = Instant.now().minus(1L, ChronoUnit.DAYS);
//      Instant heureLimite = Instant.now().minus(6L, ChronoUnit.HOURS);
//
//      System.out.println("Heure derniere recherche : " + accountsRetrieveDate);
//      System.out.println("Heure limite : " + heureLimite);
//
//      int value = accountsRetrieveDate.compareTo(heureLimite);
//
//      if (value > 0)
//         System.out.println("accountsRetrieveDate is greater");
//      else if (value == 0)
//         System.out.println("accountsRetrieveDate is equal to heureLimite");
//      else
//         System.out.println("heureLimite is greater");





//      logger.info(accountsRetrieveDate);
//
//      if(accountsRetrieveDate != null && accountsRetrieveDate.compareTo(Instant.now().minus(6L, ChronoUnit.HOURS)) > 0) {
//         return accountRepository.findAll();
//      }

      return null;
   }
}
