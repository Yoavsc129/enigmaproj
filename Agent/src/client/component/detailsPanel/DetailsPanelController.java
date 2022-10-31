package client.component.detailsPanel;

import client.component.main.AgentMainController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import util.Constants;

public class DetailsPanelController {

    @FXML
    private Label teamLbl;

    @FXML
    private Label battlefieldLbl;

    private AgentMainController mainController;

    private SimpleStringProperty allyName;

    private SimpleStringProperty battlefieldName;



    public void setMainController(AgentMainController mainController, SimpleStringProperty allyName, SimpleStringProperty battlefieldName) {
        this.mainController = mainController;
        this.allyName = allyName;
        teamLbl.textProperty().bind(this.allyName);
        this.battlefieldName = battlefieldName;
        battlefieldLbl.textProperty().bind(this.battlefieldName);
    }

    public void setWinner(String winner){
        Platform.runLater(()->{
            battlefieldName.set(String.format(Constants.WINNER_TEMPLATE, winner));
        });

    }
}
