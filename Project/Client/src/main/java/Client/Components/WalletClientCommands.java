package Client.Components;

import Client.Util.UserAccount;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import Client.Annotations.NONAUTO;
import Client.Util.Transaction;
import Client.Services.WalletClient;
import Client.Exceptions.ServerAnswerException;

@NONAUTO
@ShellComponent
public class WalletClientCommands {



    private WalletClient client;

    @Autowired
    public WalletClientCommands(WalletClient client) {
        this.client = client;
    }

    @ShellMethod("Create money to a specified user")
    public String obtainMoney(  String toUser,  Long amount) {

        ResponseEntity<String> response = client.obtainCoins(toUser,amount);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return "Something went wrong";
        }
        return "Money added to "+ toUser + " successfully" ;
    }

    @ShellMethod("Transfer money from a provided user to other provided user")
    public String transferMoney(@ShellOption() String fromUser, @ShellOption() String toUser, @ShellOption() Long amount)
    {

        ResponseEntity<String> response = client.transferMoney(fromUser, toUser, amount);
        return "Money was transferred to " + toUser+".\n" + "Money was transferred from " + fromUser +".\n" + "Amount: " + amount ;

    }


    @ShellMethod("See the provided user money")
    public void currentAmount(String id) {
        ResponseEntity<String> response = client.currentAmount(id);
            if (!response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Something went wrong");
            }else {
            System.out.println("The amount of the user " + id +" is: " +  new Gson().fromJson(response.getBody(),Long.class));
        }
    }


    @ShellMethod("See the global ledger of transactions")
    public void ledger() throws ServerAnswerException{

        ResponseEntity<String> response =  client.ledgerOfGlobalTransfers();
        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Something went wrong");
        }else {
            System.out.println(response.getBody().toString());
        }

    }

    @ShellMethod("See the legder of a specific user transactions")
    public void clientLedger(@ShellOption() String id) throws ServerAnswerException {
        ResponseEntity<String> response= client.LedgerOfClientTransfers(id);
        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Something went wrong");
        }else {
            System.out.println(response.getBody().toString());
        }

    }


}