package Client.Services;

import Client.Util.Block;
import Client.Util.Transaction;
import Client.Exceptions.ServerAnswerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface WalletClient {

    int createClient() throws ServerAnswerException;

    void  obtainCoins( Long amount) throws ServerAnswerException;

    void  transferMoney( String toUser, Long amount) throws ServerAnswerException;

    Long currentAmount(String token) throws ServerAnswerException;

   List<Transaction>  ledgerOfGlobalTransfers() throws ServerAnswerException;

    List<Transaction>  LedgerOfClientTransfers() throws ServerAnswerException;

    Block obtainLastMinedBlock() throws ServerAnswerException;

    Block pickNotMinedTransactions(String token) throws ServerAnswerException, ParseException, JsonProcessingException;

    Block mineBlock() throws ServerAnswerException;

    boolean sendMinedBlock() throws ServerAnswerException;

}