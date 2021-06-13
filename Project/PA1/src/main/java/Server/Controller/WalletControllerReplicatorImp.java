package Server.Controller;

import Server.Replication.ClientReplicator;
import Server.Replication.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import Server.Util.*;

import java.util.List;

@PropertySource("classpath:application.properties")
@RestController("ImpWalletReplicator")
@RequestMapping(value = WalletController.BASE_URL)
public class WalletControllerReplicatorImp implements WalletController {
    private final Logger logger =
            LoggerFactory.getLogger(WalletControllerReplicatorImp.class);

    @Autowired
    ClientReplicator clientReplicator;

    @Qualifier("ImpWallet")
    @Autowired
    WalletController walletController;

    @Override
    public void obtainCoins(Transaction transaction) {
        logger.info("Proxy received request createMoney");

                clientReplicator.invokeOrderedReplication(transaction, Path.OBTAIN_COINS);

    }

    @Override
    public void transferMoney(Transaction transaction) {
        logger.info("Proxy received request transferMoneyBetweenUsers");

                clientReplicator.invokeOrderedReplication(transaction,Path.TRANSFER_MONEY);

    }



    @Override
    public Long currentAmount(String userId) {
        logger.info("Proxy received request currentAmount");

               return  clientReplicator.invokeUnorderedReplication(userId, Path.GET_MONEY);

    }

    @Override
    public List<Transaction> ledgerOfGlobalTransactions() {
        logger.info("Proxy received request ledgerOfGlobalTransfers");

       return clientReplicator.invokeUnorderedReplication(Path.GET_LEDGER);
    }

    @Override
    public List<Transaction> ledgerOfClientTransfers(String userId) {
        logger.info("Proxy received request ledgerOfClientTransfers " + userId);

        return clientReplicator.invokeUnorderedReplication(userId,Path.GET_CLIENT_LEDGER);
    }
}