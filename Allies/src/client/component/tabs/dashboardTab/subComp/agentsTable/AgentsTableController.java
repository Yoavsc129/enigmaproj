package client.component.tabs.dashboardTab.subComp.agentsTable;

import client.component.tabs.dashboardTab.subComp.contestList.ContestListRefresher;
import engine.serverLogic.users.Agent;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import util.Constants;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AgentsTableController implements Closeable {


    public class TableAgent {
        private final SimpleStringProperty name;
        private final SimpleIntegerProperty threadCount;
        private final SimpleIntegerProperty missionCount;

        public TableAgent(String name, int threadCount, int missionCount) {
            this.name = new SimpleStringProperty(name);
            this.threadCount = new SimpleIntegerProperty(threadCount);
            this.missionCount = new SimpleIntegerProperty(missionCount);
        }

        public String getName() {
            return name.get();
        }
        public void setName(String name) {
            this.name.set(name);
        }
        public int getThreadCount() {
            return threadCount.get();
        }
        public void setThreadCount(int threadCount) {
            this.threadCount.set(threadCount);
        }
        public int getMissionCount() {
            return missionCount.get();
        }
        public void setMissionCount(int missionCount) {
            this.missionCount.set(missionCount);
        }
        public SimpleStringProperty nameProperty() {
            return name;
        }
        public SimpleIntegerProperty threadCountProperty() {
            return threadCount;
        }
        public SimpleIntegerProperty missionCountProperty() {
            return missionCount;
        }
    }

    @FXML
    private TableView<TableAgent> agentsTV;

    private ObservableList<TableAgent> agents;

    private Timer timer;

    private TimerTask listRefresher;

    private List<String> agentNames;

    public AgentsTableController() {
        agents = FXCollections.observableArrayList();
        agentNames = new ArrayList<>();
    }

    @FXML
    private void initialize(){
        setupAgentsTV();
        agentsTV.setItems(agents);
    }

    public void startListRefresher(){
        listRefresher = new AgentListRefresher(this::updateAgentsTable);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    private void updateAgentsTable(List<Agent> agents){
        Platform.runLater(()->{
            transformAgentList(agents);
        });
    }

    private void transformAgentList(List<Agent> agents){
        List<TableAgent> tableAgents = new ArrayList<>();
        agentNames.clear();
        for(Agent a: agents){
            agentNames.add(a.getName());
            tableAgents.add(new TableAgent(a.getName(), a.getThreadCount(), a.getMissionCount()));}
        this.agents.setAll(tableAgents);
    }

    private void setupAgentsTV(){
        for(TableColumn c : agentsTV.getColumns()){
            if(c.getText().equals("Name")){
                c.setCellValueFactory(new PropertyValueFactory<TableAgent, String>("name"));
            }
            if(c.getText().equals("Thread Count")){
                c.setCellValueFactory(new PropertyValueFactory<TableAgent, String>("threadCount"));
            }
            if(c.getText().equals("Mission Count")){
                c.setCellValueFactory(new PropertyValueFactory<TableAgent, String>("missionCount"));
            }
        }
    }

    public List<String> getAgents(){
        return agentNames;

    }

    @Override
    public void close() throws IOException {
        //clean?
        if(timer != null && listRefresher != null){
            listRefresher.cancel();
            timer.cancel();
        }
    }
}
