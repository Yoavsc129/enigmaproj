package engine.exception;

public class NotUniqueIdException extends Exception{

    private int badId;

    private final String EXCEPTION_MESSAGE = "Rotors IDs should be unique but the ID %d appears at least twice.";

    public NotUniqueIdException(int badId) {
        this.badId = badId;
    }

    @Override
    public String getMessage() {return String.format(EXCEPTION_MESSAGE, badId);}
}
