package Server.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import Server.Util.Transaction;
import Server.Util.UserAccount;
import Server.Util.VoidWrapper;
import Server.Replication.*;

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
        InvokerWrapper<VoidWrapper> result =
                clientReplicator.invokeOrderedReplication(transaction,Path.OBTAIN_COINS);
        result.getResultOrThrow();
    }

    @Override
    public void transferMoney (Transaction transaction) {
        logger.info("Proxy received request transferMoneyBetweenUsers");
        InvokerWrapper<VoidWrapper> result =
            clientReplicator.invokeOrderedReplication(transaction,Path.TRANSFER_MONEY);
        result.getResultOrThrow();
    }


    @Override
    public Long currentAmount(String userId) {
        logger.info("Proxy received request currentAmount");
        InvokerWrapper<Long> result=
            clientReplicator.invokeUnorderedReplication(userId, Path.GET_MONEY);
        return result.getResultOrThrow();
    }

    @Override
    public List<Transaction> ledgerOfGlobalTransactions() {
        logger.info("Proxy received request ledgerOfClientTransfers");
        return new GenericListResults<Transaction, Void>(clientReplicator)
                .getListWithPath(Path.GET_LEDGER);
    }

    @Override
    public List<Transaction> ledgerOfClientTransfers(String userId) {
        logger.info("Proxy received request ledgerOfClientTransfers");
        return new GenericListResults<Transaction, String>(clientReplicator)
                .getListWithPath(userId,Path.GET_CLIENT_LEDGER);
    }
}
