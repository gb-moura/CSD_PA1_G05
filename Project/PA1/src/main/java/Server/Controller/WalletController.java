package Server.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import Server.Util.Transaction;



import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


public interface
WalletController {



    String BASE_URL = "/money";
    String OBTAIN_COINS ="/obtain" ;
    String TRANSFER_MONEY = "/transfer";
    String GET_MONEY = "/current/{id}";
    String GET_LEDGER = "/ledger";
    String GET_CLIENT_LEDGER = GET_LEDGER + "/{id}";

    @PostMapping(
            value = OBTAIN_COINS,
            consumes = APPLICATION_JSON_VALUE)
    void obtainCoins(@RequestBody Transaction transaction);




    @PostMapping(
            value = TRANSFER_MONEY,
            consumes = APPLICATION_JSON_VALUE)
    void transferMoney(@RequestBody Transaction transaction);

    @GetMapping(
            value =  GET_MONEY,
            produces = APPLICATION_JSON_VALUE)
    Long currentAmount(@PathVariable("id") String id);

    @GetMapping(
            value =  GET_LEDGER,
            produces = APPLICATION_JSON_VALUE)
    List<Transaction> ledgerOfGlobalTransactions();

    @GetMapping(
            value =  GET_CLIENT_LEDGER,
            produces = APPLICATION_JSON_VALUE)
    List<Transaction> ledgerOfClientTransfers(@PathVariable("id") String id);


}

