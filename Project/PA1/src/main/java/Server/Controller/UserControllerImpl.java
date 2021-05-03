package Server.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import Server.Exceptions.UserDoesNotExistException;
import Server.Util.UserAccount;
//import Server.Repository.UserAccountRepository;

import java.util.Optional;


public class UserControllerImpl implements UserController{

   /* @Autowired
    private UserAccountRepository userAccountRepository;
*/
    @Override
    public boolean existsUserAccount(String userId) {
     /*  try {
            getUserAccount(userId);
            return true;
        } catch (UserDoesNotExistException e) {
            return false;
        }
*/
        return  false;

    }

    @Override
    public UserAccount getUserAccount(String userId) throws UserDoesNotExistException{
      /* Optional<UserAccount> user = userAccountRepository.findById(userId);
       return user.orElseThrow(() -> new UserDoesNotExistException(userId));*/
        return null;

    }

    @Override
    public void saveUserInDB(UserAccount user) {
      // userAccountRepository.save(user);
    }
}