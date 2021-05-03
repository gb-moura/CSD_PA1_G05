package Client.Services;

import Client.Util.Transaction;
import Client.Exceptions.ServerAnswerException;

import java.util.List;

public interface WalletClient {

    void obtainCoins(String toUser, Long amount);

    void transferMoney(String fromUser, String toUser, Long amount);

    void currentAmount(String userID) ;

    void ledgerOfGlobalTransfers() ;

   void LedgerOfClientTransfers(String userId) ;
}