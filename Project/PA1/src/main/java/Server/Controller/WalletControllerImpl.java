
package Server.Controller;


import Server.Exceptions.TheClientAlreadyExists;
import Server.Exceptions.TransactionAmountNotValidException;
import Server.Exceptions.UserDoesNotExistException;
import Server.Repositories.BlockRepository;
import Server.Repositories.UsersRepository;
import Server.Repositories.WalletRepository;
import Server.Util.*;
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
import java.util.*;
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

    private List<ITransaction> notMinedTransactions = new ArrayList<ITransaction>();
    private List<Block> queueToMine = new ArrayList<Block>();

    private Map<String,PublicKey> keys = new HashMap<>();

    @Override
    public int createClient(Map.Entry<byte[],String> clientEntry) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        UserAccount user = getUser(clientEntry.getValue());
        if(user != null ){
            return 0;
        }
        PublicKey publicKey =
                KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(clientEntry.getKey()));

        UserAccount u = new UserAccount(clientEntry.getValue());
        System.out.println("KEY: " + publicKey);
        keys.put(u.getId(),publicKey);
        System.out.println("ADICIONEI AO MAPA " + keys.get(u.getId()));
        userRep.save(u);
        return 1;

    }

    @Override
    public void createGenesisBlock() {
        List<ITransaction> emptyList = new ArrayList<>();
        Block b = new Block(emptyList," ");
        blockchain.save(b);
        lastMinedBlockAdded=b;
    }

    @Override
    public void obtainCoins(Transaction transaction) throws IOException {

        String token = transaction.getTo();
        UserAccount u = getUser(token);
        byte[] signature = transaction.getSign();

        if(u==null)
            throw new UserDoesNotExistException(token);
        boolean isCorrectSigned = TOMUtil.verifySignature(keys.get(token),transaction.getBytes(),signature);
        if(isCorrectSigned){

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
    public void transferMoney(Transaction transaction) {
        String token = transaction.getFrom();
        UserAccount u = getUser(token);
        byte[] signature = transaction.getSign();
        if (u == null)
            throw new UserDoesNotExistException(token);
        boolean isCorrectSigned = TOMUtil.verifySignature(keys.get(token), transaction.getBytes(), signature);
        if (isCorrectSigned) {
            Long fromAmount = currentAmount(transaction.getFrom());
            if (fromAmount < transaction.getAmount() || transaction.getAmount() < 0)
                throw new TransactionAmountNotValidException();


            walletRep.save(transaction);
            notMinedTransactions.add(transaction);
        } else {
            System.out.println("obtainCoins - isNotSigned?");
        }
    }


    @Override
    public Long currentAmount(String id)  {

        Long amount = 0L;
        List<ITransaction> clientLedger = ledgerOfClientTransfers(id);
        for(ITransaction t: clientLedger){
            if(t.getTo().equals(id)){
                amount+=t.getAmount();
            }else if(t.getFrom().equals(id)){
                amount-=t.getAmount();
            }
        }

        return amount;

    }


    @Override
    public List<ITransaction> ledgerOfGlobalTransactions() {

        return walletRep.findAll();

    }

    @Override
    public List<ITransaction> ledgerOfClientTransfers(String id) {
        UserAccount user = getUser(id);
        if(user == null){
            throw  new UserDoesNotExistException(id);
        }

        List<ITransaction> clientTransactions = new ArrayList<>();
        List<ITransaction> transactions = walletRep.findAll();
        for(ITransaction t: transactions){
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

    @Override
    public void transferMoneyWithSmr(SmartContract smrContractt) throws InterruptedException {


        String token = smrContractt.getToken();
        String toUser = smrContractt.getTo();
        Long amount = smrContractt.getAmount();
        Transaction transaction= new Transaction(token,toUser,amount);
        transaction.setBytes(smrContractt.getTransactionBytes());
        transaction.setSign(smrContractt.getTransactionSignature());

        byte[] signature = transaction.getSign();
        boolean isCorrectSigned = TOMUtil.verifySignature(keys.get(token), transaction.getBytes(), signature);
        System.out.println("isCorrectSigned " + isCorrectSigned);
        if (isCorrectSigned) {
            String toToken = transaction.getTo();
           ;
            UserAccount from = getUser(token);
            UserAccount to = getUser(toToken);
            Long fromAmount = currentAmount(token);
            if(smrContractt.validate(fromAmount,from,to)){
            System.out.println("Entrei aqui||||||||||||||||");
                walletRep.save(transaction);
                notMinedTransactions.add(transaction);
            }

        }else{
            System.out.println("Money was not transferred");

        }







    }


    /**********************************************************/

    private UserAccount getUser(String userId){
        System.out.println("Entrei no getUser " + userId);
        Optional<UserAccount> user = userRep.findById(userId);
        System.out.println("Tenho o user");
        if(user.isEmpty())
            return null;
        return user.get() ;
    }

    private boolean isAuthorized(String id){
        List<ITransaction> clientLedger = ledgerOfClientTransfers( id);
        boolean authorized = false;
        for(ITransaction t: clientLedger){
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
