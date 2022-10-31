package client.component.tabs.dashboardTab;

import client.component.main.AlliesMainController;
import client.component.tabs.dashboardTab.subComp.agentsTable.AgentsTableController;
import client.component.tabs.dashboardTab.subComp.contestList.ContestListController;
import client.component.tabs.dashboardTab.subComp.singleContest.SingleContestController;
import controllerInterface.LoginParentController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import util.Constants;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DashboardTabController implements LoginParentController, Closeable {

    @FXML
    public AnchorPane contestListPanel;
    @FXML
    private AnchorPane switchPanel;

    @FXML
    private VBox contestListComp;

    @FXML
    private ContestListController contestListCompController;


    private AlliesMainController mainController;

    private GridPane loginComponent;

    private login.LoginController logicController;

    private BorderPane agentsTableComponent;

    private AgentsTableController agentsTableController;



    @FXML
    private void initialize(){
        loadLoginPage();
        loadAgentsTablePage();

        if(contestListCompController != null){
            contestListCompController.setMainController(this);
        }

    }


    public void setMainController(AlliesMainController mainController){
        this.mainController = mainController;
    }

    private void loadLoginPage() {
        URL loginPageUrl = getClass().getResource(Constants.LOGIN_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPageUrl);
            loginComponent = fxmlLoader.load();
            logicController = fxmlLoader.getController();
            logicController.setMainController(this);
            setSwitchPanelTo(loginComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAgentsTablePage(){
        URL fileUploadURL = getClass().getResource(Constants.AGENTS_TABLE_FXML_RESOURCE_LOCATION);
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(fileUploadURL);
            agentsTableComponent = fxmlLoader.load();
            agentsTableController = fxmlLoader.getController();
            //agentsTableController.setMainController(this);
            //add after stuff
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setSwitchPanelTo(Parent pane) {
        switchPanel.getChildren().clear();
        switchPanel.getChildren().add(pane);
        AnchorPane.setBottomAnchor(pane, 1.0);
        AnchorPane.setTopAnchor(pane, 1.0);
        AnchorPane.setLeftAnchor(pane, 1.0);
        AnchorPane.setRightAnchor(pane, 1.0);
    }

    public void setBattlefieldName(String battlefieldName){
        try{
            contestListCompController.close();
            agentsTableController.close();
        }catch(Exception ignored){}
        mainController.setBattlefieldName(battlefieldName);

    }

    public void setContestPanel(Node pane, SingleContestController controller){
        mainController.setContestPanel(pane, controller);
        try{
            contestListCompController.close();
        }catch(Exception ignored){}

    }
    public List<String> getAgents(){
        return agentsTableController.getAgents();
    }


    @Override
    public void updateUserName(String userName) {
        mainController.setUserName(userName);
    }

    @Override
    public void switchFromLogin() {
        setSwitchPanelTo(agentsTableComponent);
        contestListCompController.startListRefresher();
        agentsTableController.startListRefresher();
    }

    @Override
    public String getType() {
        return Constants.ALLY;
    }

    @Override
    public void close() throws IOException {
        agentsTableController.close();
    }
}
