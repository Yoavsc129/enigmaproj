package engine.exception;

public class NotchOutOfRangeException extends Exception{

    int rotorId;

    int badNotch;

    int abcLen;

    private final String EXCEPTION_MESSAGE = "In rotor %d the given notch is %d but should be in range of the ABC length, between 1 and %d.";

    public NotchOutOfRangeException(int rotorId, int badNotch, int abcLen) {
        this.rotorId = rotorId;
        this.badNotch = badNotch;
        this.abcLen = abcLen;
    }

    @Override
    public String getMessage() {return String.format(EXCEPTION_MESSAGE, rotorId, badNotch, abcLen);}

}
