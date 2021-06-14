package Client.Exceptions;

public class NoMajorityAnswerException extends Exception {

    private static final String NO_MAJORITY_ANSWER_DEFAULT_MESSAGE =
            "There was no majority answer received";

    public NoMajorityAnswerException() {
        super(NO_MAJORITY_ANSWER_DEFAULT_MESSAGE);
    }
}
