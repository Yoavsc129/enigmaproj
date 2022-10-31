package client.component.tabs.contestTab;

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

public class AllyReadyCheckRefresher extends TimerTask {
    private final Consumer<Integer> readyCountConsumer;

    private final Consumer<String> inputConsumer;

    private final int missionSize;

    public AllyReadyCheckRefresher(Consumer<Integer> readyCountConsumer, Consumer<String> inputConsumer, int missionSize) {
        this.readyCountConsumer = readyCountConsumer;
        this.inputConsumer = inputConsumer;
        this.missionSize = missionSize;
    }

    @Override
    public void run() {
        String finalURL = HttpUrl.parse(Constants.READY_CHECK_PAGE)
                .newBuilder()
                .addQueryParameter("type", Constants.ALLY)
                .addQueryParameter("missionSize", Integer.toString(missionSize))
                .build()
                .toString();
        HttpClientUtil.runAsync(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //Oh, No!
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String[] resp = response.body().string().split(",");
                readyCountConsumer.accept(Integer.parseInt(resp[0]));
                inputConsumer.accept(resp[1]);
            }
        });
    }
}
