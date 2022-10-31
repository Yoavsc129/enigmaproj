package uBoat.client.component.main.subComp;

import engine.Engine;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import uBoat.client.component.main.UBoatMainController;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.File;
import java.io.IOException;

public class FileUploadController {

    @FXML
    private Button loadFileBtn;

    @FXML
    private Label fileName;

    @FXML
    private Label fileError;

    private UBoatMainController mainController;

    private Stage primaryStage;

    private SimpleStringProperty fileNameProperty;

    private SimpleStringProperty fileErrorProperty;

    public FileUploadController() {
        this.fileNameProperty = new SimpleStringProperty();
        this.fileErrorProperty = new SimpleStringProperty();
    }

    @FXML
    private void initialize(){
        fileName.textProperty().bind(fileNameProperty);
        fileError.textProperty().bind(fileErrorProperty);
    }

    public void setMainController(UBoatMainController mainController){
        this.mainController = mainController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
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
            fileNameProperty.set(file.getAbsolutePath());
            HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.UPLOAD_FILE)
                    .newBuilder().addQueryParameter("username", "temp");
            String finalUrl = urlBuilder.build().toString();
            RequestBody body = new MultipartBody.Builder()
                    .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("text/plain")))
                    .build();

            HttpClientUtil.runAsync(finalUrl, body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() ->
                            fileErrorProperty.set(e.getMessage()));

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if(response.isSuccessful()){
                        String rawBody = response.body().string();
                        mainController.setEngine(Constants.GSON_INSTANCE.fromJson(rawBody, Engine.class));
                        Platform.runLater(()->{
                            mainController.setupMachineTab();
                            mainController.startTeamsRefresher();
                            mainController.setAlliesReq();
                        });
                    }
                    else{
                        Platform.runLater(() ->
                                fileErrorProperty.set("Something went wrong?"));
                    }
                }
            });

        }catch(Exception e){
            fileErrorProperty.set(e.getMessage());
        }

    }

    public void clean(){
        fileNameProperty.set("");
        fileErrorProperty.set("");
    }

}
