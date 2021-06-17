
package Server.Controller;


import Server.Exceptions.TheClientAlreadyExists;
import Server.Exceptions.TransactionAmountNotValidException;
import Server.Exceptions.UserDoesNotExistException;
import Server.Repositories.BlockRepository;
import Server.Repositories.UsersRepository;
import Server.Repositories.WalletRepository;
import Server.Util.Block;
import Server.Util.Transaction;
import Server.Util.UserAccount;
import bftsmart.tom.util.TOMUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Service("ImpWallet")
public class WalletControllerImpl implements WalletController {

    public static final String SYSTEM_RESERVED_USER = "SYSTEM";
    private final Logger logger =
            LoggerFactory.getLogger(WalletControllerImpl.class);


    @Autowired
    private UsersRepository userRep;

    @Autowired
    private WalletRepository walletRep;

    @Autowired
    private BlockRepository blockchain;

    private Block lastMinedBlockAdded;

    private List<Transaction> notMinedTransactions = new ArrayList<Transaction>();
    private List<Block> queueToMine = new ArrayList<Block>();

    @Override
    public int createClient(Map.Entry<byte[],String> clientEntry) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        UserAccount user = getUser(clientEntry.getValue());
        if(user != null ){
        return 0;
        }
        PublicKey publicKey =
                KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(clientEntry.getKey()));

        UserAccount u = new UserAccount(clientEntry.getValue(),publicKey);
        userRep.save(u);
        return 1;

    }

    @Override
    public void createGenesisBlock() {
        List<Transaction> emptyList = new ArrayList<>();
        Block b = new Block(emptyList," ");
        blockchain.save(b);
        lastMinedBlockAdded=b;
    }

    @Override
    public void obtainCoins(Object[] request) {
        String token= String.valueOf(request[0]);
        byte[] signature =(byte[]) request[1];
        byte[] transactionBytes = (byte[])request[2];
        UserAccount u = getUser(token);
        if(u==null)
            throw new UserDoesNotExistException(token);

        boolean isCorrectSigned = TOMUtil.verifySignature(u.getPublicKey(),transactionBytes,signature);
        if(isCorrectSigned){
            Transaction transaction = (Transaction) fromBytes(transactionBytes);


        if (transaction.getAmount() < 0 || transaction.getTo().equals(SYSTEM_RESERVED_USER)) {
            throw new TransactionAmountNotValidException();
        }

        transaction.setFrom(SYSTEM_RESERVED_USER);
        walletRep.save(transaction);
        notMinedTransactions.add(transaction);
        }else{
            System.out.println("obtainCoins - isNotSigned?");
        }
    }

    @Override
    public void transferMoney(Transaction transaction)   {

       
        Long fromAmount = currentAmount(transaction.getFrom());
        if(fromAmount<transaction.getAmount() || transaction.getAmount() < 0)
            throw new TransactionAmountNotValidException();
        
        
        
        walletRep.save(transaction);
        notMinedTransactions.add(transaction);
    }


    @Override
    public Long currentAmount(String id)  {

        Long amount = 0L;
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


    @Override
    public Block obtainLastMinedBlock(){

        return lastMinedBlockAdded;
    }
    @Override
    public Block pickNotMineratedTransactions(String id){


        if(isAuthorized(id)){
            return new Block(notMinedTransactions,lastMinedBlockAdded.getPreviousHash());
        }


        return null;
    }

    @Override
    public boolean sendMinedBlock(Map.Entry<String,Block> entry) throws InterruptedException {
        Block newestBlock = null;
        long newestTimestamp = 0L;
        int maxListSize=0;
        Block block = entry.getValue();
        System.out.println(block);
        if(isAuthorized(entry.getKey())){
            System.out.println("TENHO PERMI para entrar");
            if(block.getHash().startsWith("0")){
                queueToMine.add(block);
                TimeUnit.SECONDS.sleep(10);
                if(queueToMine.size()==1){
                    blockchain.save(block);
                    lastMinedBlockAdded = block;
                }else{
                    for(Block b: queueToMine){
                        if(maxListSize<b.getData().size()){
                            maxListSize = b.getData().size();
                            newestBlock=b;
                        }
                    }
                    lastMinedBlockAdded=newestBlock;
                    blockchain.save(newestBlock);

                }
                queueToMine.clear();
                notMinedTransactions.clear();
            }
        }
        if(block==lastMinedBlockAdded){
            return true;
        }else{
            return  false;
        }

    }




    /**********************************************************/

    private UserAccount getUser(String userId){
        Optional<UserAccount> user = userRep.findById(userId);
        if(user.isEmpty())
            return null;
        return user.get() ;
    }

    private boolean isAuthorized(String id){
        List<Transaction> clientLedger = ledgerOfClientTransfers( id);
        boolean authorized = false;
        for(Transaction t: clientLedger){
            if(t.getTo().equals("FUND")){
                authorized=true;
                break;
            }
        }
        return authorized;
    }

    private Object fromBytes(byte[] bytes) {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;

        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);



            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
