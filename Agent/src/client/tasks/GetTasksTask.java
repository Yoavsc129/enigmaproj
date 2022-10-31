package client.tasks;

import engine.bruteForce.tasks.DecodeTask;
import engine.bruteForce.tasks.MyLock;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class GetTasksTask implements Runnable{

    private final Consumer<Integer> fromServerConsumer;

    private final Consumer<String> winnerConsumer;

    private BlockingQueue tasksQueue;

    private BlockingQueue resultsQueue;

    private int missionCount;

    private int counter;

    private MyLock lock;

    private List<DecodeTask> tasks;

    int tasksPulled = 0;

    public GetTasksTask(Consumer<Integer> fromServerConsumer, Consumer<String> winnerConsumer, BlockingQueue tasksQueue, BlockingQueue resultsQueue, int missionCount, MyLock lock) {
        this.fromServerConsumer = fromServerConsumer;
        this.winnerConsumer = winnerConsumer;
        this.tasksQueue = tasksQueue;
        this.resultsQueue = resultsQueue;
        this.missionCount = missionCount;
        this.lock = lock;
        tasks = new ArrayList<>();
        counter = 1;

    }

    @Override
    public void run() {
        Boolean run = true;
        while(run){
            if(lock.getCounter() > 0)
                lock.paused();
            getTasks();
            Constants.sleepForAWhile(200);
            if(tasks == null)
                break;
            lock.setCounter(tasks.size());
            for (DecodeTask task : tasks){
                task.threadSetup(resultsQueue, lock);
                tasksQueue.add(task);
            }
            tasksPulled+=tasks.size();
            tasks = new ArrayList<>();
            Platform.runLater(()->{
                fromServerConsumer.accept(tasksPulled);
            });

            //work!
            //if no more missions -> run = false

        }
    }

    private void getTasks(){
        String finalURL = HttpUrl.parse(Constants.GET_TASKS_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.MISSION_COUNT, Integer.toString(missionCount))
                .build()
                .toString();
        HttpClientUtil.runAsync(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //evil
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String rawBody = response.body().string();
                String[] check = rawBody.split(" ");
                if(check[0].equals("DONE")){
                    tasks = null;
                    winnerConsumer.accept(check[1]);
                }
                else{
                DecodeTask[] tasksArray = Constants.GSON_INSTANCE.fromJson(rawBody, DecodeTask[].class);
                tasks = Arrays.asList(tasksArray);
                }
            }
        });
    }
}
