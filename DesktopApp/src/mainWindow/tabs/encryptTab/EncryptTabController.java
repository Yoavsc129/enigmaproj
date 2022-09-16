package mainWindow.tabs.encryptTab;

import engine.Engine;
import engine.Msg;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;
import mainWindow.MainWindowController;
import mainWindow.tabs.encryptTab.subComp.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class EncryptTabController {

    @FXML
    private Label currConfigLabel;

    @FXML
    private Label outputLabel;

    @FXML
    private TextField inputTF;

    @FXML
    private Button clearBtn;

    @FXML
    private Button resetBtn;

    @FXML
    private Button processBtn;

    @FXML
    private Button doneBtn;

    @FXML
    private RadioButton seqRB;

    @FXML
    private RadioButton disRB;

    @FXML
    private TreeTableView<Msg> statsTT;

    @FXML
    private FlowPane bulbPane;

    @FXML
    private FlowPane keysPane;

    MainWindowController mainController;

    private SimpleStringProperty currConfig;

    private SimpleStringProperty output;

    private final ToggleGroup inputMode;

    private Engine engine;

    private List<Key> keys;

    private TreeItem<Msg> root;

    private TreeItem<Msg> currRoot;

    private boolean newConfig = false;

    public EncryptTabController() {
        currConfig = new SimpleStringProperty();
        output = new SimpleStringProperty("");
        inputMode = new ToggleGroup();
        keys = new ArrayList<>();
    }
    @FXML
    private void initialize(){
        currConfigLabel.textProperty().bind(currConfig);
        outputLabel.textProperty().bind(output);
        seqRB.setToggleGroup(inputMode);
        seqRB.setSelected(true);
        disRB.setToggleGroup(inputMode);
        setupTreeTableView();
        statsTT.setShowRoot(false);
    }

    @FXML
    void clearBtnAction(ActionEvent event) {
        output.set("");
        inputTF.clear();
    }

    @FXML
    void doneBtnAction(ActionEvent event) {
        


    }

    @FXML
    void processBtnAction(ActionEvent event) {
        String output = engine.decodeMsg(inputTF.getText().toUpperCase());
        this.output.set(output);
        updateCurrConfig();
        updateStats();
        //MORE STUFF

    }
    private void updateCurrConfig(){
        currConfig.set(engine.getCurrMachineSpecs().format());
        mainController.updateCurrConfig(currConfig.getValue());
    }

    @FXML
    void resetBtnAction(ActionEvent event) {
        engine.resetRotors();
        updateCurrConfig();
    }

    @FXML
    void keysKeyPressedAction(KeyEvent event) {
        final Key key = findKey(event.getCode());
        if(key != null){
            key.setPressed(event.getEventType()
                    == KeyEvent.KEY_PRESSED);
            event.consume();
        }
    }

    @FXML
    void keysKeyReleasedAction(KeyEvent event) {
        final Key key = findKey(event.getCode());
        if(key != null){
            key.setPressed(event.getEventType()
                    == KeyEvent.KEY_PRESSED);
            event.consume();
        }
    }

    public void setNewConfig() {
        newConfig = true;
    }

    public void setMainController(MainWindowController mainController) {
        this.mainController = mainController;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public SimpleStringProperty currConfigProperty(){return this.currConfig;}

    public void setCurrConfig(String currConfig){
        this.currConfig.set(currConfig);
    }

    public void setup(){
        /*String abc = engine.getABC();
        for (int i = 0; i < abc.length(); i++) {

            Key key = new Key(KeyCode.getKeyCode(abc.substring(i, i + 1)));
            keys.add(key);
            keysPane.getChildren().add(key.createNode());
        }*/
        root = new TreeItem<>(new Msg("", "", 0));
        currRoot = new TreeItem<>(new Msg("", "", 0));
        statsTT.setRoot(root);
    }

    public Key findKey(KeyCode code){
        for(Key key: keys)
            if(key.getKeyCode() == code)
                return key;
        return null;
    }

    private void setupTreeTableView(){
        for(TreeTableColumn c : statsTT.getColumns()){
            if(c.getText().contains("Input")){
                c.setCellValueFactory((Callback<TreeTableColumn.CellDataFeatures<Msg, String>, ObservableValue>) param -> new SimpleStringProperty(param.getValue().getValue().getInput()));
            }
            if(c.getText().contains("Output")){
                c.setCellValueFactory((Callback<TreeTableColumn.CellDataFeatures<Msg, String>, ObservableValue>) param -> new SimpleStringProperty(param.getValue().getValue().getOutput()));
            }
            if(c.getText().contains("Time")){
                c.setCellValueFactory((Callback<TreeTableColumn.CellDataFeatures<Msg, String>, ObservableValue>) param -> new SimpleStringProperty(param.getValue().getValue().getTime()>0? String.format("%,d", param.getValue().getValue().getTime()):""));
            }
        }
    }

    private void updateStats(){
        if(newConfig){
            currRoot = new TreeItem<>(new Msg(currConfig.getValue(), "", 0));
            root.getChildren().add(currRoot);
            newConfig = false;
        }
        currRoot.getChildren().add(new TreeItem<>(engine.getLastMsg()));
        root.setExpanded(true);

    }


}
