package Client.Services;

import Client.Util.Transaction;
import Client.Exceptions.ServerAnswerException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WalletClient {

    ResponseEntity<String>  obtainCoins(String toUser, Long amount);

    ResponseEntity<String>  transferMoney(String fromUser, String toUser, Long amount);

    ResponseEntity<String> currentAmount(String userID) ;

    ResponseEntity<String>  ledgerOfGlobalTransfers() ;

    ResponseEntity<String>  LedgerOfClientTransfers(String userId) ;
}