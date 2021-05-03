package Server.Controller;

import Server.Exceptions.UserDoesNotExistException;
import Server.Util.UserAccount;

public interface UserController {

    boolean existsUserAccount(String userId);

    UserAccount getUserAccount(String userId) throws UserDoesNotExistException;

    void saveUserInDB(UserAccount user);

}