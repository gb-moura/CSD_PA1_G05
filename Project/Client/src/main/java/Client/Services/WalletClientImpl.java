package Client.Services;

import Client.Exceptions.ServerAnswerException;
import Client.Handlers.RestTemplateHeaderModifierInterceptor;
import Client.Handlers.RestTemplateResponseErrorHandler;
import Client.Util.Block;
import Client.Util.ITransaction;
import Client.Util.SmartContract;
import Client.Util.Transaction;
import bftsmart.tom.util.TOMUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.glassfish.jersey.internal.guava.Maps;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.security.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Service
public class WalletClientImpl implements WalletClient {


    @Value("${client.server.url}")
    private String BASE;


    @Value("${token}")
    private String token;

    private static final String WALLET_CONTROLLER = "/money";
    private static final String OBTAIN_MONEY = "/obtain";
    private static final String TRANSFER_MONEY = "/transfer";
    private static final String GET_MONEY = "/current";
    private static final String GET_LEDGER = "/ledger";
    private static final String INIT = "/createClient";
    private static final String OBTAIN_LAST_MINED_BLOCK = "/obtainlastminedblock";
    private static final String PICK_NOT_MIN_TRANS = "/picknotminedtransactions";
    private static final String MINE_BLOCK = "/mineblock";
    private static final String TRANSFER_MONEY_SMRCONTRACT = "/transferwithsmr";

    private Block blockReceived;

    PrivateKey privateKey;
    PublicKey publicKey;

    private RestTemplate restTemplate;

