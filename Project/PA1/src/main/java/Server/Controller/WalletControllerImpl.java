
package Server.Controller;

import Server.Exceptions.InsufficientFundsForTransactionException;
import Server.Exceptions.TransactionAmountNotValidException;
import Server.Exceptions.UserDoesNotExistException;
//import Server.Repository.TransactionRepository;
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

    private List<UserAccount> users = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    File usersFile = null;
    File transactionFile = null;





    @Override
    public void obtainCoins(Transaction transaction) {
        usersFile = new File("users.txt");
        transactionFile = new File("transactions.txt");
        try {
            usersFile.createNewFile();
            transactionFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //return "HELLO";
        if (transaction.getAmount() < 0 || transaction.getTo().equals(SYSTEM_RESERVED_USER)) {
            throw new TransactionAmountNotValidException();
        }
        UserAccount account = null;
        getFromFileUsers();
        for (UserAccount u : users) {
            if (transaction.getTo().equals(u.getId())) {
                account = u;

            }
        }
        if(account == null){
            account = new UserAccount(transaction.getTo(),transaction.getAmount());
        }
        account.addMoney(transaction.getAmount());

        users.add(account);
        updateUsersFile();
        getFromFileTransactions();
        transaction.setFrom(SYSTEM_RESERVED_USER);
        transactions.add(transaction);
        updateTransactionsFile();

    }



    @Override
    public void transferMoney(Transaction transaction) {
        usersFile = new File("users.txt");
        transactionFile = new File("transactions.txt");
        try {
            usersFile.createNewFile();
            transactionFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!(transaction.getAmount() != null && transaction.getAmount() > 0))
            throw new TransactionAmountNotValidException();

        UserAccount accountTo = null;
        UserAccount accountFrom = null;
        getFromFileUsers();
        for (UserAccount u : users) {
            if (transaction.getTo().equals(u.getId())) {
                accountTo = u;

            }
            if (transaction.getFrom().equals(u.getId())) {
                accountFrom = u;

            }
        }
        if(accountTo == null){
            accountTo = new UserAccount(transaction.getTo(),transaction.getAmount());
            users.add(accountTo);
            updateUsersFile();

        }
        if(accountFrom == null){
            throw new UserDoesNotExistException(transaction.getFrom());
        }
        accountTo.addMoney(transaction.getAmount());
        accountFrom.addMoney(transaction.getAmount()*-1);


        users.add(accountTo);
        users.add(accountFrom);
        updateUsersFile();


        getFromFileTransactions();
        transactions.add(transaction);
        updateTransactionsFile();

    }


    @Override
    public Long currentAmount(String id) {
        usersFile = new File("users.txt");
        transactionFile = new File("transactions.txt");
        try {
            usersFile.createNewFile();
            transactionFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UserAccount account = null;
        getFromFileUsers();
        for (UserAccount u : users) {
            if (u.getId().equals(id)) {
                account = u;
            }
        }
        if(account == null){
            throw new UserDoesNotExistException("User does not exists");
        }


        return account.getMoney();

    }


    @Override
    public List<Transaction> ledgerOfGlobalTransactions() {
        transactionFile = new File("transactions.txt");
        try {
            transactionFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getFromFileTransactions();
        return transactions;


    }

    @Override
    public List<Transaction> ledgerOfClientTransfers(String id) {

        transactionFile = new File("transactions.txt");
        try {
            transactionFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Transaction> temp = new ArrayList<>();
        getFromFileTransactions();
        for(Transaction t:transactions){
            if(t.getTo().equals(id) || t.getFrom().equals(id)){
                temp.add(t);
            }

        }
        return temp;


    }
/*
    @Override
    public void removeMoney(@NotNull UserAccount user, long amount) {
   /*    transferMoney(user.getId(), SYSTEM_RESERVED_USER, amount);
        user.addMoney(-1 * amount);
        saveUser(user);
    }*/
/*
    @Override
    public void addMoney(@NotNull UserAccount user, long amount) {
    /*   transferMoney(SYSTEM_RESERVED_USER, user.getId(), amount);
        user.addMoney(amount);
        saveUser(user);
    }*/
/*
    private void saveUser(UserAccount user) {
        //new UserCommonsImpl().saveUserInDB(user);
        //saveUserInDB(user);
    }

    private void transferMoney(String from, String to, Long amount) {
       /* Transaction t = new Transaction();
        t.setFrom(from);
        t.setTo(to);
        t.setAmount(amount);
       // transactionRepository.save(t);*/


    /**********************************************************/
    private void getFromFileUsers() {
        FileInputStream fileIn = null;
        ObjectInputStream objIn = null;
        try {
            fileIn = new FileInputStream(usersFile);
            if (fileIn.available() != 0)
                objIn = new ObjectInputStream(fileIn);
            else
                fileIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (objIn != null) {
            Object ob;
            try {
                ob = objIn.readObject();
                users = (List<UserAccount>) ob;
                objIn.close();
                fileIn.close();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        } else {
            users = new ArrayList<>();
            updateUsersFile();
        }
    }

    private void getFromFileTransactions() {
        FileInputStream fileIn = null;
        ObjectInputStream objIn = null;
        try {
            fileIn = new FileInputStream(transactionFile);
            if (fileIn.available() != 0)
                objIn = new ObjectInputStream(fileIn);
            else
                fileIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (objIn != null) {
            Object ob;
            try {
                ob = objIn.readObject();
                transactions = (List<Transaction>) ob;
                objIn.close();
                fileIn.close();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        } else {
            transactions = new ArrayList<>();

            updateTransactionsFile();
        }
    }

    private void updateUsersFile() {
        try {
            FileOutputStream fileIn = new FileOutputStream(usersFile);
            ObjectOutputStream out = new ObjectOutputStream(fileIn);
            out.writeObject(users);
            out.close();
            fileIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTransactionsFile() {
        try {
            FileOutputStream fileIn = new FileOutputStream(transactionFile);
            ObjectOutputStream out = new ObjectOutputStream(fileIn);
            out.writeObject(transactions);
            out.close();
            fileIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
