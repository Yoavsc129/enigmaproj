package uBoat.client.component.main;


import controllerInterface.LoginParentController;
import engine.Engine;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.*;

import org.jetbrains.annotations.NotNull;
import uBoat.client.component.main.subComp.FileUploadController;
import uBoat.client.component.main.tabs.contestTab.ContestTabController;
import uBoat.client.component.main.tabs.machineTab.MachineTabController;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class UBoatMainController implements LoginParentController {

    @FXML
    public BorderPane switchPanel;
    @FXML
    public Label uBoatLbl;

    @FXML
    private Tab machineTab;

    @FXML
    private VBox machineTabComp;

    @FXML
    private MachineTabController machineTabCompController;

    @FXML
    private VBox contestTabComp;

    @FXML
    private ContestTabController contestTabCompController;

    @FXML
    private Tab contestTab;

    private GridPane loginComponent;

    private login.LoginController logicController;

    private HBox fileUploadComponent;

    private FileUploadController fileUploadController;


    private SimpleStringProperty fileNameProperty;

    private SimpleStringProperty fileErrorProperty;

    private SimpleBooleanProperty initialCodeSet;

    private SimpleStringProperty userName;

    private Stage primaryStage;

    private Engine engine;


    public UBoatMainController() {
        fileNameProperty = new SimpleStringProperty();
        fileErrorProperty = new SimpleStringProperty();
        initialCodeSet = new SimpleBooleanProperty(false);
        userName = new SimpleStringProperty("");
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize(){
        contestTab.disableProperty().bind(initialCodeSet.not());
        uBoatLbl.textProperty().bind(Bindings.format("U-Boat - %s", userName));
        if(machineTabCompController != null && contestTabCompController != null){
            machineTabCompController.setMainController(this);
            contestTabCompController.setMainController(this);
        }
        loadLoginPage();
        loadFileUploadPane();
    }
        public void setEngine(Engine engine) {
        this.engine = engine;
        machineTabCompController.setEngine(engine);
        contestTabCompController.setEngine(engine);
    }

    public void setCurrConfig(SimpleStringProperty currConfig){
        contestTabCompController.setCurrConfig(currConfig);
    }

    public void setInitialCodeSet(boolean set) {
        this.initialCodeSet.set(true);
    }

    public void setupMachineTab(){
        fileErrorProperty.set("File upload successful");
        machineTabCompController.setFileLoaded(true);
        machineTabCompController.setup();
        machineTabCompController.resetConfigurations();
    }

    public void setSwitchPanelTo(Parent pane){
        switchPanel.setCenter(pane);
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

    private void loadFileUploadPane(){
        URL fileUploadURL = getClass().getResource(Constants.FILE_UPLOAD_FXML_RESOURCE_LOCATION);
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(fileUploadURL);
            fileUploadComponent = fxmlLoader.load();
            fileUploadController = fxmlLoader.getController();
            fileUploadController.setMainController(this);
            fileUploadController.setPrimaryStage(primaryStage);//what?
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void startTeamsRefresher(){contestTabCompController.startTeamsRefresher();}

    public void setAlliesReq(){
        contestTabCompController.setAlliesReq(engine.getAlliesReq());
    }

    public void logout(){
        userName.set("");
        fileUploadController.clean();
        contestTabCompController.clean();
        machineTabCompController.clean();
        setSwitchPanelTo(loginComponent);
        HttpClientUtil.runAsync(Constants.LOGOUT_PAGE, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                HttpClientUtil.removeCookiesOf(Constants.BASE_DOMAIN);
            }
        });
    }

    @Override
    public void updateUserName(String userName) {
        Platform.runLater(()->{
            this.userName.set(userName);
        });
    }

    @Override
    public void switchFromLogin() {
        setSwitchPanelTo(fileUploadComponent);
    }

    @Override
    public String getType() {
        return Constants.U_BOAT;
    }
}
