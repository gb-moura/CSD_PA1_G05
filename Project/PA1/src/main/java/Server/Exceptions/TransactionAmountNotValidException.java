package Server.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TransactionAmountNotValidException extends RuntimeException {

    private static final String TRANSACTION_AMOUNT_NOT_VALID_DEFAULT_MESSAGE =
            "Transaction values must be positive";

    public TransactionAmountNotValidException() {
        super(TRANSACTION_AMOUNT_NOT_VALID_DEFAULT_MESSAGE);
    }
}