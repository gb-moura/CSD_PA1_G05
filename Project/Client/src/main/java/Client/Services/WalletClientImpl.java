package Client.Services;

import Client.Handlers.RestTemplateHeaderModifierInterceptor;
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
import org.springframework.http.client.ClientHttpRequestInterceptor;
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
import java.util.LinkedList;
import java.util.List;

@Service
public class WalletClientImpl implements WalletClient {



    @Value("${client.server.url}")
    private String BASE;


    @Value("${token}")
    private String token;

    private static String WALLET_CONTROLLER =  "/money";
    private static String OBTAIN_MONEY = "/obtain";
    private static String TRANSFER_MONEY = "/transfer";
    private static String GET_MONEY = "/current";
    private static String GET_LEDGER = "/ledger";
    private static String INIT = "/createClient";


    private RestTemplate restTemplate;

    static {
        //For localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                (hostname,sslSession) -> {return true;});
    }
    @Autowired
    public WalletClientImpl(RestTemplateBuilder restTemplateBuilder, Environment env) throws ServerAnswerException {
        System.setProperty("javax.net.ssl.trustStore", env.getProperty("client.ssl.trust-store"));
        System.setProperty("javax.net.ssl.trustStorePassword",env.getProperty("client.ssl.trust-store-password"));
        restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler()).rootUri(BASE)
                .build();


    }

    @PostConstruct
    public void init() throws ServerAnswerException {


        List<ClientHttpRequestInterceptor> list = new LinkedList<>();
        list.add(new RestTemplateHeaderModifierInterceptor(token));
        restTemplate.setInterceptors(list);
        System.out.println("Tried to initialize the  Client with id: " + token);
        int r = createClient();
        if(r==0){
            System.out.println("This client already exists! Please try with another id!");
            System.exit(0);
        }
        System.out.println("Client with id: " + token + " created successfully");

    }

    @Override
    public int createClient() throws ServerAnswerException {
       String value =  new ExtractAnswer().extractAnswerPost(BASE + WALLET_CONTROLLER +INIT, token, restTemplate);
        return Integer.valueOf(value);
    }

    @Override
    public void  obtainCoins( Long amount) throws ServerAnswerException {
        Transaction transaction = new Transaction(token, amount);
        new ExtractAnswer().extractAnswerPost(BASE +WALLET_CONTROLLER + OBTAIN_MONEY, transaction, restTemplate);
    }

    @Override
    public void  transferMoney( String toUser, Long amount) throws ServerAnswerException {
        Transaction transaction = new Transaction(token, toUser, amount);
        new ExtractAnswer().extractAnswerPost(BASE +WALLET_CONTROLLER + TRANSFER_MONEY, transaction, restTemplate);
    }

    @Override
    public Long currentAmount(String token) throws ServerAnswerException {
        String longJson = new ExtractAnswer().extractAnswerGet(BASE +WALLET_CONTROLLER +  GET_MONEY + "/" + token, restTemplate);
        return Long.valueOf(longJson);
    }

    @Override
    public List<Transaction> ledgerOfGlobalTransfers() throws ServerAnswerException {
        return getLedgerFromPath(BASE +WALLET_CONTROLLER+ GET_LEDGER);
    }

    @Override
    public List<Transaction> LedgerOfClientTransfers() throws ServerAnswerException {
        return  getLedgerFromPath(BASE +WALLET_CONTROLLER+ GET_LEDGER + "/" + token);
    }

    private List<Transaction> getLedgerFromPath(String path) throws ServerAnswerException {

        String transactionsJson = new ExtractAnswer().extractAnswerGet(path, restTemplate);
        return Arrays.asList(new Gson().fromJson(transactionsJson, Transaction[].class));
    }


}