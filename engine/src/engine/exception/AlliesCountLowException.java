package engine.exception;

public class AlliesCountLowException extends Exception {

    private final String EXCEPTION_MESSAGE = "The given allies count is below 2.";

    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}
