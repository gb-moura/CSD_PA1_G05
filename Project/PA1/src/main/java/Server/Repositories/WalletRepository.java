package Server.Repositories;

import Server.Util.ITransaction;
import Server.Util.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WalletRepository extends MongoRepository<ITransaction,String> {

    //see if it is necessary to add methods here
}
