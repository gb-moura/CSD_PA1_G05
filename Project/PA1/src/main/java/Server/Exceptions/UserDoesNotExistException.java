package Server.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserDoesNotExistException extends RuntimeException {

    private static final String USER_DOES_NOT_EXIST_DEFAULT_MESSAGE =
            "There is no recorded user named %s";

    public UserDoesNotExistException(String userId) {
        super(String.format(USER_DOES_NOT_EXIST_DEFAULT_MESSAGE,userId));
    }

}