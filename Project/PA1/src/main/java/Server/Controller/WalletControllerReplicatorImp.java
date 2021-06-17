package Server.Controller;

import Server.Replication.ClientAsyncReplicator;
import Server.Replication.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import Server.Util.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@PropertySource("classpath:application.properties")
@RestController("ImpWalletReplicator")
@RequestMapping(value = WalletController.BASE_URL)
public class WalletControllerReplicatorImp implements AsyncWalletController {
    private final Logger logger =
            LoggerFactory.getLogger(WalletControllerReplicatorImp.class);

   /* @Autowired
    ClientReplicator clientReplicator;*/
   @Autowired
   ClientAsyncReplicator clientAsyncReplicator;

    @Qualifier("ImpWallet")
    @Autowired
    WalletController walletController;

    @Override
    public void createGenesisBlock(){
    clientAsyncReplicator.invokeOrderedReplication(null);
    }
    @Override
    public SystemReply createClient(Map.Entry<byte[],String> clientEntry) {
        logger.info("Proxy received request createMoney");
        return clientAsyncReplicator.invokeOrderedReplication(clientEntry,Path.INIT);
    }

    @Override
    public SystemReply obtainCoins(Object[] request) {
        logger.info("Proxy received request createMoney");
        Transaction transaction = (Transaction) fromBytes((byte[])request[2]);
            return clientAsyncReplicator.invokeOrderedReplication(transaction,Path.OBTAIN_COINS);

    }

    @Override
    public SystemReply transferMoney(Transaction transaction) {
        logger.info("Proxy received request transferMoneyBetweenUsers");
        return clientAsyncReplicator.invokeUnorderedReplication(transaction,Path.TRANSFER_MONEY);

    }



    @Override
    public SystemReply currentAmount(String userId) {
        logger.info("Proxy received request currentAmount");
        return clientAsyncReplicator.invokeUnorderedReplication(userId,Path.GET_MONEY);

    }

    @Override
    public SystemReply ledgerOfGlobalTransactions() {
        logger.info("Proxy received request ledgerOfGlobalTransfers");
        return clientAsyncReplicator.invokeUnorderedReplication(Path.GET_LEDGER);
    }



    @Override
    public SystemReply ledgerOfClientTransfers(String userId) {
        logger.info("Proxy received request ledgerOfClientTransfers " + userId);
        return clientAsyncReplicator.invokeUnorderedReplication(userId,Path.GET_CLIENT_LEDGER);

    }

    @Override
    public SystemReply obtainLastMinedBlock() {
        return clientAsyncReplicator.invokeUnorderedReplication(Path.OBTAIN_LAST_MINED_BLOCK);
    }

    @Override
    public SystemReply pickNotMineratedTransactions(String id) {
        return clientAsyncReplicator.invokeUnorderedReplication(id,Path.PICK_NOT_MIN_TRANS);
    }

    @Override
    public SystemReply sendMinedBlock(Map.Entry<String,Block> entry) {
        return clientAsyncReplicator.invokeOrderedReplication(entry,Path.MINE_BLOCK);
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