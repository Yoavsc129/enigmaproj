package screenmachine;

import engine.Engine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class ScreenMachine extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        Engine engine = new Engine();

        primaryStage.setTitle("Enigma");

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("ScreenMachine.fxml");
        fxmlLoader.setLocation(url);

        Parent load = fxmlLoader.load(url.openStream());

        ScreenMachineController screenMachineController = fxmlLoader.getController();
        screenMachineController.setEngine(engine);

        Scene scene = new Scene(load, 900, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
