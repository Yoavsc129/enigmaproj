package uBoat.client.main;

import engine.Engine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

import uBoat.client.component.main.UBoatMainController;
import util.Constants;
import util.http.HttpClientUtil;

public class UBoatClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("UBoat");

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(Constants.UBOAT_MAIN_FXML_RESOURCE_LOCATION);
        fxmlLoader.setLocation(url);

        Parent load = fxmlLoader.load(url.openStream());

        UBoatMainController uBoatMainController = fxmlLoader.getController();
        uBoatMainController.setPrimaryStage(primaryStage);
        Engine engine = new Engine();
        uBoatMainController.setEngine(engine);

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
