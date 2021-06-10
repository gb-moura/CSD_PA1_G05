
package Server.Controller;

import Server.Exceptions.InsufficientFundsForTransactionException;
import Server.Exceptions.TransactionAmountNotValidException;
import Server.Exceptions.UserDoesNotExistException;
//import Server.Repository.TransactionRepository;
import Server.Repositories.UsersRepository;
import Server.Repositories.WalletRepository;
import Server.Util.Transaction;
import Server.Util.UserAccount;
import ch.qos.logback.core.encoder.EchoEncoder;
import org.apache.catalina.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
//import Server.Repository.UserAccountRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class WalletControllerImpl implements WalletController {

    public static final String SYSTEM_RESERVED_USER = "SYSTEM";
    private final Logger logger =
            LoggerFactory.getLogger(WalletControllerImpl.class);


    @Autowired
    private UsersRepository userRep;

    @Autowired
    private WalletRepository walletRep;



    @Override
    public void obtainCoins(Transaction transaction) {

        if (transaction.getAmount() < 0 || transaction.getTo().equals(SYSTEM_RESERVED_USER)) {
            throw new TransactionAmountNotValidException();
        }
        UserAccount user = getOrCreateUser(transaction.getTo());
        user.addMoney(transaction.getAmount());
        userRep.save(user);
        transaction.setFrom(SYSTEM_RESERVED_USER);
        walletRep.save(transaction);
    }

    private UserAccount getOrCreateUser (String userId) {
        try {
            //return new UserCommonsImpl().getUserAccount(userId);
            Optional<UserAccount> user = userRep.findById(userId);
            return user.orElseThrow(()->new UserDoesNotExistException(userId));
        } catch (UserDoesNotExistException e) {
            return new UserAccount(userId, 0L);
        }
    }

    @Override
    public void transferMoney(Transaction transaction) {

        if (!(transaction.getAmount() != null && transaction.getAmount() > 0))
            throw new TransactionAmountNotValidException();

        UserAccount accountTo = getUser(transaction.getTo());
        UserAccount accountFrom = getUser(transaction.getFrom());
     //we only can transfer money to existing accounts
        if(accountTo == null){
            throw new UserDoesNotExistException(transaction.getTo());
        }
        if(accountFrom == null){
            throw new UserDoesNotExistException(transaction.getFrom());
        }

        accountTo.addMoney(transaction.getAmount());
        accountFrom.addMoney(transaction.getAmount()*-1);


        userRep.save(accountTo);
        userRep.save(accountFrom);
        walletRep.save(transaction);


    }


    @Override
    public Long currentAmount(String id) {
        System.out.println("user " + id );
        UserAccount account = getUser(id);
        if(account == null){
            throw new UserDoesNotExistException("User does not exists " + id);
        }
        return account.getMoney();

    }


    @Override
    public List<Transaction> ledgerOfGlobalTransactions() {
        return walletRep.findAll();

    }

    @Override
    public List<Transaction> ledgerOfClientTransfers(String id) {
        UserAccount user = getUser(id);
        if(user == null){
            throw  new UserDoesNotExistException(id);
        }

        List<Transaction> clientTransactions = new ArrayList<>();
        List<Transaction> transactions = walletRep.findAll();
        for(Transaction t: transactions){
            if(t.getFrom().equals(id)){
                clientTransactions.add(t);
            }
        }
         return clientTransactions;
    }




    /**********************************************************/

    private UserAccount getUser(String userId){
        Optional<UserAccount> user = userRep.findById(userId);
        return user.orElseThrow(()-> new UserDoesNotExistException(userId)) ;
    }

}
