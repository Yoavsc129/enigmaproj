package uBoat.client.component.main.tabs.contestTab;

import candidatesTable.CandidatesTableController;
import controllerInterface.CandidateTableParentController;
import engine.Engine;
import engine.serverLogic.users.Ally;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import uBoat.client.component.main.UBoatMainController;
import uBoat.client.component.main.tabs.contestTab.subComp.DictionaryController;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ContestTabController implements CandidateTableParentController {


    @FXML
    private Label currConfigLbl;

    @FXML
    private HBox inputPanel;

    @FXML
    private TextField inputTF;

    @FXML
    private Label outputLbl;

    @FXML
    private Button readyBtn;

    @FXML
    private Label alliesCountLbl;

    @FXML
    private TableView<TeamRow> teamsTV;

    @FXML
    private TextArea statusTA;

    @FXML
    private Button logoutBtn;

    private UBoatMainController mainController;

    private DictionaryController dictionaryController;

    @FXML
    private AnchorPane candidatesPanel;

    private BorderPane candidatesTable;

    private CandidatesTableController candidatesTableController;

    private Engine engine;

    private SimpleStringProperty currConfig;

    private String input;

    private SimpleStringProperty output;

    private SimpleBooleanProperty isInputGiven;

    private SimpleBooleanProperty isReady;

    private SimpleIntegerProperty alliesCurr;

    private SimpleIntegerProperty alliesReq;

    private SimpleIntegerProperty alliesReadyCount;

    private SimpleBooleanProperty contestDone;

    private Timer timer;

    private TimerTask listRefresher;

    private Timer readyTimer;

    private TimerTask readyCheck;

    private ObservableList<TeamRow> allies;

    public ContestTabController() {
        currConfig = new SimpleStringProperty("");
        output = new SimpleStringProperty("");
        isInputGiven = new SimpleBooleanProperty(false);
        isReady = new SimpleBooleanProperty(false);
        contestDone = new SimpleBooleanProperty(false);
        alliesCurr = new SimpleIntegerProperty(0);
        alliesReq = new SimpleIntegerProperty();
        alliesReadyCount = new SimpleIntegerProperty(0);
        allies = FXCollections.observableArrayList();
    }

    public void setMainController(UBoatMainController mainController) {
        this.mainController = mainController;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void setAlliesReq(int alliesReq){
        this.alliesReq.set(alliesReq);
    }

    @FXML
    private void initialize(){
        currConfigLbl.textProperty().bind(currConfig);
        outputLbl.textProperty().bind(output);
        alliesCountLbl.textProperty().bind(Bindings.format("Allies %d/%d", alliesCurr, alliesReq));
        readyBtn.disableProperty().bind(isInputGiven.not());
        inputPanel.disableProperty().bind(isReady);
        logoutBtn.disableProperty().bind(contestDone.not());
        loadCandidatesTable();
        setupTeamsTV();
        teamsTV.setItems(allies);
    }

    private void loadCandidatesTable(){
        URL url = getClass().getResource(Constants.CANDIDATES_TABLE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(url);
            candidatesTable = fxmlLoader.load();
            candidatesTableController = fxmlLoader.getController();
            candidatesTableController.setMainController(this);
            candidatesTableController.setAlly();
            candidatesPanel.getChildren().add(candidatesTable);
            AnchorPane.setBottomAnchor(candidatesTable, 1.0);
            AnchorPane.setTopAnchor(candidatesTable, 1.0);
            AnchorPane.setLeftAnchor(candidatesTable, 1.0);
            AnchorPane.setRightAnchor(candidatesTable, 1.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupTeamsTV(){
        for(TableColumn c : teamsTV.getColumns()){
            if(c.getText().equals("Team Name")){
                c.setCellValueFactory(new PropertyValueFactory<TeamRow, String>("name"));
            }
            if(c.getText().equals("Agents Count")){
                c.setCellValueFactory(new PropertyValueFactory<TeamRow, String>("agentsCount"));
            }
            if(c.getText().equals("Mission Size")){
                c.setCellValueFactory(new PropertyValueFactory<TeamRow, String>("missionSize"));
            }
        }
    }

    public void startTeamsRefresher(){
        listRefresher = new TeamsRefresher(this::updateTeamTV);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    public void startReadyCheckRefresher(){
        /*if (listRefresher != null && timer != null) {
            listRefresher.cancel();
            timer.cancel();
        }*/
        readyCheck = new ReadyCheckRefresher(this::setAlliesReadyCount, output.get(), engine.getPickedRotors(), engine.getPickedReflector());
        readyTimer = new Timer();
        readyTimer.schedule(readyCheck, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
        candidatesTableController.setWiningCandidate(input);
    }

    public void updateTeamTV(List<Ally> allies){
        Platform.runLater(()->{
            List<TeamRow> newAllies = new ArrayList<>();
            for(Ally a: allies)
                newAllies.add(new TeamRow(a.getName(), a.getAgentsCount(), a.getMissionSize()));
            this.allies.setAll(newAllies);
            alliesCurr.set(allies.size());

        });


    }

    public void setAlliesReadyCount(int alliesReadyCount) {
        Platform.runLater(()->{
            this.alliesReadyCount.set(alliesReadyCount);
        });
        if(alliesReadyCount == alliesReq.get() && isReady.get()){
            //start contest baby :-)
            if (listRefresher != null && timer != null) {
                listRefresher.cancel();
                timer.cancel();
                readyCheck.cancel();
                readyTimer.cancel();
            }
            HttpClientUtil.runAsync(Constants.START_CONTEST_PAGE, null, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //somthing nice
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Platform.runLater(()->{
                        candidatesTableController.startTableRefresher();
                    });
                }
            });
        }
    }

    @FXML
    void dictionaryBtnAction(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        //stage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(Constants.DICTIONARY_FXML_RESOURCE_LOCATION); // replace with constant
        fxmlLoader.setLocation(url);
        Parent load = fxmlLoader.load(url.openStream());
        dictionaryController = fxmlLoader.getController();
        dictionaryController.setEngine(engine);
        dictionaryController.setupDictionary();
        Scene scene = new Scene(load, 900, 800);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void processBtnAction() {
        String check = checkInput(inputTF.getText().toUpperCase());
        if(check != null)
            output.set(check);
        else{
            output.set(engine.decodeMsgBT(inputTF.getText().toUpperCase()));
            input = inputTF.getText().toUpperCase();
            isInputGiven.set(true);
            updateCurrConfig();
        }

    }

    private String checkInput(String input){
        String check = engine.checkAbc(input);
        if(check == null){
            String[] words = input.split(" ");
            for(String word: words){
                if(!engine.inDictionary(word)){
                    return String.format("The word %s is not part of the dictionary", word);
                }
            }
            return null;
        }
        else
            return check;
    }

    private void updateCurrConfig(){
        currConfig.set(engine.getCurrMachineSpecs().format());
    }

    public void setCurrConfig(SimpleStringProperty currConfig) {
        this.currConfig = currConfig;
        currConfigLbl.textProperty().bind(currConfig);
    }

    @FXML
    void readyBtnAction() {
        isReady.set(true);
        alliesCountLbl.textProperty().unbind();
        alliesCountLbl.textProperty().bind(Bindings.format("Allies Ready %d/%d", alliesReadyCount, alliesReq));
        startReadyCheckRefresher();

    }

    @FXML
    void resetBtnAction() {
        engine.resetRotors();
        output.set("");
        updateCurrConfig();
        isInputGiven.set(false);
    }
    @FXML
    public void logoutBtnAction() {
        mainController.logout();
    }

    @Override
    public void endContest(String winingTeam) {
        String resoluton = String.format(Constants.WINNER_TEMPLATE, winingTeam);
        statusTA.insertText(0, resoluton);
        contestDone.set(true);
        String finalURL = HttpUrl
                .parse(Constants.KILL_CONTEST_PAGE)
                .newBuilder()
                .addQueryParameter("username", winingTeam)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //dono
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //maybe add string?
            }
        });
        try {
            candidatesTableController.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clean(){
        currConfig.set("");
        output.set("");
        isInputGiven.set(false);
        isReady.set(false);
        contestDone.set(false);
        alliesCurr.set(0);
        alliesReq.set(0);
        alliesReadyCount.set(0);
        alliesCountLbl.textProperty().unbind();
        alliesCountLbl.textProperty().bind(Bindings.format("Allies %d/%d", alliesCurr, alliesReq));
    }
}
