package client.component.main;

import client.component.tabs.contestTab.ContestTabController;
import client.component.tabs.dashboardTab.DashboardTabController;
import client.component.tabs.dashboardTab.subComp.singleContest.SingleContestController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class AlliesMainController {

    @FXML
    private Tab contestTab;

    @FXML
    private Label nameLbl;

    @FXML
    private SplitPane dashboardTabComp;
    @FXML
    private DashboardTabController dashboardTabCompController;

    @FXML
    private VBox contestTabComp;

    @FXML
    private ContestTabController contestTabCompController;

    private SimpleStringProperty userName;
    private SimpleStringProperty battlefieldName;

    private SimpleBooleanProperty inContest;


    public AlliesMainController() {
        userName = new SimpleStringProperty();
        battlefieldName = new SimpleStringProperty();
        inContest = new SimpleBooleanProperty(false);
    }
    @FXML
    private void initialize(){
        nameLbl.textProperty().bind(Bindings.format("Ally - %s", userName));
        contestTab.disableProperty().bind(inContest.not());
        if(dashboardTabCompController != null && contestTabCompController != null){
            dashboardTabCompController.setMainController(this);
            contestTabCompController.setMainController(this);
        }
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public void setBattlefieldName(String battlefieldName) {
        this.battlefieldName.set(battlefieldName);
        inContest.set(true);
    }

    public void setContestPanel(Node pane, SingleContestController controller){
        inContest.set(true);
        contestTabCompController.setContestPanel(pane, controller);
    }

    public List<String> getAgents(){
        return dashboardTabCompController.getAgents();
    }

    public void endContest(){

    }
}
