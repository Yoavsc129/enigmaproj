package client.component.prepPanel;


import com.google.gson.Gson;
import engine.serverLogic.Battlefield;
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

public class AlliesListRefresher extends TimerTask {

    private final Consumer<List<User>> allyListConsumer;

    public AlliesListRefresher(Consumer<List<User>> allyListConsumer) {
        this.allyListConsumer = allyListConsumer;
    }

    @Override
    public void run() {
        String finalURL = HttpUrl.parse(Constants.USER_LIST_PAGE)
                .newBuilder()
                .addQueryParameter("type", Constants.ALLY)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //something went wrong?
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfUsers = response.body().string();
                Gson gson = new Gson();
                User[] allies = gson.fromJson(jsonArrayOfUsers, User[].class);
                allyListConsumer.accept(Arrays.asList(allies));
            }
        });
    }
}
