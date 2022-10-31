package client.component.main;

import client.component.candidatesTable.CandidatesTableController;
import client.component.detailsPanel.DetailsPanelController;
import client.component.prepPanel.PrepPanelController;
import client.component.progressPanel.ProgressPanelController;
import client.tasks.GetResultsTask;
import client.tasks.GetTasksTask;
import engine.bruteForce.tasks.MissionResult;
import engine.bruteForce.tasks.MyLock;
import controllerInterface.LoginParentController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import util.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AgentMainController implements LoginParentController {

    @FXML
    private AnchorPane switchPanel;

    @FXML
    private Label agentLbl;

    private GridPane loginComponent;

    private login.LoginController logicController;

    private GridPane prepPanelComponent;

    private PrepPanelController prepPanelController;

    private GridPane detailsPanelComponent;

    private DetailsPanelController detailsPanelController;
    @FXML
    private BorderPane progressPanel;

    @FXML
    private ProgressPanelController progressPanelController;

    @FXML
    private BorderPane candidatesTable;

    @FXML
    private CandidatesTableController candidatesTableController;

    private Boolean canceled;

    private SimpleStringProperty userName;

    private SimpleStringProperty allyName;

    private SimpleStringProperty battlefieldName;

    private SimpleIntegerProperty threadCount;

    private SimpleIntegerProperty missionCount;

    private ThreadPoolExecutor threadPool;

    private TimerTask readyCheck;

    private Timer timer;

    private BlockingQueue<Runnable> tasksQueue;

    private GetResultsTask getResultsTask;


    public AgentMainController() {
        userName = new SimpleStringProperty("");
        allyName = new SimpleStringProperty("");
        battlefieldName = new SimpleStringProperty("");
        threadCount = new SimpleIntegerProperty();
        missionCount = new SimpleIntegerProperty();
        canceled = Boolean.FALSE;
    }

    @FXML
    private void initialize(){
        agentLbl.textProperty().bind(Bindings.format("Agent - %s", userName));
        loadLoginPage();
        loadPrepPanel();
        loadDetailsPanel();
        if(candidatesTableController != null && progressPanelController != null){
            candidatesTableController.setMainController(this);
            progressPanelController.setMainController(this);
        }
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

    private void loadPrepPanel(){
        URL fileUploadURL = getClass().getResource(Constants.PREP_PANEL_FXML_RESOURCE_LOCATION);
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(fileUploadURL);
            prepPanelComponent = fxmlLoader.load();
            prepPanelController = fxmlLoader.getController();
            prepPanelController.setMainController(this);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void loadDetailsPanel(){
        URL fileUploadURL = getClass().getResource(Constants.DETAILS_PANEL_FXML_RESOURCE_LOCATION);
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(fileUploadURL);
            detailsPanelComponent = fxmlLoader.load();
            detailsPanelController = fxmlLoader.getController();
            detailsPanelController.setMainController(this, allyName, battlefieldName);
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

    public void switchFromPrep(String allyName, String battlefieldName, int threadCount, int missionCount){
        this.allyName.set(allyName);
        this.battlefieldName.set(battlefieldName);
        this.threadCount.set(threadCount);
        this.missionCount.set(missionCount);
        setSwitchPanelTo(detailsPanelComponent);
        try {
            prepPanelController.close();
        }catch (Exception ignored){}
        startReadyCheck();
    }

    public void bruteForce(){
        tasksQueue = new LinkedBlockingQueue<>();
        progressPanelController.setBlockingQueue(tasksQueue);
        BlockingQueue<MissionResult> resultsQueue = new LinkedBlockingQueue<>();
        threadPool = new ThreadPoolExecutor(threadCount.get(), threadCount.get(), 5, TimeUnit.SECONDS, tasksQueue);
        threadPool.prestartAllCoreThreads();
        MyLock lock = new MyLock();
        GetTasksTask getTasks = new GetTasksTask(this::updateTaskspulled, this::getWinner, tasksQueue, resultsQueue, missionCount.get(), lock);
        new Thread(getTasks).start();
        timer = new Timer();
        getResultsTask = new GetResultsTask(this::updateMissionResult, resultsQueue, canceled);
        timer.schedule(getResultsTask, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    public void updateTaskspulled(int tasksPulled){
        progressPanelController.updateFromServer(tasksPulled);
    }

    public void updateMissionResult(MissionResult missionResult){
        progressPanelController.updateMissionResult(missionResult);
        candidatesTableController.updateTable(missionResult.getResults());
    }

    public void startReadyCheck(){
        readyCheck = new AgentReadyCheckRefresher(this::setBattlefieldName);
        timer = new Timer();
        timer.schedule(readyCheck, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    public void setBattlefieldName(String battlefieldName){
        Platform.runLater(()->{
            this.battlefieldName.set(battlefieldName);
        });
        if(!battlefieldName.equals("idle")){
            readyCheck.cancel();
            timer.cancel();
            bruteForce();

        }
    }

    public void getWinner(String winner){
        detailsPanelController.setWinner(winner);
        getResultsTask.cancel();
        timer.cancel();
    }

    @Override
    public void updateUserName(String userName) {
        this.userName.set(userName);
    }

    @Override
    public void switchFromLogin() {
        setSwitchPanelTo(prepPanelComponent);
        prepPanelController.startListRefresher();
    }

    @Override
    public String getType() {
        return Constants.AGENT;
    }
}