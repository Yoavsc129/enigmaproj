package client.component.tabs.contestTab;

import client.component.main.AlliesMainController;
import client.component.tabs.contestTab.agentData.AgentDataController;
import client.component.tabs.contestTab.alliesTable.AlliesTableController;
import client.component.tabs.dashboardTab.subComp.singleContest.SingleContestController;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.Constants;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ContestTabController {

    @FXML
    public Button readyBtn;

    @FXML
    private AnchorPane contestPanel;

    @FXML
    private Spinner<Integer> missionSizeSpinner;

    @FXML
    private Label inputLbl;

    @FXML
    private HBox agentDataComp;

    @FXML
    private AgentDataController agentDataCompController;

    @FXML
    private VBox alliesTableComp;

    @FXML
    private AlliesTableController alliesTableCompController;

    private AlliesMainController mainController;

    private SingleContestController contestController;

    private SimpleStringProperty battlefield;

    private SimpleStringProperty input;

    private SimpleIntegerProperty missionSize;

    private SimpleBooleanProperty ready;

    private TimerTask readyCheckRefresher;

    private Timer timer;

    public ContestTabController() {
        battlefield = new SimpleStringProperty("");
        input = new SimpleStringProperty("-");
        missionSize = new SimpleIntegerProperty(1);
        ready = new SimpleBooleanProperty(false);
    }

    @FXML
    private void initialize(){
        setupSpinner();
        missionSizeSpinner.disableProperty().bind(ready);
        readyBtn.disableProperty().bind(ready);
        inputLbl.textProperty().bind(input);
        if(agentDataCompController != null && alliesTableCompController != null){
            agentDataCompController.setMainController(this);
            alliesTableCompController.setMainController(this);
        }
    }

    public void setMainController(AlliesMainController mainController){
        this.mainController = mainController;
    }

    public void setInput(String input) {
        Platform.runLater(()->{
            this.input.set(input);
        });

    }

    private void setupSpinner(){
        missionSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
        TextField editor = missionSizeSpinner.getEditor();
        editor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                newValue = newValue + "a";
                editor.setText(newValue.replaceAll("\\D", "1"));
            }
            else missionSize.set(Integer.parseInt(newValue));
        });
    }


    public void setContestPanel(Node pane, SingleContestController controller){
        Platform.runLater(()->{
            this.contestPanel.getChildren().add(pane);
            AnchorPane.setBottomAnchor(pane, 1.0);
            AnchorPane.setTopAnchor(pane, 1.0);
            AnchorPane.setLeftAnchor(pane, 1.0);
            AnchorPane.setRightAnchor(pane, 1.0);
            contestController = controller;
            controller.disableRadioButton();
        });
        alliesTableCompController.setuBoat(controller.getUBoat());
        alliesTableCompController.startTableRefresher();
    }

    public void startReadyCheckRefresher(){
        readyCheckRefresher = new AllyReadyCheckRefresher(this::contestStarts, this::setInput, missionSize.get());
        timer = new Timer();
        timer.schedule(readyCheckRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);

    }

    public void contestStarts(int readyCount){
        if(readyCount == 1){
        agentDataCompController.startCandidateTableRefresher();
        agentDataCompController.startProgressRefresher();
        //agentDataCompController.getTotalTasks();
        readyCheckRefresher.cancel();
        timer.cancel();
        }
    }
    @FXML
    public void readyBtnAction() {
        ready.set(true);
        List<String> agents = mainController.getAgents();
        agentDataCompController.initializeAgentTable(agents);
        startReadyCheckRefresher();
        //Callout!
    }

    public void endContest(String winner){
        Platform.runLater(()->{
            input.set(String.format(Constants.WINNER_TEMPLATE, winner));
        });

    }
}
