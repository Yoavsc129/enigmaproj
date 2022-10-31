package client.component.tabs.contestTab.agentData;

import engine.serverLogic.users.AgentProgress;
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

public class AgentsProgressRefresher extends TimerTask {

    private final Consumer<List<AgentProgress>> agentProgressConsumer;

    public AgentsProgressRefresher(Consumer<List<AgentProgress>> agentProgressConsumer) {
        this.agentProgressConsumer = agentProgressConsumer;
    }

    @Override
    public void run() {
        HttpClientUtil.runAsync(Constants.AGENT_PROGRESS_PAGE, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //something blue
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String rawBody = response.body().string();
                AgentProgress[] agentProgressArr = Constants.GSON_INSTANCE.fromJson(rawBody, AgentProgress[].class);
                agentProgressConsumer.accept(Arrays.asList(agentProgressArr));
            }
        });
    }
}
