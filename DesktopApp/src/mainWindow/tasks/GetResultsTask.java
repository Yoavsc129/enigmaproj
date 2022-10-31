package mainWindow.tasks;

import engine.bruteForce.BFDictionary;
import javafx.concurrent.Task;
import mainWindow.tabs.bruteTab.subComp.UIAdapter;
import mainWindow.tabs.bruteTab.subComp.agentResults.AgentResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class GetResultsTask extends Task<Boolean> {

    private BlockingQueue resultQueue;

    private long missionCount;

    private UIAdapter uiAdapter;

    private Map<String, AgentResult> results;

    private BFDictionary dictionary;

    private boolean isCanceled = false;

    private MyLock lock;




    public GetResultsTask(BlockingQueue resultQueue, long missionCount, UIAdapter uiAdapter, BFDictionary dictionary, MyLock resLock) {
        this.resultQueue = resultQueue;
        this.missionCount = missionCount;
        this.uiAdapter = uiAdapter;
        this.dictionary = dictionary;
        lock = resLock;
        results = new HashMap<>();
    }


    @Override
    protected Boolean call(){
        updateProgress(0, missionCount);
        AgentResult result;
        AgentResult existing;

        do{
            try {
                if(dictionary.isPause())
                    lock.paused();
                if(isCancelled())
                    throw new TaskIsCanceledException();


                result = (AgentResult) resultQueue.poll();

                updateProgress(dictionary.getTasksDone(), missionCount);
                long SLEEP_TIME = 10;
                Utils.sleepForAWhile(SLEEP_TIME);
                if (result != null) {
                    existing = results.get(result.getAgentName());
                    if (existing == null) {
                        existing = new AgentResult(result.getAgentName(), result.getTime(), result.getResults());
                        results.put(existing.getAgentName(), existing);
                        uiAdapter.addNewAgent(existing);
                    } else {
                        uiAdapter.updateAgent(result);
                    }
                }
                Utils.sleepForAWhile(SLEEP_TIME);
            }catch(TaskIsCanceledException e){
                System.out.println("Canceled");
                isCanceled = true;
            }
        }while (dictionary.getTasksDone() < missionCount && !isCanceled);

        updateProgress(dictionary.getTasksDone(), missionCount);
        System.out.println("all done");


        return Boolean.TRUE;
    }

}
