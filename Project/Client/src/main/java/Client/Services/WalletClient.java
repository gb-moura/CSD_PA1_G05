package Client.Services;

import Client.Util.Block;
import Client.Exceptions.ServerAnswerException;
import Client.Util.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.parser.ParseException;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface WalletClient {

    int createClient() throws ServerAnswerException, NoSuchAlgorithmException;

    int  obtainCoins( Long amount) throws ServerAnswerException, NoSuchAlgorithmException;

    int  transferMoney( String toUser, Long amount) throws ServerAnswerException;

    Long currentAmount(String token) throws ServerAnswerException;

    List<Transaction>  ledgerOfGlobalTransfers() throws ServerAnswerException, JsonProcessingException;

    List<Transaction>  LedgerOfClientTransfers() throws ServerAnswerException, JsonProcessingException;

    Block obtainLastMinedBlock() throws ServerAnswerException, JsonProcessingException;

    Block pickNotMinedTransactions(String token) throws ServerAnswerException, ParseException, JsonProcessingException;

    Block mineBlock() throws ServerAnswerException;

    boolean sendMinedBlock() throws ServerAnswerException;

    int transferMoneyWithSmrContract(String toUser, Long amount) throws ServerAnswerException;

    int transferMoneyWithPrivacy(String to,Long amount)  throws ServerAnswerException;


}