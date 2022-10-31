package candidatesTable;

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
import java.util.function.Consumer;

public class CandidatesRefresher extends TimerTask {

    private final Consumer<CandidatesWithVersion> candidatesConsumer;

    private final Consumer<String> winnerConsumer;
    private final SimpleIntegerProperty version;

    private boolean ally;

    public CandidatesRefresher(Consumer<CandidatesWithVersion> candidatesConsumer, Consumer<String> winnerConsumer, SimpleIntegerProperty version, boolean ally) {
        this.candidatesConsumer = candidatesConsumer;
        this.winnerConsumer = winnerConsumer;
        this.version = version;
        this.ally = ally;
    }

    @Override
    public void run() {
        String finalURL;
        HttpUrl.Builder builder = HttpUrl
                .parse(Constants.GET_CANDIDATES_PAGE)
                .newBuilder()
                .addQueryParameter("version", String.valueOf(version.get()));
        if(!ally)
            builder.addQueryParameter("type", Constants.ALLY);
        finalURL = builder.build().toString();


        HttpClientUtil.runAsync(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //Holy sh-
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String rawBody = response.body().string();
                String[] check = rawBody.split(" ");
                if(check[0].equals("DONE")){
                    winnerConsumer.accept(check[1]);
                }
                else{
                CandidatesWithVersion candidatesWithVersion = Constants.GSON_INSTANCE.fromJson(rawBody, CandidatesWithVersion.class);
                candidatesConsumer.accept(candidatesWithVersion);
                }
            }
        });
    }
}
