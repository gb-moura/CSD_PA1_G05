package Server.Repositories;

import Server.Util.Block;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlockRepository extends MongoRepository<Block,String> {
}
