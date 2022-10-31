package candidatesTable;

import javafx.beans.property.SimpleStringProperty;

public class Candidate {

    private String candidate;

    private String configuration;

    private String allyTeam;

    private String agent;

    private  SimpleStringProperty candidateProperty;

    private  SimpleStringProperty configurationProperty;

    private  SimpleStringProperty allyTeamProperty;

    private  SimpleStringProperty agentProperty;

    public void setProperties(){
        candidateProperty = new SimpleStringProperty(candidate);
        configurationProperty = new SimpleStringProperty(configuration);
        allyTeamProperty = new SimpleStringProperty(allyTeam);
        agentProperty = new SimpleStringProperty(agent);
    }

    public String getCandidate() {
        return candidate;
    }

    public String getConfiguration() {
        return configuration;
    }

    public String getAllyTeam() {
        return allyTeam;
    }

    public String getAgent() {
        return agent;
    }

    public String getCandidateProperty() {
        return candidateProperty.get();
    }

    public SimpleStringProperty candidatePropertyProperty() {
        return candidateProperty;
    }

    public String getConfigurationProperty() {
        return configurationProperty.get();
    }

    public SimpleStringProperty configurationPropertyProperty() {
        return configurationProperty;
    }

    public String getAllyTeamProperty() {
        return allyTeamProperty.get();
    }

    public SimpleStringProperty allyTeamPropertyProperty() {
        return allyTeamProperty;
    }

    public String getAgentProperty() {
        return agentProperty.get();
    }

    public SimpleStringProperty agentPropertyProperty() {
        return agentProperty;
    }
}
