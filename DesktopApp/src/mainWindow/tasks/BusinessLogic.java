package mainWindow.tasks;

import engine.bruteForce.Decipher;
import engine.bruteForce.Difficulty;
import javafx.concurrent.Task;
import machine.Machine;
import mainWindow.tabs.bruteTab.BruteTabController;
import mainWindow.tabs.bruteTab.subComp.UIAdapter;
import mainWindow.tabs.bruteTab.subComp.agentResults.AgentResult;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class BusinessLogic {

    private Decipher decipher;

    private BruteTabController bruteTabController;

    private Task<Boolean> currentProducingTask;

    private Task<Boolean> currentResultsTask;

    private long totalTasks;

    private long missionCount;

    private MyLock prodLock;

    private MyLock resLock;

    public BusinessLogic(BruteTabController bruteTabController) {
        this.bruteTabController = bruteTabController;
    }

    public void bruteForce(Difficulty difficulty, String userInput, int missionScope, int agentCount, UIAdapter uiAdapter, Runnable onFinish) throws IOException, ClassNotFoundException {
        prodLock = new MyLock();
        resLock = new MyLock();

        calculateTotalTasks(difficulty, missionScope);
        decipher.getBfDictionary().reset();
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(1000);
        BlockingQueue<AgentResult> resultBlockingQueue = new LinkedBlockingQueue<>();
        currentProducingTask = new ProduceDecryptionTasks(decipher.getSavedEnigma(), blockingQueue, resultBlockingQueue, difficulty, userInput, missionScope, decipher.getBfDictionary(), agentCount, totalTasks, prodLock);
        currentResultsTask = new GetResultsTask(resultBlockingQueue, missionCount, uiAdapter, decipher.getBfDictionary(), resLock);
        bruteTabController.bindTaskToUIComponents(currentResultsTask, onFinish);
        new Thread(currentProducingTask).start();
        new Thread(currentResultsTask).start();
    }

    public synchronized void pause(){
        decipher.getBfDictionary().pause();
    }

    public void resume(){
        decipher.getBfDictionary().resume();
        prodLock.unPause();
        resLock.unPause();

    }

    public void stop(){
        decipher.getBfDictionary().resume();
        currentResultsTask.cancel();
        currentProducingTask.cancel();

    }

    public void setDecipher(Decipher decipher) {
        this.decipher = decipher;
    }

    private void calculateTotalTasks(Difficulty difficulty, int missionScope) throws IOException, ClassNotFoundException {
        Machine enigma = decipher.readMachineFromString(decipher.getSavedEnigma());

        totalTasks = (long)Math.pow(enigma.getABC().length(), enigma.getRotorsCount());

        if(difficulty != Difficulty.EASY) {
            totalTasks *= enigma.getReflectorsTotal();
            if (difficulty != Difficulty.MEDIUM){
                totalTasks *= factorial(enigma.getRotorsCount());
                if(difficulty != Difficulty.HARD)
                    totalTasks *= binomi(enigma.getRotorsTotal(), enigma.getRotorsCount());
            }

        }
        missionCount = totalTasks % missionScope == 0? totalTasks / missionScope: totalTasks / missionScope + 1;
    }

    public static long factorial(int number) {
        int result = 1;

        for (int factor = 2; factor <= number; factor++) {
            result *= factor;
        }

        return result;
    }

    public static int binomi(int n, int k) {
        if ((n == k) || (k == 0))
            return 1;
        else
            return binomi(n - 1, k) + binomi(n - 1, k - 1);
    }
}
