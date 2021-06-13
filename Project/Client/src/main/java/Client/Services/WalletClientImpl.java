package Client.Services;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import Client.Handlers.RestTemplateResponseErrorHandler;
import Client.Util.Transaction;
import Client.Exceptions.ServerAnswerException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class WalletClientImpl implements WalletClient {



    @Value("${client.server.url}")
    private String BASE;

    private static String WALLET_CONTROLLER =  "/money";
    private static String OBTAIN_MONEY = "/obtain";
    private static String TRANSFER_MONEY = "/transfer";
    private static String GET_MONEY = "/current";
    private static String GET_LEDGER = "/ledger";


    private RestTemplate restTemplate;

    static {
        //For localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                (hostname,sslSession) -> {return true;});
    }
    @Autowired
    public WalletClientImpl(RestTemplateBuilder restTemplateBuilder, Environment env) {
        System.setProperty("javax.net.ssl.trustStore", env.getProperty("client.ssl.trust-store"));
        System.setProperty("javax.net.ssl.trustStorePassword",env.getProperty("client.ssl.trust-store-password"));
        restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler()).rootUri(BASE)
                .build();

    }

    @PostConstruct
    public void init() {
    }

    @Override
    public ResponseEntity<String>  obtainCoins(String toUser, Long amount) {
        Transaction transaction = new Transaction(toUser, amount);
        System.out.println(BASE+OBTAIN_MONEY);
        ResponseEntity<String> response = restTemplate.postForEntity(BASE +WALLET_CONTROLLER+ OBTAIN_MONEY, transaction, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String>  transferMoney(String fromUser, String toUser, Long amount) {
        Transaction transaction = new Transaction(fromUser, toUser, amount);
        ResponseEntity<String> response = restTemplate.postForEntity( BASE +WALLET_CONTROLLER+ TRANSFER_MONEY, transaction, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> currentAmount(String userID) {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE +WALLET_CONTROLLER+ GET_MONEY + "/" + userID, String.class);
        return response;
    }

    @Override
    public List<Transaction> ledgerOfGlobalTransfers() throws ServerAnswerException {
        return getLedgerFromPath(BASE +WALLET_CONTROLLER+ GET_LEDGER);
    }

    @Override
    public List<Transaction> LedgerOfClientTransfers(String userId) throws ServerAnswerException {
       // System.out.println("client ledger: " + BASE +WALLET_CONTROLLER+ GET_LEDGER + "/" + userId);
        return  getLedgerFromPath(BASE +WALLET_CONTROLLER+ GET_LEDGER + "/" + userId);
    }

    private List<Transaction> getLedgerFromPath(String path) throws ServerAnswerException {
      /*  System.out.println(path);
        Transaction[] transactions = null;
        ResponseEntity<String> response = restTemplate.getForEntity(path, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("ERRO CODE      "+response.getStatusCode());
            JSONObject obj = new JSONObject(response.getBody());
            String message = (String) obj.get("message");
            throw new ServerAnswerException(message);
        }else {
            transactions =new Gson().fromJson(response.getBody(), Transaction[].class);

        }

        return Arrays.asList(transactions);*/

        ResponseEntity<String> response = restTemplate.getForEntity(path, String.class);
        Transaction[] transactions = new HandleServerAnswer<Transaction[]>().
                processServerAnswer(response, Transaction[].class);
        return Arrays.asList(transactions);
    }


}