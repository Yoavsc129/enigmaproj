package client.component.prepPanel;

import client.component.main.AgentMainController;
import com.google.gson.Gson;
import engine.serverLogic.users.User;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

public class PrepPanelController implements Closeable {

    @FXML
    public Label threadCountLbl;

    @FXML
    private ListView<String> alliesLV;

    @FXML
    private Slider threadSlider;

    @FXML
    private Spinner<Integer> missionSpinner;

    @FXML
    private Button joinBtn;

    private AgentMainController mainController;

    private SimpleBooleanProperty teamSelected;

    private SimpleIntegerProperty missionCount;

    private Timer timer;
    private TimerTask listRefresher;

    public PrepPanelController(){
        teamSelected = new SimpleBooleanProperty(false);
        missionCount = new SimpleIntegerProperty(1);
    }

    public void setMainController(AgentMainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize(){
        setupSpinner();
        threadCountLbl.textProperty().bind(Bindings.format("%.0f", threadSlider.valueProperty()));
        joinBtn.disableProperty().bind(alliesLV.getSelectionModel().selectedIndexProperty().lessThan(0));
    }

    @FXML
    void joinBtnAction(){
        String ally =alliesLV.getSelectionModel().getSelectedItem();
        String finalURL = HttpUrl.parse(Constants.JOIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", ally)
                .addQueryParameter(Constants.THREAD_COUNT, Integer.toString((int)threadSlider.getValue()))
                .addQueryParameter(Constants.MISSION_COUNT, Integer.toString(missionCount.get()))
                .build()
                .toString();
        HttpClientUtil.runAsync(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //something went wrong?
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(()->{
                    String[] details;
                    try {
                        details = response.body().string().split(" ");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    mainController.switchFromPrep(details[0], details[1], (int)threadSlider.getValue(), missionCount.get());
                });

            }
        });

    }

    private void setupSpinner(){
        missionSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
        TextField editor = missionSpinner.getEditor();
        editor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*") || newValue.equals("")) {
                newValue = newValue + "a";
                editor.setText(newValue.replaceAll("\\D", "1"));
            }
            else missionCount.set(Integer.parseInt(newValue));
        });
    }

    public void startListRefresher(){
        listRefresher = new AlliesListRefresher(this::updateAlliesList);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    public void updateAlliesList(List<User> allies){
        Platform.runLater(()->{
            //focus baby
            String ally = alliesLV.getSelectionModel().getSelectedItem();
            List<String> names = new ArrayList<>();
            allies.stream().forEach(user -> names.add(user.getName()));
            alliesLV.setItems(FXCollections.observableArrayList(names));
            int allyIndex = alliesLV.getItems().indexOf(ally);
            if(allyIndex > 0)
                alliesLV.getSelectionModel().select(allyIndex);
        });

    }

    @Override
    public void close() throws IOException {
        alliesLV.getItems().clear();
        if(timer != null && listRefresher != null){
            listRefresher.cancel();
            timer.cancel();
        }
    }
}
