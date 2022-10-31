package client.component.candidatesTable;

import client.component.main.AgentMainController;
import com.google.gson.Gson;
import engine.bruteForce.tasks.TaskResult;
import engine.serverLogic.candidates.Candidate;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CandidatesTableController {

    @FXML
    private TableView<TableRow> candidatesTV;

    @FXML
    private TableColumn<TableRow, String> candidateCol;

    @FXML
    private TableColumn<TableRow, String> configurationCol;

    private ObservableList<TableRow> candidates;

    private AgentMainController mainController;

    public CandidatesTableController() {candidates = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize(){
        setupTable();
        candidatesTV.setItems(candidates);
    }

    public void setMainController(AgentMainController mainController){
        this.mainController = mainController;
    }
    private void setupTable(){
        candidateCol.setCellValueFactory(new PropertyValueFactory<>("candidate"));
        configurationCol.setCellValueFactory(new PropertyValueFactory<>("configuration"));
    }

    public void updateTable(List<TaskResult> results){
        List<Candidate> candidatesToSend = new ArrayList<>();
        for(TaskResult result:results){
            candidates.add(new TableRow(result.getCandidate(), result.getConfiguration()));
            candidatesToSend.add(new Candidate(result.getCandidate(), result.getConfiguration(), "", ""));
        }
        Gson gson = new Gson();
        String candidatesAsString = gson.toJson(candidatesToSend);
        String finalURL = HttpUrl
                .parse(Constants.ADD_CANDIDATES_PAGE)
                .newBuilder()
                .addQueryParameter("candidates", candidatesAsString)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //what?
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //don't do that
            }
        });
        //send it!
    }

}
