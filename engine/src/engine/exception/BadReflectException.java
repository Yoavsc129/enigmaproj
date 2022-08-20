package engine.exception;

public class BadReflectException extends Exception{

    private String reflectorId;

    private int reflect;

    private final String EXCEPTION_MESSAGE = "In reflector '%s' there is an invalid reflection, from %d to itself.";

    public BadReflectException(String reflectorId, int reflect) {
        this.reflectorId = reflectorId;
        this.reflect = reflect;
    }

    @Override
    public String getMessage() {return String.format(EXCEPTION_MESSAGE, reflectorId, reflect);}
}
