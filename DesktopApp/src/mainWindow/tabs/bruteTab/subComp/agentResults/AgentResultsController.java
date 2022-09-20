package mainWindow.tabs.bruteTab.subComp.agentResults;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import mainWindow.tabs.bruteTab.subComp.agentResults.AgentResult;

import java.util.List;

public class AgentResultsController extends AgentResult {

    @FXML
    private Label agentNameLbl;

    @FXML
    private TextArea resultsTA;

    boolean firstResult = true;


    public AgentResultsController() {
        super("", -1, null);
    }

    @FXML
    private void initialize(){
        agentNameLbl.textProperty().bind(Bindings.concat("<", agentName, ">"));
    }

    public void setAgentName(String agentName) {
        this.agentName.set(agentName);
    }

    public void addResults(List<String> results){
        StringBuilder theResults = new StringBuilder("");

        for(String result: results){
            if(firstResult){
                theResults.append(result);
                firstResult = false;
            }
            else
                theResults.append("\n").append(result);
        }
        resultsTA.setText(resultsTA.getText() + theResults);
    }

    public String getAgentName() {
        return agentName.get();
    }

    public SimpleStringProperty agentNameProperty() {
        return agentName;
    }
}
