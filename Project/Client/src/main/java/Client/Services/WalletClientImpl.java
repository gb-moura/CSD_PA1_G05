package Client.Services;

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
    String GET_CLIENT_LEDGER = GET_LEDGER + "/{id}";

    private RestTemplate restTemplate;





    @Autowired
    public WalletClientImpl(RestTemplateBuilder restTemplateBuilder,Environment env) {

        restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler()).rootUri("http://localhost:8080")
                .build();

    }

    @PostConstruct
    public void init() {
    }

    @Override
    public void obtainCoins(String toUser, Long amount)  {
        Transaction transaction = new Transaction(toUser,amount);
        ResponseEntity<String> response = restTemplate.postForEntity(BASE+OBTAIN_MONEY, transaction, String.class);

    }

    @Override
    public void transferMoney(String fromUser, String toUser, Long amount)  {
        Transaction transaction = new Transaction(fromUser,toUser,amount);
        ResponseEntity<String> response = restTemplate.postForEntity(BASE + TRANSFER_MONEY, transaction, String.class);

    }

    @Override
    public void currentAmount(String userID) {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE + GET_MONEY + userID, String.class);

    }

    @Override
   public void ledgerOfGlobalTransfers() {
       getLedgerFromPath(BASE+GET_LEDGER);


    }

    @Override
   public void LedgerOfClientTransfers(String userId)  {
       getLedgerFromPath(BASE + GET_LEDGER + userId);
    }

    private void getLedgerFromPath(String path)  {
        ResponseEntity<String> response = restTemplate.getForEntity(path, String.class);
        System.out.println(response.getBody().toString());


    }

}