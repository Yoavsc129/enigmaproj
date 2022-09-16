package engine.bruteForce.tasks;

import engine.bruteForce.BFDictionary;
import engine.bruteForce.CustomThreadPoolExecutor;
import engine.bruteForce.Decipher;
import engine.bruteForce.Difficulty;
import machine.Machine;
import machine.Reflector;
import machine.Rotor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TestingClass implements Runnable{

    Machine enigma;

    String savedEnigma;

    BlockingQueue blockingQueue;

    Difficulty difficulty;

    String abc;

    String userInput;

    int missionScope;

    long totalTasks;

    long missionCount;

    long posPermutation;

    BFDictionary dictionary;

    private int agentsCount;

    public TestingClass(String savedEnigma, BlockingQueue blockingQueue, Difficulty difficulty, String abc, String userInput, int missionScope, BFDictionary dictionary, int agentsCount) throws IOException, ClassNotFoundException {
        this.savedEnigma = savedEnigma;
        enigma = Decipher.readMachineFromString(savedEnigma);
        this.agentsCount =agentsCount;
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
        posPermutation = res = (long)Math.pow(abc.length(), enigma.getRotorsCount());
        posPermutation = posPermutation % missionScope == 0? posPermutation / missionScope: posPermutation / missionScope + 1;
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
    public void writeMachineToString() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(enigma);
        out.close();
        savedEnigma = Base64.getEncoder().encodeToString(bytes.toByteArray());
    }



    @Override
    public void run() {
        System.out.println("Let's ROCK!");
        CustomThreadPoolExecutor threadPool = new CustomThreadPoolExecutor(2, agentsCount, 5, TimeUnit.SECONDS, blockingQueue);
        threadPool.prestartAllCoreThreads();
        try {
            switch (difficulty) {
                case EASY:
                    createTasksEasy();
                    break;
                case MEDIUM:
                    createTasksMedium();
                    break;
                case HARD:
                    createTasksHard();
                    break;
            }
        }catch(Exception e){
            System.out.println(e.getMessage()); // something nicer
        }
        boolean finish = false;
        while(!finish){
            if(blockingQueue.isEmpty())
                finish = true;
        }
        threadPool.shutdown();

    }

    private void createTasksEasy(){
        long start = 0;
        long end = missionScope - 1;
        for (int i = 0; i < posPermutation; i++) {
            try {
                blockingQueue.offer(new BruteForceTask(userInput, savedEnigma, dictionary, start, end ));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            start+=missionScope;
            end+=missionScope;
            if(end >= totalTasks)
                end = totalTasks - 1;

        }

    }

    private void createTasksMedium() throws IOException, ClassNotFoundException {
        for (int i = 0; i < enigma.getReflectorsTotal(); i++) {
            enigma.pickTempReflector(Reflector.ReflectorID.values()[i].name());
            enigma.applyChanges();
            writeMachineToString();
            createTasksEasy();
        }
    }

    private void createTasksHard() throws IOException, ClassNotFoundException {
        Integer[] arr = new Integer[enigma.getRotorsCount()];
        int i = 0;
        for(Rotor r : enigma.getUsedRotors()) {
            if(r.getId() > 0){
                arr[i] = r.getId();
                i++;
            }
        }
        List<List<Integer>> permutations = new ArrayList<>();
        getPermutations(arr, arr.length, permutations);
        for(List<Integer> permutation: permutations){
            enigma.pickTempRotors(permutation);
            enigma.applyChanges();
            writeMachineToString();
            createTasksMedium();
        }

    }

    private void createTasksHardest(){

    }

    private static void getPermutations(Integer[] arr, int size, List<List<Integer>> res){
        if(size == 1){
            res.add(new ArrayList<>(Arrays.asList(arr)));
        }
        for (int i = 0; i < size; i++) {
            getPermutations(arr, size - 1, res);
            if(size%2 == 1)
                swap(arr, 0, size - 1);
            else
                swap(arr, i, size - 1);
        }
    }
    private static void swap(Integer[] arr, int i, int j){
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
