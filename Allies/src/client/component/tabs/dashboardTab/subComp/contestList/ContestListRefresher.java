package client.component.tabs.dashboardTab.subComp.contestList;

import com.google.gson.Gson;
import engine.serverLogic.Battlefield;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ContestListRefresher extends TimerTask {

    private final Consumer<List<Battlefield>> battlefieldListConsumer;

    public ContestListRefresher(Consumer<List<Battlefield>> battlefieldListConsumer) {
        this.battlefieldListConsumer = battlefieldListConsumer;
    }

    @Override
    public void run() {

        HttpClientUtil.runAsync(Constants.CONTEST_LIST_PAGE, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //something went wrong?
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfBattlefields = response.body().string();
                Gson gson = new Gson();
                Battlefield[] battlefields = gson.fromJson(jsonArrayOfBattlefields, Battlefield[].class);
                battlefieldListConsumer.accept(Arrays.asList(battlefields));
            }
        });

    }
}
