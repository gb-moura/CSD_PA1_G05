
package Server.Controller;


import Server.Exceptions.UserDoesNotExistException;
import Server.Repositories.BlockRepository;
import Server.Repositories.UsersRepository;
import Server.Repositories.WalletRepository;
import Server.Util.*;
import bftsmart.tom.util.TOMUtil;
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



    @Autowired
    private UsersRepository userRep;

    @Autowired
    private WalletRepository walletRep;

    @Autowired
    private BlockRepository blockchain;

    private Block lastMinedBlockAdded;

    private final List<Transaction> notMinedTransactions = new ArrayList<Transaction>();
    private final List<Block> queueToMine = new ArrayList<Block>();

    private final Map<String,PublicKey> keys = new HashMap<>();

    @Override
    public int createClient(Map.Entry<byte[],String> clientEntry) throws NoSuchAlgorithmException, InvalidKeySpecException {
        UserAccount user = getUser(clientEntry.getValue());
        if(user != null ){
            return 0;
        }
        PublicKey publicKey =
                KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(clientEntry.getKey()));

        UserAccount u = new UserAccount(clientEntry.getValue());

        keys.put(u.getId(),publicKey);
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
    public int obtainCoins(Transaction transaction) throws IOException {

        String token = transaction.getTo();
        UserAccount u = getUser(token);
        byte[] signature = transaction.getSign();

        if(u==null)
           return 0;
        boolean isCorrectSigned = TOMUtil.verifySignature(keys.get(token),transaction.getBytes(),signature);
        if(isCorrectSigned){

            if (transaction.getAmount() < 0 || transaction.getTo().equals(SYSTEM_RESERVED_USER)) {
                return 0;
            }

            transaction.setFrom(SYSTEM_RESERVED_USER);
            walletRep.save(transaction);
            notMinedTransactions.add(transaction);
            return 1;
        }else{
           return 0;
        }
    }

    @Override
    public int transferMoney(Transaction transaction) {
        String token = transaction.getFrom();
        UserAccount u = getUser(token);
        UserAccount t = getUser(transaction.getTo());
        byte[] signature = transaction.getSign();
        if (u == null || t == null)
           return 0;
        boolean isCorrectSigned = TOMUtil.verifySignature(keys.get(token), transaction.getBytes(), signature);
        if (isCorrectSigned) {
            Long fromAmount = currentAmount(transaction.getFrom());
            if (fromAmount < transaction.getAmount() || transaction.getAmount() < 0)
                return 0;

            walletRep.save(transaction);
            notMinedTransactions.add(transaction);
            return 1;
        } else {
            return 0;
        }
    }


    @Override
    public Long currentAmount(String id)  {

        Long amount = 0L;
        List<Transaction> clientLedger = ledgerOfClientTransfers(id);
        String publicKey = String.valueOf(keys.get(id).hashCode());
        for(Transaction t: clientLedger){
            if(t.getTo().equals(id) || t.getTo().equals(publicKey)){
                amount+=t.getAmount();
            }else if(t.getFrom().equals(id) || t.getFrom().equals(publicKey)){
                amount-=t.getAmount();
            }
        }

        return amount;

    }


    @Override
    public List<Transaction> ledgerOfGlobalTransactions(String id) {
        List<Transaction> transactions = walletRep.findAll();
        String userKey =  String.valueOf(keys.get(id).hashCode());
        for(Transaction t: transactions){
            if(t.isPrivacy()){
                System.out.println("SOU PRIVADO");
                if(!(t.getFrom().equals(userKey) || t.getTo().equals(userKey))){
                    System.out.println("ENTREI AQUI");
                    t.setFrom("UNKNOWN");
                    t.setTo("UNKNOWN");
                    t.setAmount(-1L);
                }
            }
        }
        return transactions;

    }

    @Override
    public List<Transaction> ledgerOfClientTransfers(String id) {
        UserAccount user = getUser(id);
        if(user == null){
            throw  new UserDoesNotExistException(id);
        }

        List<Transaction> clientTransactions = new ArrayList<>();
        List<Transaction> transactions = walletRep.findAll();
        String fromKey = String.valueOf(keys.get(id).hashCode());
        for(Transaction t: transactions){

            if(t.getFrom().equals(id) || t.getTo().equals(id)  ){
                clientTransactions.add(t);
            }
            if(t.getFrom().equals(fromKey) || t.getTo().equals(fromKey)){
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
    public boolean sendMinedBlock(Map.Entry<String,Block> entry) throws InterruptedException, IOException {
        Block newestBlock = null;
        int maxListSize=0;
        Block block = entry.getValue();
        if(isAuthorized(entry.getKey())){
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
            Transaction transaction = new Transaction("SYSTEM",entry.getKey(),  6L);
            walletRep.save(transaction);
            return true;
        }else{
            return  false;
        }

    }

    @Override
    public int transferMoneyWithSmr(SmartContract smrContractt) throws InterruptedException {


        String token = smrContractt.getToken();
        String toUser = smrContractt.getTo();
        Long amount = smrContractt.getAmount();
        Transaction transaction= new Transaction(token,toUser,amount);
        transaction.setBytes(smrContractt.getTransactionBytes());
        transaction.setSign(smrContractt.getTransactionSignature());

        byte[] signature = transaction.getSign();
        boolean isCorrectSigned = TOMUtil.verifySignature(keys.get(token), transaction.getBytes(), signature);

        if (isCorrectSigned) {
            String toToken = transaction.getTo();
            UserAccount from = getUser(token);
            UserAccount to = getUser(toToken);
            Long fromAmount = currentAmount(token);
            if(smrContractt.validate(fromAmount,from,to)){
                walletRep.save(transaction);
                notMinedTransactions.add(transaction);
                return 1;
            }

        }
        return 0;
    }

    @Override
    public int transferMoneyWithPrivacy(Transaction transaction){
        String token = transaction.getFrom();
        String toToken = transaction.getTo();
        UserAccount u = getUser(token);
        UserAccount t = getUser(toToken);
        byte[] signature = transaction.getSign();
        if (u == null || t == null)
           return 0;
        boolean isCorrectSigned = TOMUtil.verifySignature(keys.get(token), transaction.getBytes(), signature);
        if (isCorrectSigned) {

            PublicKey fromKey = keys.get(token);
            PublicKey toKey = keys.get(toToken);
            Long fromAmount = currentAmount(transaction.getFrom());
            if (fromAmount < transaction.getAmount() || transaction.getAmount() < 0)
                return 0;

            transaction.setFrom(String.valueOf(fromKey.hashCode()));
            transaction.setTo(String.valueOf(toKey.hashCode()));

            walletRep.save(transaction);
            notMinedTransactions.add(transaction);
            return 1;
        } else {
           return 0;
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


}
