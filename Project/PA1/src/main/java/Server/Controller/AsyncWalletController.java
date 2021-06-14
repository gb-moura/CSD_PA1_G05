package Server.Controller;

import Server.Util.SystemReply;
import Server.Util.Transaction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface AsyncWalletController {


    String BASE_URL = "/money";
    String OBTAIN_COINS ="/obtain" ;
    String TRANSFER_MONEY = "/transfer";
    String GET_MONEY = "/current/{id}";
    String GET_LEDGER = "/ledger";
    String GET_CLIENT_LEDGER = "/ledger/{id}";
    String INIT = "/createClient";


    @PostMapping(
            value = INIT,

            produces = APPLICATION_JSON_VALUE)
    SystemReply createClient(@RequestBody String id);


    @PostMapping(
            value = OBTAIN_COINS,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    SystemReply obtainCoins(@RequestBody Transaction transaction);




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
            value =  GET_LEDGER,
            produces = APPLICATION_JSON_VALUE)
    SystemReply ledgerOfGlobalTransactions();

    @GetMapping(
            value =  GET_CLIENT_LEDGER,
            produces = APPLICATION_JSON_VALUE)
    SystemReply ledgerOfClientTransfers(@PathVariable("id") String id);

}