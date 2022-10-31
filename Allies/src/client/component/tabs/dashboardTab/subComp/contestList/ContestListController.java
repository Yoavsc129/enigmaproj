package client.component.tabs.dashboardTab.subComp.contestList;

import client.component.tabs.dashboardTab.DashboardTabController;
import client.component.tabs.dashboardTab.subComp.singleContest.SingleContestController;
import engine.serverLogic.Battlefield;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

public class ContestListController implements Closeable {

    @FXML
    private FlowPane contestsFP;

    @FXML
    private Button readyBtn;

    private DashboardTabController mainController;

    private Map<String, Node> contestPanels;

    private SingleContestController focusedContest = null;

    private Timer timer;

    private TimerTask listRefresher;

    private SimpleIntegerProperty totalContests;

    @FXML
    void readyBtnAction() {
        String finalURL = HttpUrl.parse(Constants.JOIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", focusedContest.getUBoat())//maybe battlefield?
                .build()
                .toString();

        //join + stop refresher
        HttpClientUtil.runAsync(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //what? no!
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                mainController.setContestPanel(contestPanels.get(focusedContest.getBattlefield()), focusedContest);
            }
        });
    }

    public ContestListController() {
        contestPanels = new HashMap<>();
        totalContests = new SimpleIntegerProperty();
    }

    public void setMainController(DashboardTabController mainController) {
        this.mainController = mainController;
    }

    public void startListRefresher(){
        listRefresher = new ContestListRefresher(this::updateContestList);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    public void updateContestList(List<Battlefield> battlefields){
        Platform.runLater(()->{
            contestsFP.getChildren().clear();
            contestPanels = new HashMap<>();
            for(Battlefield bf : battlefields)
                createTile(bf);
            //set focus
            if(battlefields.size() < totalContests.get()){
                //battlefield removed from list

            } else if (battlefields.size() > totalContests.get()) {
                //new battlefield added!
            }
        });

    }

    private void createTile(Battlefield battlefield){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(Constants.SINGLE_CONTEST_FXML_RESOURCE_LOCATION));
            Node contestTile = loader.load();
            SingleContestController controller = loader.getController();
            controller.setSingleContestController(battlefield, this);
            //set main controller?
            contestsFP.getChildren().add(contestTile);
            contestPanels.put(battlefield.getName(), contestTile);
            if(focusedContest != null)
                if(battlefield.getName().contains(focusedContest.getBattlefield())){
                    if(battlefield.getAlliesCurr() == battlefield.getAlliesCount())
                        focusedContest = null;
                    else {
                        focusedContest = controller;
                        focusedContest.setFocus();
                    }
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeFocus(SingleContestController focusedContest){
        if(this.focusedContest != null)
            focusedContest.setFocus();
        this.focusedContest = focusedContest;
    }

    @Override
    public void close() throws IOException {
        Platform.runLater(()->{
            contestsFP.getChildren().clear();
            contestsFP.setDisable(true);
            readyBtn.setDisable(true);
        });

        if(timer != null && listRefresher != null){
            listRefresher.cancel();
            timer.cancel();
        }
    }
}
