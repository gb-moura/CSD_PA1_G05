package Server.Controller;

import Server.Util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


public interface
WalletController {



    String BASE_URL = "/money";
    String OBTAIN_COINS ="/obtain" ;
    String TRANSFER_MONEY = "/transfer";
    String GET_MONEY = "/current/{id}";
    String GET_LEDGER = "/ledger";
    String GET_CLIENT_LEDGER = "/ledger/{id}";
    String INIT = "/createClient";
    String OBTAIN_LAST_MINED_BLOCK="/obtainlastminedblock";
    String PICK_NOT_MIN_TRANS = "/picknotminedtransactions/{id}";
    String MINE_BLOCK ="/mineblock";
    String TRANSFER_MONEY_SMRCONTRACT = "/transferwithsmr";
    String TRANSFER_MONEY_PRIVACY = "/transferMoneyWithPrivacy";
    String GET_LEDGER_GLOBAL = "/globalLedger/{id}";



    @PostMapping(
            value = INIT,
            consumes = APPLICATION_JSON_VALUE)
    int createClient(@RequestBody Map.Entry<byte[], String> clientEntry ) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException;

    void createGenesisBlock();
    @PostMapping(
            value = OBTAIN_COINS,
            consumes = APPLICATION_JSON_VALUE)
    int obtainCoins(@RequestBody Transaction transaction) throws IOException;


    @PostMapping(
            value = TRANSFER_MONEY,
            consumes = APPLICATION_JSON_VALUE)
    int transferMoney(@RequestBody Transaction transaction) throws InterruptedException;

    @GetMapping(
            value =  GET_MONEY,
            produces = APPLICATION_JSON_VALUE)
    Long currentAmount(@PathVariable("id") String id) throws InterruptedException;

    @GetMapping(
            value =  GET_LEDGER_GLOBAL,
            produces = APPLICATION_JSON_VALUE)
    List<Transaction> ledgerOfGlobalTransactions(@PathVariable("id") String id);

    @GetMapping(
            value =  GET_CLIENT_LEDGER,
            produces = APPLICATION_JSON_VALUE)
    List<Transaction> ledgerOfClientTransfers(@PathVariable("id") String id);


    @GetMapping(
            value =  OBTAIN_LAST_MINED_BLOCK,
            produces = APPLICATION_JSON_VALUE)
    Block obtainLastMinedBlock();

    @GetMapping(
            value =  PICK_NOT_MIN_TRANS,
            produces = APPLICATION_JSON_VALUE)
    Block pickNotMineratedTransactions(String id);

    @PostMapping(
            value = MINE_BLOCK,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    boolean sendMinedBlock(@RequestBody Map.Entry<String,Block> entry) throws InterruptedException, IOException;

    @PostMapping(
            value = TRANSFER_MONEY_SMRCONTRACT,
            consumes = APPLICATION_JSON_VALUE)
    int transferMoneyWithSmr(@RequestBody SmartContract smrContract) throws InterruptedException;

    @PostMapping(
            value = TRANSFER_MONEY_PRIVACY,
            consumes = APPLICATION_JSON_VALUE)
    int transferMoneyWithPrivacy(@RequestBody Transaction transaction) ;

}

