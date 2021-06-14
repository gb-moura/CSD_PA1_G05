package Server.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TheClientAlreadyExists extends RuntimeException{

    private static final String USER_ALREADY_EXIST_DEFAULT_MESSAGE =
            "There is a recorded user named %s";

    public TheClientAlreadyExists(String userId) {
        super(String.format(USER_ALREADY_EXIST_DEFAULT_MESSAGE,userId));
    }
}
