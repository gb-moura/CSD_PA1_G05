package Client.Services;

import Client.Util.Transaction;
import Client.Exceptions.ServerAnswerException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WalletClient {

    int createClient() throws ServerAnswerException;

    void  obtainCoins( Long amount) throws ServerAnswerException;

    void  transferMoney( String toUser, Long amount) throws ServerAnswerException;

    Long currentAmount(String token) throws ServerAnswerException;

   List<Transaction>  ledgerOfGlobalTransfers() throws ServerAnswerException;

    List<Transaction>  LedgerOfClientTransfers() throws ServerAnswerException;
}