package uBoat.client.component.main.tabs.contestTab;

import com.google.gson.Gson;
import engine.serverLogic.users.Ally;
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

public class TeamsRefresher extends TimerTask {

    private final Consumer<List<Ally>> allyListConsumer;

    public TeamsRefresher(Consumer<List<Ally>> allyListConsumer) {
        this.allyListConsumer = allyListConsumer;
    }

    @Override
    public void run() {
        HttpClientUtil.runAsync(Constants.MY_ALLIES_LIST_PAGE, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfAllies = response.body().string();
                Gson gson = new Gson();
                Ally[] allies = gson.fromJson(jsonArrayOfAllies, Ally[].class);
                allyListConsumer.accept(Arrays.asList(allies));
            }
        });
    }
}
