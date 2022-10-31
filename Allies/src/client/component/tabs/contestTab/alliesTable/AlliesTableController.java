package client.component.tabs.contestTab.alliesTable;

import client.component.tabs.contestTab.ContestTabController;
import engine.serverLogic.users.Ally;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AlliesTableController {

    @FXML
    private TableView<AlliesTableRow> alliesTV;

    @FXML
    private TableColumn<AlliesTableRow, String> nameTC;

    @FXML
    private TableColumn<AlliesTableRow, String> agentCountTC;

    @FXML
    private TableColumn<AlliesTableRow, String> missionSizeTC;

    private ContestTabController mainController;

    private String uBoat;

    private TimerTask alliesTableRefresher;

    private Timer timer;

    @FXML
    private void initialize(){
        nameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
        agentCountTC.setCellValueFactory(new PropertyValueFactory<>("agentCount"));
        missionSizeTC.setCellValueFactory(new PropertyValueFactory<>("missionSize"));
    }

    public void setMainController(ContestTabController mainController){
        this.mainController = mainController;
    }

    public void startTableRefresher(){
        alliesTableRefresher = new AlliesTableRefresher(this::updateTable, uBoat);
        timer = new Timer();
        timer.schedule(alliesTableRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    public void updateTable(List<Ally> allies){
        Platform.runLater(()->{
            List<AlliesTableRow> rows = new ArrayList<>();
            for(Ally ally: allies)
                rows.add(new AlliesTableRow(ally.getName(), ally.getAgentsCount(), ally.getMissionSize()));
            alliesTV.getItems().setAll(rows);
        });
    }

    public void setuBoat(String uBoat) {
        this.uBoat = uBoat;
    }
}
