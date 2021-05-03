package Client.Components;

import org.springframework.beans.factory.annotation.Autowired;
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
    //TODO improvement include a Terminal class with colors

    private WalletClient client;

    @Autowired
    public WalletClientCommands(WalletClient client) {
        this.client = client;
    }

    @ShellMethod("Create money to a specified user")
    public String obtainMoney(  String toUser,  Long amount) {

            client.obtainCoins(toUser,amount);
            return "Money created successfully";

    }

    @ShellMethod("Transfer money from a provided user to other provided user")
    public String transferMoney(@ShellOption() String fromUser, @ShellOption() String toUser, @ShellOption() Long amount)
    {

            client.transferMoney(fromUser, toUser, amount);
            return "Money was transfer.";

    }


    @ShellMethod("See the provided user money")
    public void currentAmount(String id) {

            client.currentAmount(id);


    }


    @ShellMethod("See the provided user money")
    public void ledger() throws ServerAnswerException{


           client.ledgerOfGlobalTransfers();

    }

    @ShellMethod("See the provided user money")
    public void clientLedger(@ShellOption() String id) throws ServerAnswerException {
        client.LedgerOfClientTransfers(id);
    }


}