package engine.serverLogic;

import engine.Engine;
import engine.bruteForce.Difficulty;
import engine.bruteForce.tasks.DecodeTask;
import machine.Machine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DM {
    private final String name;

    private Engine engine;

    private final int missionScope;

    private final int totalTasks;

    private final int missionCount;

    private int posPermutation;

    private Difficulty difficulty;

    private String userInput;

    private DMProducerTask producerTask;

    private Thread workThread;

    private BlockingQueueWraper wrapedBlockingQueue;

    private Boolean finish;

    public DM(String name, Engine engine, int missionScope, int totalTasks, int posPermutations, Difficulty difficulty, String userInput) {
        this.name = name;
        this.engine = engine;
        this.missionScope = missionScope;
        this.totalTasks = totalTasks;
        this.difficulty = difficulty;
        this.userInput = userInput;
        posPermutation = posPermutations;
        missionCount = totalTasks % missionScope == 0? totalTasks / missionScope: totalTasks / missionScope + 1;
        posPermutation = posPermutation % missionScope == 0? posPermutation / missionScope: posPermutation / missionScope + 1;
        finish = Boolean.FALSE;
        wrapedBlockingQueue = new BlockingQueueWraper();
    }

    public void produceTasks(){
        producerTask = new DMProducerTask(engine.getEnigma(), difficulty, userInput, missionScope, totalTasks, posPermutation, engine.getDecipher().getBfDictionary(), wrapedBlockingQueue);
        workThread = new Thread(producerTask);
        workThread.start();
    }


    public synchronized List<DecodeTask> getTasks(int missionCount){
        List<DecodeTask> tasks;
        tasks = wrapedBlockingQueue.getTasks(missionCount);
        return tasks;
    }

    public int getMissionCount() {
        return missionCount;
    }

    public void endContest(){
        workThread.interrupt();
    }
}
