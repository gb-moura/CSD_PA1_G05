package Server.Repositories;

import Server.Util.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersRepository extends MongoRepository<UserAccount,String> {

    //see if it is necessary to add methods here
}
