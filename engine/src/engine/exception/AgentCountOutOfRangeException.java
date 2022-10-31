package engine.exception;

public class AgentCountOutOfRangeException extends Exception{

    private int badAgentCount;



    private final String EXCEPTION_MESSAGE = "AgentCount should be between 2 and 50 but the given count is %d.";

    public AgentCountOutOfRangeException(int badAgentCount){
        this.badAgentCount = badAgentCount;
    }

    @Override
    public String getMessage() {return String.format(EXCEPTION_MESSAGE, badAgentCount);}

}
