package engine.exception;

public class DoubleMappingException extends Exception{

    private char doubleMapped;

    private int rotorId;

    private  String rotorSide;

    private final String EXCEPTION_MESSAGE = "In the %s side of rotor %d the letter %c appears at least twice.";

    public DoubleMappingException(char doubleMapped, int rotorId, String rotorSide) {
        this.doubleMapped = doubleMapped;
        this.rotorId = rotorId;
        this.rotorSide = rotorSide;
    }

    @Override
    public String getMessage() {return String.format(EXCEPTION_MESSAGE, rotorSide, rotorId, doubleMapped);}
}
