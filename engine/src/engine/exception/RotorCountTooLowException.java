package engine.exception;

public class RotorCountTooLowException extends Exception{
    private final String EXCEPTION_MESSAGE = "The variable 'rotors-count' should be at least 2.";

    @Override
    public String getMessage() {return EXCEPTION_MESSAGE;}
}
