package candidatesTable;

import controllerInterface.CandidateTableParentController;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import util.Constants;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CandidatesTableController implements Closeable {

    @FXML
    private TableView<Candidate> candidatesTV;

    @FXML
    private TableColumn<Candidate, String> candidateTC;

    @FXML
    private TableColumn<Candidate, String> configurationTC;

    @FXML
    private TableColumn<Candidate, String> allyAndAgentTC;

    private CandidateTableParentController mainController;

    private SimpleBooleanProperty ally;

    private SimpleIntegerProperty version;

    private SimpleStringProperty winingCandidate;

    private Timer timer;

    private TimerTask tableRefresher;

    public CandidatesTableController() {
        ally = new SimpleBooleanProperty(false);
        version = new SimpleIntegerProperty(0);
        winingCandidate = new SimpleStringProperty("");
    }

    @FXML
    private void initialize(){
        setupCandidatesTV();
    }

    public void setMainController(CandidateTableParentController mainController) {
        this.mainController = mainController;
    }

    public void setWiningCandidate(String winingCandidate) {
        this.winingCandidate.set(winingCandidate);
    }

    public void updateCandidatesTV(CandidatesWithVersion candidatesWithVersion){
        candidatesWithVersion.setProperties();
        Platform.runLater(()->{
            if(version.get() != candidatesWithVersion.getVersion()){
                candidatesTV.getItems().addAll(candidatesWithVersion.getCandidates());
            }
            version.set(candidatesWithVersion.getVersion());
            if(ally.get())
                checkWinner(candidatesWithVersion.getCandidates());
        });
    }

    public void startTableRefresher(){
        tableRefresher = new CandidatesRefresher(this::updateCandidatesTV, this::getWinner, version, ally.get());
        timer = new Timer();
        timer.schedule(tableRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    private void setupCandidatesTV(){
        candidateTC.setCellValueFactory(new PropertyValueFactory<>("candidateProperty"));
        configurationTC.setCellValueFactory(new PropertyValueFactory<>("configurationProperty"));
        allyAndAgentTC.setCellValueFactory(new PropertyValueFactory<>("agentProperty"));
    }

    public void setAlly() {
        ally.set(true);
        allyAndAgentTC.setText("Ally Team");
        allyAndAgentTC.setCellValueFactory(new PropertyValueFactory<>("allyTeamProperty"));
    }

    private void checkWinner(List<Candidate> candidates){
        for(Candidate c: candidates){
            if(c.getCandidateProperty().equals(winingCandidate.get().toUpperCase())) {
                mainController.endContest(c.getAllyTeamProperty());
                break;
            }
        }
        //kill?
    }

    public void getWinner(String winner){
        mainController.endContest(winner);
    }

    @Override
    public void close() throws IOException {
        if(timer != null && tableRefresher != null){
            tableRefresher.cancel();
            timer.cancel();
        }
    }
}
