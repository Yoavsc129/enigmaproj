package engine.exception;

public class BadReflectorIdException extends Exception{

    private String reflectorId;

    private String reflectorIdsRange;

    private final String EXCEPTION_MESSAGE = "The reflectors IDs should be a roman numeral between 'I' and '%s' but a reflector with ID %s was given.";

    public BadReflectorIdException(String reflectorId, String reflectorIdsRange) {
        this.reflectorId = reflectorId;
        this.reflectorIdsRange = reflectorIdsRange;
    }

    @Override
    public String getMessage() {return String.format(EXCEPTION_MESSAGE, reflectorIdsRange, reflectorId);}
}
