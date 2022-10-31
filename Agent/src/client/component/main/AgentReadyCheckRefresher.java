package client.component.main;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

public class AgentReadyCheckRefresher extends TimerTask {

    private final Consumer<String> battlefieldNameConsumer;

    public AgentReadyCheckRefresher(Consumer<String> battlefieldNameConsumer) {
        this.battlefieldNameConsumer = battlefieldNameConsumer;
    }

    @Override
    public void run() {
        String finalURL = HttpUrl.parse(Constants.READY_CHECK_PAGE)
                .newBuilder().addQueryParameter("type", Constants.AGENT)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //Oh, No!
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                battlefieldNameConsumer.accept((response.body().string()));
            }
        });
    }
}
