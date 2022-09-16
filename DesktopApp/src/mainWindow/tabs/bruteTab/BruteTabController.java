package mainWindow.tabs.bruteTab;

import engine.Engine;
import engine.bruteForce.Difficulty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mainWindow.MainWindowController;
import mainWindow.tabs.bruteTab.subComp.DictionaryController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BruteTabController {

    @FXML
    private Label currConfigLbl;

    @FXML
    private TextField inputTF;

    @FXML
    private Label outputLbl;

    @FXML
    private GridPane dmOpBoard;

    @FXML
    private Slider agentSlider;

    @FXML
    private ComboBox<Difficulty> difficultyCB;

    @FXML
    private Button startBtn;

    @FXML
    private Button pauseBtn;

    @FXML
    private Button stopBtn;

    @FXML
    private Label agentsCountLbl;

    @FXML
    private Spinner<Integer> sizeSpinner;

    @FXML
    private Label progressLbl;

    @FXML
    private ProgressBar taskProgressBar;

    @FXML
    private FlowPane resultsFP;

    MainWindowController mainController;

    DictionaryController dictionaryController;

    private Engine engine;

    private SimpleStringProperty currConfig;

    private SimpleStringProperty output;

    private SimpleBooleanProperty isInputGiven;

    private SimpleBooleanProperty inProgress;

    private SimpleBooleanProperty isPaused;

    public BruteTabController() {
        currConfig = new SimpleStringProperty("");
        output = new SimpleStringProperty("");
        isInputGiven = new SimpleBooleanProperty(false);
        inProgress = new SimpleBooleanProperty(false);
        isPaused = new SimpleBooleanProperty(false);
    }

    @FXML
    private void initialize(){
        currConfigLbl.textProperty().bind(currConfig);
        outputLbl.textProperty().bind(output);
        dmOpBoard.disableProperty().bind(isInputGiven.not());
        pauseBtn.disableProperty().bind(Bindings.and(inProgress.not(), isPaused));
        stopBtn.disableProperty().bind(inProgress.not());
        startBtn.disableProperty().bind(Bindings.and(inProgress, isPaused.not()));
    }

    public void setup(){
        agentSlider.setMax(engine.getAgentsCount());
        agentsCountLbl.textProperty().bind(Bindings.format("%.0f", agentSlider.valueProperty()));
        difficultyCB.getItems().setAll(Difficulty.values());
        difficultyCB.setValue(Difficulty.EASY);
        sizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
    }

    @FXML
    void dictionaryBtnAction() throws IOException {
        Stage stage = new Stage();
        //stage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/mainWindow/tabs/bruteTab/subComp/dictionary.fxml"); // replace with constant
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
    void pauseBtnAction(ActionEvent event) {

    }

    @FXML
    void processBtnAction(ActionEvent event) {
        String check = checkInput(inputTF.getText().toUpperCase());
        if(check != null)
            output.set(check);
        else{
            output.set(engine.decodeMsgBT(inputTF.getText().toUpperCase()));
            isInputGiven.set(true);
        }


    }

    @FXML
    void resetBtnAction(ActionEvent event) {
        engine.resetRotors();
        output.set("");
        isInputGiven.set(false);

    }

    @FXML
    void startBtnAction(ActionEvent event) {

    }

    @FXML
    void stopBtnAction(ActionEvent event) {

    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void setMainController(MainWindowController mainController) {
        this.mainController = mainController;
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
}
