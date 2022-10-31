package mainWindow;

import engine.Engine;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mainWindow.tabs.machineTab.MachineTabController;
import mainWindow.tabs.bruteTab.BruteTabController;
import mainWindow.tabs.encryptTab.EncryptTabController;
import mainWindow.tasks.BusinessLogic;

import java.io.File;

public class MainWindowController {

    @FXML
    private Button loadFileBtn;

    @FXML
    private Label fileName;

    @FXML
    private Label fileError;

    @FXML
    private Tab machineTab;

    @FXML
    private VBox machineTabComp;

    @FXML
    private MachineTabController machineTabCompController;

    @FXML
    private VBox encryptTabComp;

    @FXML
    private EncryptTabController encryptTabCompController;

    @FXML
    private Tab encryptTab;

    @FXML
    private Tab bruteTab;

    @FXML
    private ScrollPane bruteTabComp;

    @FXML
    private BruteTabController bruteTabCompController;

    private SimpleStringProperty fileNameProperty;

    private SimpleStringProperty fileErrorProperty;

    private SimpleBooleanProperty initialCodeSet;

    private Stage primaryStage;

    private Engine engine;

    public MainWindowController() {
        fileNameProperty = new SimpleStringProperty();
        fileErrorProperty = new SimpleStringProperty();
        initialCodeSet = new SimpleBooleanProperty(false);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize(){
        fileName.textProperty().bind(fileNameProperty);
        fileError.textProperty().bind(fileErrorProperty);
        encryptTab.disableProperty().bind(initialCodeSet.not());
        bruteTab.disableProperty().bind(initialCodeSet.not());
        if(machineTabCompController != null && encryptTabCompController != null && bruteTabCompController != null){
            machineTabCompController.setMainController(this);
            encryptTabCompController.setMainController(this);
            bruteTabCompController.setMainController(this);
            BusinessLogic businessLogic = new BusinessLogic(bruteTabCompController);
            bruteTabCompController.setBusinessLogic(businessLogic);
        }
    }

    @FXML
    void loadFileAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select .xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file == null) {
            return;
        }
        try{
            engine.createMachineFromFile(file.getAbsolutePath());
            fileNameProperty.set(file.getAbsolutePath());
            fileErrorProperty.set("");
            //some clear function
            machineTabCompController.setup();
            encryptTabCompController.setup();
            bruteTabCompController.setup();
            machineTabCompController.setFileLoaded(true);
            machineTabCompController.resetConfigurations();
            initialCodeSet.set(false);
        }catch (Exception e){
            fileErrorProperty.set(e.getMessage());
        }



    }

    public void setEngine(Engine engine) {
        this.engine = engine;
        machineTabCompController.setEngine(engine);
        encryptTabCompController.setEngine(engine);
        bruteTabCompController.setEngine(engine);
    }

    public void setCurrConfig(SimpleStringProperty currConfig){
        encryptTabCompController.setCurrConfig(currConfig);
        bruteTabCompController.setCurrConfig(currConfig);
    }

    public void setNewConfig(){encryptTabCompController.setNewConfig();}

    public void setMessages(SimpleIntegerProperty messages){
        encryptTabCompController.setMessages(messages);
    }

    public void setInitialCodeSet() {
        this.initialCodeSet.set(true);
    }
}