    static {
        //For localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                (hostname, sslSession) -> {
                    return true;
                });
    }

    @Autowired
    public WalletClientImpl(RestTemplateBuilder restTemplateBuilder, Environment env) throws ServerAnswerException {
        System.setProperty("javax.net.ssl.trustStore", env.getProperty("client.ssl.trust-store"));
        System.setProperty("javax.net.ssl.trustStorePassword", env.getProperty("client.ssl.trust-store-password"));
        restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler()).rootUri(BASE)
                .build();


    }

    @PostConstruct
    public void init() throws ServerAnswerException, NoSuchAlgorithmException {


        List<ClientHttpRequestInterceptor> list = new LinkedList<>();
        list.add(new RestTemplateHeaderModifierInterceptor(token));
        restTemplate.setInterceptors(list);
        System.out.println("Tried to initialize the  Client with id: " + token);
        int r = createClient();
        if (r == 0) {
            System.out.println("This client already exists! Please try with another id!");
            System.exit(0);
        }
        System.out.println("Client with id: " + token + " created successfully");

    }

    @Override
    public int createClient() throws ServerAnswerException, NoSuchAlgorithmException {
        KeyPairGenerator c = KeyPairGenerator.getInstance("RSA");
        c.initialize(2048);
        KeyPair k = c.genKeyPair();
        privateKey = k.getPrivate();
        publicKey = k.getPublic();
        byte[] key = publicKey.getEncoded();
        Map.Entry<byte[], String> clientEntry = Maps.immutableEntry(key, token);
        System.out.println("CHEGUEI AQUI");
        //criar entry com a chave publica e o tokene enviar para o servidor
        Object value = new ExtractAnswer().extractAnswerPost(BASE + WALLET_CONTROLLER + INIT, clientEntry, restTemplate);
        return Integer.parseInt(value.toString());
    }

    @Override
    public void obtainCoins(Long amount) throws ServerAnswerException, NoSuchAlgorithmException {
        ITransaction transaction = new Transaction(token, amount);

        byte[] byteTransaction = new Gson().toJson(transaction).getBytes();
        byte[] sign = TOMUtil.signMessage(privateKey, byteTransaction);
        transaction.setSign(sign);
        transaction.setBytes(byteTransaction);

        new ExtractAnswer().extractAnswerPost(BASE + WALLET_CONTROLLER + OBTAIN_MONEY, transaction, restTemplate);
    }

    @Override
    public void transferMoney(String toUser, Long amount) throws ServerAnswerException {
        ITransaction transaction = new Transaction(token, toUser, amount);
        byte[] byteTransaction = new Gson().toJson(transaction).getBytes();
        byte[] sign = TOMUtil.signMessage(privateKey, byteTransaction);
        transaction.setSign(sign);
        transaction.setBytes(byteTransaction);

        new ExtractAnswer().extractAnswerPost(BASE + WALLET_CONTROLLER + TRANSFER_MONEY, transaction, restTemplate);
    }

    @Override
    public Long currentAmount(String token) throws ServerAnswerException {
        Object longJson = new ExtractAnswer().extractAnswerGet(BASE + WALLET_CONTROLLER + GET_MONEY + "/" + token, restTemplate);
        return Long.valueOf(longJson.toString());
    }

    @Override
    public List<ITransaction> ledgerOfGlobalTransfers() throws ServerAnswerException {
        return getLedgerFromPath(BASE + WALLET_CONTROLLER + GET_LEDGER);
    }

    @Override
    public List<ITransaction> LedgerOfClientTransfers() throws ServerAnswerException {
        return getLedgerFromPath(BASE + WALLET_CONTROLLER + GET_LEDGER + "/" + token);
    }

    @Override
    public Block obtainLastMinedBlock() throws ServerAnswerException, JsonProcessingException {
        Object answer = new ExtractAnswer().extractAnswerGet(BASE + WALLET_CONTROLLER + OBTAIN_LAST_MINED_BLOCK, restTemplate);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonString = new ObjectMapper().writeValueAsString(answer);
        return new Gson().fromJson(jsonString, Block.class);
    }

    @Override
    public Block pickNotMinedTransactions(String token) throws ServerAnswerException, ParseException, JsonProcessingException {
        System.out.println(BASE + WALLET_CONTROLLER + PICK_NOT_MIN_TRANS + "/" + token);
        Object answer = new ExtractAnswer().extractAnswerGet(BASE + WALLET_CONTROLLER + PICK_NOT_MIN_TRANS + "/" + token, restTemplate);


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonString = new ObjectMapper().writeValueAsString(answer);

        blockReceived = objectMapper.readValue(jsonString, Block.class);
        return blockReceived;

    }

    @Override
    public Block mineBlock() throws ServerAnswerException {
        String hash = blockReceived.mineBlock(1);
        blockReceived.setHash(hash);
        System.out.println(hash);
        return blockReceived;
    }

    @Override
    public boolean sendMinedBlock() throws ServerAnswerException {
        Map.Entry<String, Block> blockEntry = Maps.immutableEntry(token, blockReceived);
        Object answer = new ExtractAnswer().extractAnswerPost(BASE + WALLET_CONTROLLER + MINE_BLOCK, blockEntry, restTemplate);
        return Boolean.parseBoolean(answer.toString());
    }

    @Override
    public void transferMoneyWithSmrContract(String toUser, Long amount) throws ServerAnswerException {
        Transaction transaction = new Transaction(token,toUser,amount);
        byte[] byteTransaction = new Gson().toJson(transaction).getBytes();
        byte[] sign = TOMUtil.signMessage(privateKey, byteTransaction);
        transaction.setSign(sign);
        transaction.setBytes(byteTransaction);
        SmartContract smrContract = new SmartContract(token,byteTransaction,sign,amount,token,toUser);



        new ExtractAnswer().extractAnswerPost(BASE + WALLET_CONTROLLER + TRANSFER_MONEY_SMRCONTRACT , smrContract, restTemplate);

    }


    private List<ITransaction> getLedgerFromPath(String path) throws ServerAnswerException {

        Object transactionsJson = new ExtractAnswer().extractAnswerGet(path, restTemplate);

        return Arrays.asList(new Gson().fromJson(transactionsJson.toString(), Transaction[].class));
    }

    private byte[] toBytes(Object obj) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;

        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
