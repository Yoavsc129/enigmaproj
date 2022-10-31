package client.component.progressPanel;

import client.component.main.AgentMainController;
import engine.bruteForce.tasks.MissionResult;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

public class ProgressPanelController {

    @FXML
    private Label inQueueLbl;

    @FXML
    private Label fromServerLbl;

    @FXML
    private Label finishedLbl;

    @FXML
    private Label candidatesLbl;

    private SimpleIntegerProperty inQueue, fromServer, finished, candidates;

    private AgentMainController mainController;

    private BlockingQueue blockingQueue;


    public ProgressPanelController() {
        inQueue = new SimpleIntegerProperty(0);
        fromServer = new SimpleIntegerProperty(0);
        finished = new SimpleIntegerProperty(0);
        candidates = new SimpleIntegerProperty(0);
    }

    @FXML
    private void initialize(){
        inQueueLbl.textProperty().bind(inQueue.asString());
        fromServerLbl.textProperty().bind(fromServer.asString());
        finishedLbl.textProperty().bind(finished.asString());
        candidatesLbl.textProperty().bind(candidates.asString());
    }

    public void setMainController(AgentMainController mainController){
        this.mainController = mainController;
    }

    public void setBlockingQueue(BlockingQueue blockingQueue){
        this.blockingQueue =blockingQueue;
    }

    public void updateFromServer(int fromServer){
        this.fromServer.set(fromServer);
        //inQueue.set(blockingQueue.size());
    }

    public void updateMissionResult(MissionResult missionResult){
        finished.set(finished.get() + missionResult.getTasksDone());
        candidates.set(candidates.get() + missionResult.getCandidatesFound());
        String finalURL = HttpUrl
                .parse(Constants.UPDATE_AGENT_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.IN_QUEUE, Integer.toString(inQueue.get()))
                .addQueryParameter(Constants.FROM_SERVER, Integer.toString(fromServer.get()))
                .addQueryParameter(Constants.FINISHED, Integer.toString(finished.get()))
                .addQueryParameter(Constants.CANDIDATES, Integer.toString(candidates.get()))
                .build()
                .toString();
        HttpClientUtil.runAsync(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //bad

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //good? status maybe
            }
        });
    }




}
