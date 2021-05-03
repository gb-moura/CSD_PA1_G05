package Server.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InsufficientFundsForTransactionException extends RuntimeException {

    private static final String INSUFICIENT_FUNDS_FOR_TRANSACTION_DEFAULT_MESSAGE =
            "User %s, with money %d, cannot make a transaction of value %d";

    public InsufficientFundsForTransactionException(String userId, long userMoney, long transactionAmount) {
        super (String.format(INSUFICIENT_FUNDS_FOR_TRANSACTION_DEFAULT_MESSAGE, userId,userMoney,transactionAmount));
    }
}