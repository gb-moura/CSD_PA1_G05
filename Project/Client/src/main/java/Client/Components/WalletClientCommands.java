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

import java.util.List;

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


    @ShellMethod("See the provided user money")
    public String ledger() {
        try {
            return tryToLedger();
        } catch (ServerAnswerException e) {
            return e.getMessage();
        }
    }

    private String tryToLedger() throws ServerAnswerException {
        return transformListOfTransactionInString(
                client.ledgerOfGlobalTransfers());
    }

    @ShellMethod("See the provided user money")
    public String clientLedger(@ShellOption() String id) {
        try {
            return tryToClientLedger(id);
        } catch (ServerAnswerException e) {
            return e.getMessage();
        }
    }

    private String tryToClientLedger(String id) throws ServerAnswerException {
        return transformListOfTransactionInString(
                client.LedgerOfClientTransfers(id));
    }

    private String transformListOfTransactionInString(List<Transaction> transactionsList){
        if (transactionsList.isEmpty())
            return "No transactions performed.";
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Ledger:\n");

        for(Transaction transaction : transactionsList){
            stringBuffer.append(String.format("Transfer from %s to %s of %d\n",
                    transaction.getFrom(),transaction.getTo(),transaction.getAmount()));
        }

        return stringBuffer.toString();
    }


}