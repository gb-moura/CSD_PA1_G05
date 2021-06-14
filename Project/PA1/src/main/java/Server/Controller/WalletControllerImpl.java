
package Server.Controller;


import Server.Exceptions.TheClientAlreadyExists;
import Server.Exceptions.TransactionAmountNotValidException;
import Server.Exceptions.UserDoesNotExistException;
import Server.Repositories.UsersRepository;
import Server.Repositories.WalletRepository;
import Server.Util.Block;
import Server.Util.Transaction;
import Server.Util.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;




@Service("ImpWallet")
public class WalletControllerImpl implements WalletController {

    public static final String SYSTEM_RESERVED_USER = "SYSTEM";
    private final Logger logger =
            LoggerFactory.getLogger(WalletControllerImpl.class);


    @Autowired
    private UsersRepository userRep;

    @Autowired
    private WalletRepository walletRep;

    private Block lastBlockAdded;

    @Override
    public int createClient(String id){
        UserAccount user = getUser(id);
        if(user != null ){
        return 0;
        }
        UserAccount u = new UserAccount(id);
        userRep.save(u);
        return 1;

    }

    @Override
    public void obtainCoins(Transaction transaction) {

        if (transaction.getAmount() < 0 || transaction.getTo().equals(SYSTEM_RESERVED_USER)) {
            throw new TransactionAmountNotValidException();
        }

        transaction.setFrom(SYSTEM_RESERVED_USER);
        walletRep.save(transaction);
    }



    @Override
    public void transferMoney(Transaction transaction) {

       
        Long fromAmount = currentAmount(transaction.getFrom());
        if(fromAmount<transaction.getAmount() || transaction.getAmount() < 0)
            throw new TransactionAmountNotValidException();
        
        
        
        walletRep.save(transaction);
    }


    @Override
    public Long currentAmount(String id) {
        Long amount = 0L;
        System.out.println("IDDDDDD " + id);
        List<Transaction> clientLedger = ledgerOfClientTransfers(id);
        for(Transaction t: clientLedger){
            if(t.getTo().equals(id)){
                amount+=t.getAmount();
            }else if(t.getFrom().equals(id)){
                amount-=t.getAmount();
            }
        }
        
        return amount;

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
            if(t.getFrom().equals(id) || t.getTo().equals(id)){
                clientTransactions.add(t);
            }
        }
         return clientTransactions;
    }



    public Block obtainLastMinedBlock(){

        return null;
    }
    public Block pickNotMineratedTransactions(){
        return null;
    }
    public void sendMinedBlock(Block block){

    }




    /**********************************************************/

    private UserAccount getUser(String userId){
        Optional<UserAccount> user = userRep.findById(userId);
        if(user.isEmpty())
            return null;
        return user.get() ;
    }
    
   

}
