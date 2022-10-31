package mainWindow.tabs.machineTab;

import engine.Engine;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import machine.Reflector;
import mainWindow.MainWindowController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MachineTabController {

    @FXML
    private Label usedTotalRotors;

    @FXML
    private Label totalReflectors;

    @FXML
    private Label msgProcessed;

    @FXML
    private Label setConfig;

    @FXML
    private TextField rotorTF;

    @FXML
    private TextField positionsTF;

    @FXML
    private TextField plugsTF;

    @FXML
    private ComboBox<String> reflectorCB;

    @FXML
    private Button setCodeBtn;

    @FXML
    private Button randomCodeBtn;

    @FXML
    private Label configError;

    @FXML
    private Label currConfig;

    @FXML
    private GridPane configMenu;

    private SimpleIntegerProperty usedRotors;

    private SimpleIntegerProperty totalRotors;

    private SimpleIntegerProperty reflectors;

    private SimpleIntegerProperty messages;

    private SimpleStringProperty configCurr;

    private SimpleStringProperty configInit;

    private SimpleStringProperty error;

    public void setFileLoaded(boolean fileLoaded) {
        this.fileLoaded.set(fileLoaded);
    }

    private SimpleBooleanProperty fileLoaded;

    MainWindowController mainController;

    private Engine engine;



    public MachineTabController() {
        usedRotors = new SimpleIntegerProperty(0);
        totalRotors = new SimpleIntegerProperty(0);
        reflectors = new SimpleIntegerProperty(0);
        messages = new SimpleIntegerProperty(0);
        configCurr = new SimpleStringProperty();
        configInit = new SimpleStringProperty();
        error = new SimpleStringProperty();
        fileLoaded = new SimpleBooleanProperty(false);
    }

    public void setMainController(MainWindowController mainController) {
        this.mainController = mainController;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void setup(){
        setReflectorCB();
        usedRotors.set(engine.getRotorsCount());
        totalRotors.set(engine.getRotorsTotal());
        reflectors.set(engine.getReflectorsTotal());
        messages.set(engine.getMessagesCount());
        mainController.setMessages(messages);
    }

    public void setReflectorCB() {
        Reflector.ReflectorID[] ids = Reflector.ReflectorID.values();
        List<String> content = new ArrayList<>();
        for (int i = 0; i < engine.getReflectorsTotal(); i++) {
            content.add(ids[i].name());
        }
        ObservableList<String> observableList = FXCollections.observableList(content);
        reflectorCB.setItems(observableList);
    }

    @FXML
    private void initialize(){
        usedTotalRotors.textProperty().bind(Bindings.format("%d / %d", usedRotors, totalRotors));
        totalReflectors.textProperty().bind(Bindings.format("%d", reflectors));
        msgProcessed.textProperty().bind(Bindings.format("%d", messages));
        setConfig.textProperty().bind(configInit);
        currConfig.textProperty().bind(configCurr);
        configError.textProperty().bind(error);
        configMenu.disableProperty().bind(fileLoaded.not());
    }

    @FXML
    void randomCodeAction() throws IOException, ClassNotFoundException {
        engine.randMachineSpecs();
        setInitialConfig();
        setCurrConfig();
        mainController.setInitialCodeSet();
    }

    @FXML
    void setCodeAction() throws IOException, ClassNotFoundException {
        String msg;
        msg = engine.pickRotors(rotorTF.getText());
        if(msg == null) {
            msg = engine.pickInitialRotorsPos(positionsTF.getText().toUpperCase());
            if(msg == null) {
                engine.pickReflector(Reflector.ReflectorID.valueOf(reflectorCB.getValue()).ordinal());
                msg = engine.pickPlugs(plugsTF.getText().toUpperCase());
                if(msg == null) {
                    engine.applyChanges();
                    setInitialConfig();
                    setCurrConfig();
                    mainController.setInitialCodeSet();
                    error.set("");
                    return;
                }
            }
        }
        error.set(msg);
    }

    private void setInitialConfig() throws IOException, ClassNotFoundException {
        configInit.set(engine.getInitialMachineSpecs().format());
        engine.writeMachineToString();
    }

    private void setCurrConfig(){
        configCurr.set(engine.getCurrMachineSpecs().format());
        mainController.setCurrConfig(configCurr);
        mainController.setNewConfig();
    }


    public void resetConfigurations(){
        configCurr.set("");
        configInit.set("");
    }

}

