package Client.Components;

import Client.Util.Block;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.parser.ParseException;
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
import java.util.Map;

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

    @ShellMethod("Obtains the last mined block")
    public String getLastMinedBlock(){
        try{
            return getBlockInformation(client.obtainLastMinedBlock());
        }catch (ServerAnswerException | JsonProcessingException e) {
            return e.getMessage();
        }
    }

    @ShellMethod("Gets a Block with the transactions not mined")
    public String pickNotMinedTransactions(){
        try{
            return getBlockInformation(client.pickNotMinedTransactions(token));
        }catch (ServerAnswerException | ParseException | JsonProcessingException e) {
            return e.getMessage();
        }
    }
    @ShellMethod("Mines the block received by pickNotMinedTransaction")
    public String mineBlock(){
        try{
            client.mineBlock();
            return "Block mined successfully";
        }catch (ServerAnswerException e) {
            return e.getMessage();
        }
    }

    @ShellMethod("Sends the mined block")
    public String sendMineBlock(){
        try{
           boolean answer = client.sendMinedBlock();
            System.out.println("ANSWER   " + answer);
           if(answer){
                return "Block successfully added to blockchain";
           }
            return "Block was not added to the blockchain!";
        }catch (ServerAnswerException e) {
            return e.getMessage();
        }
    }







    private String getBlockInformation(Block block){

        if(block == null){
            return "Client not authorized, please transfer money to FUND to execute operations with the blockchain";
        }
        return block.getHash()+"\n" + transformListOfTransactionInString(block.getData());
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