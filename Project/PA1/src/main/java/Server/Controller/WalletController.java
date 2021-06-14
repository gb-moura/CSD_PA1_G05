package Server.Controller;

import Server.Util.Block;
import Server.Util.SystemReply;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import Server.Util.Transaction;



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


    @PostMapping(
            value = INIT,
            consumes = APPLICATION_JSON_VALUE)
    int createClient(@RequestBody String id);

    void createGenesisBlock();
    @PostMapping(
            value = OBTAIN_COINS,
            consumes = APPLICATION_JSON_VALUE)
    void obtainCoins(@RequestBody Transaction transaction);


    @PostMapping(
            value = TRANSFER_MONEY,
            consumes = APPLICATION_JSON_VALUE)
    void transferMoney(@RequestBody Transaction transaction) throws InterruptedException;

    @GetMapping(
            value =  GET_MONEY,
            produces = APPLICATION_JSON_VALUE)
    Long currentAmount(@PathVariable("id") String id) throws InterruptedException;

    @GetMapping(
            value =  GET_LEDGER,
            produces = APPLICATION_JSON_VALUE)
    List<Transaction> ledgerOfGlobalTransactions();

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
    boolean sendMinedBlock(@RequestBody Map.Entry<String,Block> entry) throws InterruptedException;
}

