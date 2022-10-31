package client.main;

import client.component.main.AlliesMainController;
import engine.Engine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.Constants;
import util.http.HttpClientUtil;

import java.net.URL;

public class AlliesClient extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Ally");
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(Constants.ALLIES_MAIN_FXML_RESOURCE_LOCATION);
        fxmlLoader.setLocation(url);

        Parent load = fxmlLoader.load(url.openStream());

        AlliesMainController alliesMainController = fxmlLoader.getController();
        alliesMainController.setUserName("");

        Scene scene = new Scene(load, 1038, 844);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        HttpClientUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
