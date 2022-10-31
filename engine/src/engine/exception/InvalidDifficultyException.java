package engine.exception;

public class InvalidDifficultyException extends Exception{
    private String badDifficulty;

    private final String EXCEPTION_MESSAGE = "The given difficulty %s is invalid.";

    public InvalidDifficultyException(String badDifficulty){this.badDifficulty = badDifficulty;}

    @Override
    public String getMessage() {return String.format(EXCEPTION_MESSAGE, badDifficulty);}

}
