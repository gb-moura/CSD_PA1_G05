package Server.Controller;

import Server.Util.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface AsyncWalletController {


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

            produces = APPLICATION_JSON_VALUE)
    SystemReply createClient(@RequestBody Map.Entry<byte[],String> clientEntry);

    void createGenesisBlock();


    @PostMapping(
            value = OBTAIN_COINS,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    SystemReply obtainCoins(@RequestBody Transaction transaction) throws IOException;




    @PostMapping(
            value = TRANSFER_MONEY,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    SystemReply transferMoney(@RequestBody Transaction transaction);

    @GetMapping(
            value =  GET_MONEY,
            produces = APPLICATION_JSON_VALUE)
    SystemReply currentAmount(@PathVariable("id") String id);

    @GetMapping(
            value =  GET_LEDGER_GLOBAL,
            produces = APPLICATION_JSON_VALUE)
    SystemReply ledgerOfGlobalTransactions(@PathVariable("id") String id);

    @GetMapping(
            value =  GET_CLIENT_LEDGER,
            produces = APPLICATION_JSON_VALUE)
    SystemReply ledgerOfClientTransfers(@PathVariable("id") String id);



    @GetMapping(
            value =  OBTAIN_LAST_MINED_BLOCK,
            produces = APPLICATION_JSON_VALUE)
    SystemReply obtainLastMinedBlock();

    @GetMapping(
            value =  PICK_NOT_MIN_TRANS,
            produces = APPLICATION_JSON_VALUE)
    SystemReply pickNotMineratedTransactions(@PathVariable("id") String id);

    @PostMapping(
            value = MINE_BLOCK,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    SystemReply sendMinedBlock(@RequestBody Map.Entry<String,Block> entry);
    @PostMapping(
            value = TRANSFER_MONEY_SMRCONTRACT,
            consumes = APPLICATION_JSON_VALUE)
    SystemReply transferMoneyWithSmr(@RequestBody  SmartContract smrContract) throws InterruptedException;

    @PostMapping(
            value = TRANSFER_MONEY_PRIVACY,
            consumes = APPLICATION_JSON_VALUE)
    SystemReply transferMoneyWithPrivacy(@RequestBody Transaction transaction) throws InterruptedException;

}
