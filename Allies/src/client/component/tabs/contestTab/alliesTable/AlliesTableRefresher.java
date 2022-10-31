package client.component.tabs.contestTab.alliesTable;

import com.google.gson.Gson;
import engine.serverLogic.users.Ally;
import engine.serverLogic.users.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class AlliesTableRefresher extends TimerTask {
    private final Consumer<List<Ally>> allyListConsumer;
    private final String uBoat;

    public AlliesTableRefresher(Consumer<List<Ally>> allyListConsumer, String uBoat) {
        this.allyListConsumer = allyListConsumer;
        this.uBoat = uBoat;
    }

    @Override
    public void run() {
        String finalURL = HttpUrl.parse(Constants.MY_ALLIES_LIST_PAGE)
                .newBuilder()
                .addQueryParameter("username", uBoat)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //something went wrong?
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
