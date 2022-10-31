package mainWindow.tabs.bruteTab;

import engine.Engine;
import engine.bruteForce.Difficulty;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import mainWindow.MainWindowController;
import mainWindow.tabs.bruteTab.subComp.agentResults.AgentResultsController;
import mainWindow.tabs.bruteTab.subComp.DictionaryController;
import mainWindow.tabs.bruteTab.subComp.UIAdapter;
import mainWindow.tasks.BusinessLogic;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private Label averageTimeLbl;

    @FXML
    private FlowPane resultsFP;

    MainWindowController mainController;

    DictionaryController dictionaryController;

    private Engine engine;

    private BusinessLogic businessLogic;

    private Map<String, AgentResultsController> agentControllers;

    private SimpleStringProperty currConfig;

    private SimpleStringProperty output;

    private SimpleBooleanProperty isInputGiven;

    private SimpleBooleanProperty inProgress;

    private SimpleBooleanProperty isPaused;

    private SimpleFloatProperty averageTime;


    public BruteTabController() {
        currConfig = new SimpleStringProperty("");
        output = new SimpleStringProperty("");
        isInputGiven = new SimpleBooleanProperty(false);
        inProgress = new SimpleBooleanProperty(false);
        isPaused = new SimpleBooleanProperty(false);
        averageTime = new SimpleFloatProperty(0);
    }

    @FXML
    private void initialize(){
        currConfigLbl.textProperty().bind(currConfig);
        outputLbl.textProperty().bind(output);
        dmOpBoard.disableProperty().bind(isInputGiven.not());
        pauseBtn.disableProperty().bind(Bindings.or(inProgress.not(), isPaused));
        stopBtn.disableProperty().bind(inProgress.not());
        startBtn.disableProperty().bind(Bindings.and(inProgress, isPaused.not()));
        averageTimeLbl.textProperty().bind(Bindings.format("%f ms", averageTime));
    }

    public void setup(){
        businessLogic.setDecipher(engine.getDecipher());
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
    void processBtnAction() {
        String check = checkInput(inputTF.getText().toUpperCase());
        if(check != null)
            output.set(check);
        else{
            output.set(engine.decodeMsgBT(inputTF.getText().toUpperCase()));

            isInputGiven.set(true);
            updateCurrConfig();
        }
    }
    private void updateCurrConfig(){
        currConfig.set(engine.getCurrMachineSpecs().format());
    }

    @FXML
    void resetBtnAction() {
        engine.resetRotors();
        output.set("");
        isInputGiven.set(false);

    }

    @FXML
    void startBtnAction() throws IOException, ClassNotFoundException {
        if(isPaused.get()) {
            businessLogic.resume();
            isPaused.set(false);
        }
        else {
            inProgress.set(true);
            cleanData();
            UIAdapter uiAdapter = createUIAdapter();
            //toggles?
            businessLogic.bruteForce(difficultyCB.getValue(), output.get(), sizeSpinner.getValue(), (int) agentSlider.getValue(), uiAdapter,
                    () -> inProgress.set(false));
        }

    }

    @FXML
    void stopBtnAction() {
        businessLogic.stop();
        isPaused.set(false);
        inProgress.set(false);
    }

    @FXML
    void pauseBtnAction() {
        businessLogic.pause();
        isPaused.set(true);

    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void setMainController(MainWindowController mainController) {
        this.mainController = mainController;
    }

    public void setBusinessLogic(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
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
    public void bindTaskToUIComponents(Task<Boolean> aTask, Runnable onFinish){
        taskProgressBar.progressProperty().bind(aTask.progressProperty());

        progressLbl.textProperty().bind(
                Bindings.concat(
                        Bindings.format(
                                "%.0f",
                                Bindings.multiply(
                                        aTask.progressProperty(),
                                        100)),
                        " %"));

        aTask.valueProperty().addListener((observable, oldValue, newValue) -> onTaskFinished(Optional.ofNullable(onFinish)));

    }

    private void onTaskFinished(Optional<Runnable> onFinish) {
        this.taskProgressBar.progressProperty().unbind();
        this.progressLbl.textProperty().unbind();
        averageTime.set(engine.getDecipher().getBfDictionary().getAverageTime());
        onFinish.ifPresent(Runnable::run);
    }

    private UIAdapter createUIAdapter(){
        return new UIAdapter(
                agentResult -> createTile(agentResult.getAgentName(), agentResult.getResults()),
                agentResult -> {
                    AgentResultsController controller = agentControllers.get(agentResult.getAgentName());
                    if(controller != null)
                        controller.addResults(agentResult.getResults());
                }
        );
    }

    private void createTile(String agentName, List<String> results){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/mainWindow/tabs/bruteTab/subComp/agentResults/agentResults.fxml"));// replace with constant
            Node agentTile = loader.load();
            AgentResultsController controller = loader.getController();
            controller.setAgentName(agentName);
            controller.addResults(results);
            resultsFP.getChildren().add(agentTile);
            agentControllers.put(agentName, controller);
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setCurrConfig(SimpleStringProperty currConfig) {
        this.currConfig = currConfig;
        currConfigLbl.textProperty().bind(currConfig);
    }

    public void cleanData(){
        agentControllers = new HashMap<>();
        resultsFP.getChildren().clear();
        averageTime.set(0);
    }
}
