package Client.Services;

import com.google.gson.Gson;
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

@RestController
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
        ResponseEntity<String> response = restTemplate.postForEntity(BASE +WALLET_CONTROLLER+ TRANSFER_MONEY, transaction, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> currentAmount(String userID) {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE +WALLET_CONTROLLER+ GET_MONEY + "/" + userID, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> ledgerOfGlobalTransfers() {
        return getLedgerFromPath(BASE +WALLET_CONTROLLER+ GET_LEDGER);
    }

    @Override
    public ResponseEntity<String> LedgerOfClientTransfers(String userId) {
        ResponseEntity<String> response = getLedgerFromPath(BASE +WALLET_CONTROLLER+ GET_LEDGER + "/" + userId);
        return response;
    }

    private ResponseEntity<String> getLedgerFromPath(String path) {
        ResponseEntity<String> response = restTemplate.getForEntity(path, String.class);
        return response;
    }


}