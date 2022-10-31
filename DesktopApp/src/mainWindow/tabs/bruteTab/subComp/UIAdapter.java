package mainWindow.tabs.bruteTab.subComp;

import javafx.application.Platform;
import mainWindow.tabs.bruteTab.subComp.agentResults.AgentResult;


import java.util.function.Consumer;

public class UIAdapter {
    private Consumer<AgentResult> addNewAgentResult;
    private Consumer<AgentResult> updateExistingAgent;

    public UIAdapter(Consumer<AgentResult> addNewAgentResult, Consumer<AgentResult> updateExistingAgent) {
        this.addNewAgentResult = addNewAgentResult;
        this.updateExistingAgent = updateExistingAgent;
    }

    public void addNewAgent(AgentResult newAgent){
        Platform.runLater(
                () -> addNewAgentResult.accept(newAgent)
        );
    }

    public void updateAgent(AgentResult agent){
        Platform.runLater(
                () -> updateExistingAgent.accept(agent)
        );
    }
}
