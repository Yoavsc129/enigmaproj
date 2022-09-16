package engine.bruteForce.tasks;

import engine.bruteForce.BFDictionary;
import engine.bruteForce.Decipher;
import engine.bruteForce.Difficulty;
import javafx.concurrent.Task;
import machine.Machine;

import java.io.IOException;
import java.math.*;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class ProduceDecryptionTasks extends Task<Boolean> {

    Machine enigma;

    String savedEnigma;

    BlockingQueue blockingQueue;

    Difficulty difficulty;

    String abc;

    String userInput;

    int missionScope;

    long totalTasks;

    long missionCount;

    BFDictionary dictionary;

    public ProduceDecryptionTasks(String savedEnigma, BlockingQueue blockingQueue, Difficulty difficulty, String abc, String userInput, int missionScope, BFDictionary dictionary) throws IOException, ClassNotFoundException {
        this.savedEnigma = savedEnigma;
        enigma = Decipher.readMachineFromString(savedEnigma);
        this.blockingQueue = blockingQueue;
        this.difficulty = difficulty;
        this.abc = abc;
        this.userInput = userInput;
        this.missionScope = missionScope;
        this.dictionary =dictionary;
        totalTasks = calculateTotalTasks();
        missionCount = totalTasks % missionScope == 0? totalTasks / missionScope: totalTasks / missionScope + 1;
    }

    private long calculateTotalTasks(){
            long res = 0;
            res = (long)Math.pow(abc.length(), enigma.getRotorsCount());
            if(difficulty != Difficulty.EASY) {
            res *= enigma.getReflectorsTotal();
            if (difficulty != Difficulty.MEDIUM){
                res *= factorial(enigma.getRotorsCount());
                if(difficulty != Difficulty.HARD)
                    res *= binomi(enigma.getRotorsTotal(), enigma.getRotorsCount());
            }

        }
        return res;
    }

    public static long factorial(int number) {
        long result = 1;

        for (int factor = 2; factor <= number; factor++) {
            result *= factor;
        }

        return result;
    }

    static long binomi(int n, int k) {
        if ((n == k) || (k == 0))
            return 1;
        else
            return binomi(n - 1, k) + binomi(n - 1, k - 1);
    }

    @Override
    protected Boolean call() throws Exception {
        //only easy
        System.out.println("Let's ROCK!");
        boolean added;
        long start = 0;
        long end = missionScope - 1;
        for (int i = 0; i < missionCount; i++) {
            added = blockingQueue.offer(new BruteForceTask(userInput, savedEnigma, dictionary, start, end ));
            if(!added)
                return null;
            System.out.println("Created a task");
            start+=missionScope;
            end+=missionScope;
            if(end >= totalTasks)
                end = totalTasks - 1;

        }


        return null;
    }
}
