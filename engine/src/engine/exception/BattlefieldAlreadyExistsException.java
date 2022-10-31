package engine.exception;

public class BattlefieldAlreadyExistsException extends Exception{

    private String badBattleField;
    private final String EXCEPTION_MESSAGE = "A battlefield called %s already exists";

    public BattlefieldAlreadyExistsException(String badBattleField){this.badBattleField = badBattleField;}

    @Override
    public String getMessage() {return String.format(EXCEPTION_MESSAGE, badBattleField);}
}
