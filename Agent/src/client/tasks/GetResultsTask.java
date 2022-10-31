package client.tasks;

import engine.bruteForce.tasks.MissionResult;
import javafx.application.Platform;
import util.Constants;

import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class GetResultsTask extends TimerTask {
    private final Consumer<MissionResult> missionResultConsumer;

    private BlockingQueue resultQueue;

    private Boolean canceled;

    public GetResultsTask(Consumer<MissionResult> missionResultConsumer, BlockingQueue resultQueue, Boolean canceled) {
        this.missionResultConsumer = missionResultConsumer;
        this.resultQueue = resultQueue;
        this.canceled = canceled;
    }

    @Override
    public void run() {
        MissionResult temp;
        temp = (MissionResult) resultQueue.poll();

        if(temp!=null){
            //System.out.println(temp.getResults());
            Platform.runLater(()->{
                missionResultConsumer.accept(temp);
            });
        }
        Constants.sleepForAWhile(200);
    }
}
