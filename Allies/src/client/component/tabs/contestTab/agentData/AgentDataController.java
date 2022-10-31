package client.component.tabs.contestTab.agentData;

import candidatesTable.CandidatesTableController;
import client.component.tabs.contestTab.ContestTabController;
import client.component.tabs.dashboardTab.subComp.agentsTable.AgentsTableController;
import controllerInterface.CandidateTableParentController;
import engine.serverLogic.users.AgentProgress;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AgentDataController implements CandidateTableParentController {



    public class ProgTableAgent {
        private final SimpleStringProperty name;
        private final SimpleIntegerProperty recieved;
        private final SimpleIntegerProperty waiting;

        private final SimpleIntegerProperty candidates;

        public ProgTableAgent(String name) {
            this.name = new SimpleStringProperty(name);
            this.recieved = new SimpleIntegerProperty(0);
            this.waiting = new SimpleIntegerProperty(0);
            this.candidates = new SimpleIntegerProperty(0);
        }

        public void updateAgent(int recieved, int waiting, int candidates){
            this.recieved.set(recieved);
            this.waiting.set(waiting);
            this.candidates.set(candidates);

        }

        public String getName() {
            return name.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public int getRecieved() {
            return recieved.get();
        }

        public SimpleIntegerProperty recievedProperty() {
            return recieved;
        }

        public int getWaiting() {
            return waiting.get();
        }

        public SimpleIntegerProperty waitingProperty() {
            return waiting;
        }

        public int getCandidates() {
            return candidates.get();
        }

        public SimpleIntegerProperty candidatesProperty() {
            return candidates;
        }
    }

    @FXML
    private Label totalLbl;

    @FXML
    private Label prodLbl;

    @FXML
    private Label doneLbl;

    @FXML
    private TableView<ProgTableAgent> agentProgTV;

    @FXML
    private AnchorPane candidatesPanel;

    private BorderPane candidatesTable;

    private CandidatesTableController candidatesTableController;

    private Map<String, ProgTableAgent> agents;

    private ContestTabController mainController;

    private SimpleIntegerProperty missionsTotal;

    private SimpleIntegerProperty missionsProduced;

    private SimpleIntegerProperty missionsDone;

    private TimerTask progressRefresher;

    private Timer timer;

    public AgentDataController() {
        agents = new HashMap<>();
        missionsTotal = new SimpleIntegerProperty(0);
        missionsProduced = new SimpleIntegerProperty(0);
        missionsDone = new SimpleIntegerProperty(0);
    }

    public void setMainController(ContestTabController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize(){
        prodLbl.textProperty().bind(Bindings.format("%d", missionsProduced));
        doneLbl.textProperty().bind(Bindings.format("%d", missionsDone));
        totalLbl.textProperty().bind(Bindings.format("%d", missionsTotal));
        setupAgentsTV();
        loadCandidatesTable();
    }

    private void loadCandidatesTable(){
        URL url = getClass().getResource(Constants.CANDIDATES_TABLE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(url);
            candidatesTable = fxmlLoader.load();
            candidatesTableController = fxmlLoader.getController();
            candidatesTableController.setMainController(this);
            candidatesPanel.getChildren().add(candidatesTable);
            AnchorPane.setBottomAnchor(candidatesTable, 1.0);
            AnchorPane.setTopAnchor(candidatesTable, 1.0);
            AnchorPane.setLeftAnchor(candidatesTable, 1.0);
            AnchorPane.setRightAnchor(candidatesTable, 1.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupAgentsTV(){
        for(TableColumn c : agentProgTV.getColumns()){
            if(c.getText().equals("Agent")){
                c.setCellValueFactory(new PropertyValueFactory<AgentsTableController.TableAgent, String>("name"));
            }
            if(c.getText().equals("Recieved")){
                c.setCellValueFactory(new PropertyValueFactory<AgentsTableController.TableAgent, String>("recieved"));
            }
            if(c.getText().equals("Waiting")){
                c.setCellValueFactory(new PropertyValueFactory<AgentsTableController.TableAgent, String>("waiting"));
            }
            if(c.getText().equals("Candidates")){
                c.setCellValueFactory(new PropertyValueFactory<AgentsTableController.TableAgent, String>("candidates"));
            }
        }
    }

    public void startProgressRefresher(){
        progressRefresher = new AgentsProgressRefresher(this::updateAgentsTV);
        timer = new Timer();
        timer.schedule(progressRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    private void updateAgentsTV(List<AgentProgress> agentsData){
        Platform.runLater(()->{
            String name;
            int totalProduced = 0;
            int totalDone = 0;
            for(AgentProgress ap: agentsData){
                name = ap.getName();
                agents.get(name).updateAgent(ap.getFromServer(), ap.getInQueue(), ap.getCandidates());
                totalProduced += ap.getFromServer();
                totalDone += ap.getFinished();
            }
            missionsProduced.set(totalProduced);
            missionsDone.set(totalDone);
        });
    }

    public void getTotalTasks(){
        HttpClientUtil.runAsync(Constants.TOTAL_TASKS_PAGE, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //bla
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(()->{
                    try {
                        missionsTotal.set(Integer.parseInt(response.body().string()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }

    public void startCandidateTableRefresher(){
        candidatesTableController.startTableRefresher();
    }

    public void initializeAgentTable(List<String> agents){
        ProgTableAgent newAgent;
        for(String agentName: agents){
            newAgent = new ProgTableAgent(agentName);
            this.agents.put(agentName, newAgent);
            agentProgTV.getItems().add(newAgent);
        }
    }

    @Override
    public void endContest(String winingTeam) {
        try {
            candidatesTableController.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        progressRefresher.cancel();
        timer.cancel();

        mainController.endContest(winingTeam);
    }


}
