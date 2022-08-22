package engine.exception;

public class NotUniqueRefIdException extends Exception{

    private final String badId;

    private final String EXCEPTION_MESSAGE = "Reflectors IDs should be unique but the ID %s appears at least twice.";

    public NotUniqueRefIdException(String badId) {
        this.badId = badId;
    }

    @Override
    public String getMessage() {return String.format(EXCEPTION_MESSAGE, badId);}

}
