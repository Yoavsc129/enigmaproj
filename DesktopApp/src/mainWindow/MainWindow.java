package mainWindow;

import engine.Engine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainWindow extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {



        primaryStage.setTitle("Enigma");

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("mainWindow.fxml"); // replace with constant
        fxmlLoader.setLocation(url);

        Parent load = fxmlLoader.load(url.openStream());

        MainWindowController mainWindowController = fxmlLoader.getController();
        mainWindowController.setPrimaryStage(primaryStage);
        Engine engine = new Engine();
        mainWindowController.setEngine(engine);

        Scene scene = new Scene(load, 900, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
