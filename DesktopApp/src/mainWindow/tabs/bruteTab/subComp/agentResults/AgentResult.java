package mainWindow.tabs.bruteTab.subComp.agentResults;

import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class AgentResult implements ResultData{

    protected SimpleStringProperty agentName;

    private long time;

    private List<String> results;

    public AgentResult(String agentName, long time, List<String> results) {
        this.agentName = new SimpleStringProperty(agentName);
        this.time = time;
        this.results = results;
    }

    public String getAgentName() {
        return agentName.get();
    }

    public SimpleStringProperty agentNameProperty() {
        return agentName;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public List<String> getResults() {
        return results;
    }
}
