package client.component.progressPanel;

import javafx.beans.property.SimpleIntegerProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;

public class UpdateProgressRefresher extends TimerTask {

    private SimpleIntegerProperty inQueue, fromServer, finished, candidates;

    public UpdateProgressRefresher(SimpleIntegerProperty inQueue, SimpleIntegerProperty fromServer, SimpleIntegerProperty finished, SimpleIntegerProperty candidates) {
        this.inQueue = inQueue;
        this.fromServer = fromServer;
        this.finished = finished;
        this.candidates = candidates;
    }

    @Override
    public void run() {
        /*String finalURL = HttpUrl
                .parse(Constants.UPDATE_AGENT_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.IN_QUEUE, Integer.toString(inQueue.get()))
                .addQueryParameter(Constants.FROM_SERVER, Integer.toString(fromServer.get()))
                .addQueryParameter(Constants.FINISHED, Integer.toString(finished.get()))
                .addQueryParameter(Constants.CANDIDATES, Integer.toString(candidates.get()))
                .build()
                .toString();
        HttpClientUtil.runAsync(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //bad

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //good? status maybe
            }
        });*/
    }
}
