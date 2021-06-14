package Client.Components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${token}")
    private String token;


    private WalletClient client;

    @Autowired
    public WalletClientCommands(WalletClient client)  {
        this.client = client;

    }

    @ShellMethod("Create money to a specified user")
    public String obtainMoney( Long amount)  {
    try{
        client.obtainCoins(amount);
        return  "Money added successfully";
    }catch(ServerAnswerException e){
        return e.getMessage();
    }


    }

    @ShellMethod("Transfer money from a provided user to other provided user")
    public String transferMoney( @ShellOption() String toUser, @ShellOption() Long amount)
    {
        try{
            client.transferMoney(toUser,amount);
            return "Money was transferred to " + toUser+".\n" +  "Amount: " + amount ;

        }catch(ServerAnswerException e){
            return e.getMessage();
        }

    }


    @ShellMethod("See the provided user money")
    public String currentAmount() {
        try{
            System.out.println("IDDDD " + token);
            Long response = client.currentAmount(token);
            if(response==0L){
                return "The user does not exists";
            }else{
                return "You have: " + response ;
            }

        }catch(ServerAnswerException e){
            return e.getMessage();
        }

    }


    @ShellMethod("See the provided user money")
    public String ledger() {
        try {
            return transformListOfTransactionInString(
                    client.ledgerOfGlobalTransfers());
        } catch (ServerAnswerException e) {
            return e.getMessage();
        }
    }



    @ShellMethod("See the provided user money")
    public String clientLedger() {
        try {
            return transformListOfTransactionInString(
                    client.LedgerOfClientTransfers());
        } catch (ServerAnswerException e) {
            return e.getMessage();
        }
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