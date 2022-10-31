package client.component.tabs.dashboardTab.subComp.agentsTable;

import com.google.gson.Gson;
import engine.serverLogic.Battlefield;
import engine.serverLogic.users.Agent;
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

public class AgentListRefresher extends TimerTask {
    private final Consumer<List<Agent>> agentListConsumer;

    public AgentListRefresher(Consumer<List<Agent>> agentListConsumer) {
        this.agentListConsumer = agentListConsumer;
    }

    @Override
    public void run() {
        HttpClientUtil.runAsync(Constants.AGENT_LIST_PAGE, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //something went wrong?
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfAgents = response.body().string();
                Gson gson = new Gson();
                Agent[] agents = gson.fromJson(jsonArrayOfAgents, Agent[].class);
                agentListConsumer.accept(Arrays.asList(agents));
            }
        });

    }
}
