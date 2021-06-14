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
    public SystemReply createClient(String id) {
        logger.info("Proxy received request createMoney");
        return clientAsyncReplicator.invokeOrderedReplication(id,Path.INIT);
    }

    @Override
    public SystemReply obtainCoins(Transaction transaction) {
        logger.info("Proxy received request createMoney");
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
}