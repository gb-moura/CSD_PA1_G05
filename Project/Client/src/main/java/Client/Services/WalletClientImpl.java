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


    private String BASE = "http://localhost:8080";


    String OBTAIN_MONEY = "/obtain";
    String TRANSFER_MONEY = "/transfer";
    String GET_MONEY = "/current";
    String GET_LEDGER = "/ledger";


    private RestTemplate restTemplate;


    @Autowired
    public WalletClientImpl(RestTemplateBuilder restTemplateBuilder, Environment env) {

        restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler()).rootUri("http://localhost:8080")
                .build();

    }

    @PostConstruct
    public void init() {
    }

    @Override
    public ResponseEntity<String>  obtainCoins(String toUser, Long amount) {
        Transaction transaction = new Transaction(toUser, amount);
        ResponseEntity<String> response = restTemplate.postForEntity(BASE + OBTAIN_MONEY, transaction, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String>  transferMoney(String fromUser, String toUser, Long amount) {
        Transaction transaction = new Transaction(fromUser, toUser, amount);
        ResponseEntity<String> response = restTemplate.postForEntity(BASE + TRANSFER_MONEY, transaction, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> currentAmount(String userID) {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE + GET_MONEY + "/" + userID, String.class);
        return response;
    }

    @Override
    public ResponseEntity<String> ledgerOfGlobalTransfers() {
        return getLedgerFromPath(BASE + GET_LEDGER);
    }

    @Override
    public ResponseEntity<String> LedgerOfClientTransfers(String userId) {
        ResponseEntity<String> response = getLedgerFromPath(BASE + GET_LEDGER + "/" + userId);
        return response;
    }

    private ResponseEntity<String> getLedgerFromPath(String path) {
        ResponseEntity<String> response = restTemplate.getForEntity(path, String.class);
        return response;
    }


}