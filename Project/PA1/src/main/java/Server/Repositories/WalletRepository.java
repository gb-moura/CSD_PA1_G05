package Server.Repositories;

import Server.Util.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface WalletRepository extends MongoRepository<Transaction,String> {

}
