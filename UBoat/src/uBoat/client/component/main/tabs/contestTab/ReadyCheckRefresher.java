package uBoat.client.component.main.tabs.contestTab;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ReadyCheckRefresher extends TimerTask {

    private final Consumer<Integer> readyCountConsumer;

    private final String encodedMessage;

    private final List<Integer> rotors;

    private final int reflector;


    public ReadyCheckRefresher(Consumer<Integer> readyCountConsumer, String encodedMessage, List<Integer> rotors, int reflector) {
        this.readyCountConsumer = readyCountConsumer;
        this.encodedMessage = encodedMessage;
        this.rotors = rotors;
        this.reflector = reflector;
    }

    @Override
    public void run() {
        String finalURL = HttpUrl.parse(Constants.READY_CHECK_PAGE)
                .newBuilder()
                .addQueryParameter("type", Constants.U_BOAT)
                .addQueryParameter("uBoatInput", encodedMessage)
                .addQueryParameter("rotors", rotors.toString())
                .addQueryParameter("reflector", Integer.toString(reflector))
                .build()
                .toString();
        HttpClientUtil.runAsync(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //Oh, No!
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                readyCountConsumer.accept(Integer.parseInt(response.body().string()));
            }
        });
    }
}
