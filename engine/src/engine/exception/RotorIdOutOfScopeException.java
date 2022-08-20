package engine.exception;

public class RotorIdOutOfScopeException extends Exception{

    private int badRotorId;

    private int rotorIdsRange;

    private final String EXCEPTION_MESSAGE = "Rotor Ids should be between 1 and %d but a rotor with id %d was given.";

    public RotorIdOutOfScopeException(int badRotorId, int rotorIdsRange) {
        badRotorId = badRotorId;
        rotorIdsRange = rotorIdsRange;
    }

    @Override
    public String getMessage() {return String.format(EXCEPTION_MESSAGE, rotorIdsRange, badRotorId);}
}
