package engine.exception;

public class NotEnoughRotorsException extends Exception{

    private int rotorsCount;

    private int rotorsTotal;

    private final String EXCEPTION_MESSAGE = "Rotor-count is %d but only %d rotors are given in the file. The number of rotors should be at least %d.";

    public NotEnoughRotorsException(int rotorsCount, int rotorsTotal) {
        this.rotorsCount = rotorsCount;
        this.rotorsTotal = rotorsTotal;
    }

    @Override
    public String getMessage() {return String.format(EXCEPTION_MESSAGE, rotorsCount, rotorsTotal, rotorsCount);}
}
