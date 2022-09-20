package mainWindow.tabs.encryptTab;

import engine.Engine;
import engine.Msg;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
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

    private SimpleBooleanProperty seqInput;

    private SimpleIntegerProperty messages;

    private final ToggleGroup inputMode;

    private Engine engine;

    private List<Key> keys;

    private TreeItem<Msg> root;

    private TreeItem<Msg> currRoot;

    private boolean newConfig = false;

    public EncryptTabController() {
        currConfig = new SimpleStringProperty();
        output = new SimpleStringProperty("");
        seqInput = new SimpleBooleanProperty(true);
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
        processBtn.disableProperty().bind(seqInput.not());
        doneBtn.disableProperty().bind(seqInput);

        inputMode.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(inputMode.getSelectedToggle() == seqRB) {
                seqInput.set(true);
                if(output.get() != ""){
                    clearBtnAction();
                }
            }
            else{
                seqInput.set(false);
                clearBtnAction();
            }
        });
    }

    @FXML
    void clearBtnAction() {
        output.set("");
        inputTF.clear();
        engine.discreteMsgDone();
    }

    @FXML
    void doneBtnAction(ActionEvent event) {
        updateStats();
        engine.discreteMsgDone();
        inputTF.clear();
        output.set("");
    }

    @FXML
    void processBtnAction(ActionEvent event) {
        String output = engine.decodeMsg(inputTF.getText().toUpperCase());
        this.output.set(output);
        if(engine.checkAbc(inputTF.getText().toUpperCase()) == null) {
            updateCurrConfig();
            updateStats();
            messages.set(engine.getMessagesCount());
        }
        //MORE STUFF

    }
    private void updateCurrConfig(){
        currConfig.set(engine.getCurrMachineSpecs().format());
    }

    @FXML
    void resetBtnAction(ActionEvent event) {
        engine.resetRotors();
        updateCurrConfig();
        engine.discreteMsgDone();
        inputTF.clear();
        output.set("");
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

    @FXML
    void singleKeyAction(KeyEvent event) {
        if(disRB.isSelected()){
            String key = event.getCharacter();
            if(engine.getABC().contains(key.toUpperCase())) {
                output.set(output.get() + engine.decodeChar(key.toUpperCase().charAt(0)));
                updateCurrConfig();
            }
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

    public void setCurrConfig(SimpleStringProperty currConfig){
        this.currConfig = currConfig;
        currConfigLabel.textProperty().bind(currConfig);
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

    public void setMessages(SimpleIntegerProperty messages){
        this.messages =messages;
    }



}
