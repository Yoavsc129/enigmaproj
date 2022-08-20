package engine.exception;

public class ABCNotEvenException extends Exception {

    private int ABCLen;

    private final String EXCEPTION_MESSAGE = "The length of the ABC in the file is %d. The length should be an even number.";



    public ABCNotEvenException(int abcLen) {
        ABCLen = abcLen;
    }

    @Override
    public String getMessage() {return String.format(EXCEPTION_MESSAGE, ABCLen);}
}
