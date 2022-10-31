package mainWindow.tasks;

import engine.bruteForce.BFDictionary;
import engine.bruteForce.Decipher;
import engine.bruteForce.Difficulty;
import javafx.concurrent.Task;
import machine.Machine;
import machine.Reflector;
import machine.Rotor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ProduceDecryptionTasks extends Task<Boolean> {

    private Machine enigma;

    private String savedEnigma;

    private BlockingQueue blockingQueue;

    private BlockingQueue resultQueue;

    private Difficulty difficulty;

    private String userInput;

    private int missionScope;

    private long totalTasks;


    private long posPermutation;

    private BFDictionary dictionary;

    private int agentsCount;

    private CustomThreadPoolExecutor threadPool;

    private MyLock lock;

    public ProduceDecryptionTasks(String savedEnigma, BlockingQueue blockingQueue, BlockingQueue resultQueue, Difficulty difficulty, String userInput, int missionScope, BFDictionary dictionary, int agentsCount, long totalTasks, MyLock lock) throws IOException, ClassNotFoundException {
        this.savedEnigma = savedEnigma;
        enigma = Decipher.readMachineFromString(savedEnigma);
        enigma.setupTempPlugs();
        enigma.applyChanges();
        this.agentsCount =agentsCount;
        this.blockingQueue = blockingQueue;
        this.resultQueue = resultQueue;
        this.difficulty = difficulty;
        this.userInput = userInput;
        this.missionScope = missionScope;
        this.dictionary =dictionary;
        posPermutation = (long)Math.pow(enigma.getABC().length(), enigma.getRotorsCount());
        posPermutation = posPermutation % missionScope == 0? posPermutation / missionScope: posPermutation / missionScope + 1;
        this.totalTasks = totalTasks;
        this.lock = lock;
    }

    public void writeMachineToString() throws IOException{
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(enigma);
        out.close();
        savedEnigma = Base64.getEncoder().encodeToString(bytes.toByteArray());
    }

    @Override
    protected Boolean call(){
        threadPool = new CustomThreadPoolExecutor(agentsCount, agentsCount, 5, TimeUnit.SECONDS, blockingQueue);
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
                case HARDEST:
                    createTasksHardest();
                    break;
            }
        }catch(TaskIsCanceledException e){
            threadPool.shutdownNow();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        boolean finish = false;
        while(!finish){
            try {
                if(isCancelled())
                    throw new TaskIsCanceledException();
                if (blockingQueue.isEmpty() && resultQueue.isEmpty())
                    finish = true;
            }catch(TaskIsCanceledException e){
                threadPool.shutdownNow();
            }
        }

        threadPool.shutdown();


        return Boolean.TRUE;
    }

    private void createTasksEasy(){
        long start = 0;
        long end = missionScope - 1;
        for (int i = 0; i < posPermutation; i++) {
            if(isCancelled()){
                throw new TaskIsCanceledException();
            }
            if(dictionary.isPause()){
               lock.paused();
            }
            try {
                blockingQueue.put(new BruteForceTask(userInput, savedEnigma, dictionary, start, end, resultQueue));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (TaskIsCanceledException e){
               throw e;
            }
            start+=missionScope;
            end+=missionScope;
            if(end >= totalTasks)
                end = totalTasks - 1;

        }

    }

    private void createTasksMedium() throws IOException{
        for (int i = 0; i < enigma.getReflectorsTotal(); i++) {
            if(isCancelled()){
                throw new TaskIsCanceledException();
            }
            if(dictionary.isPause())
                pause();
            enigma.pickTempReflector(Reflector.ReflectorID.values()[i].name());
            enigma.applyChanges();
            writeMachineToString();
            createTasksEasy();
        }
    }

    private void createTasksHard() throws IOException{
        Integer[] arr = new Integer[enigma.getRotorsCount()];
        int i = 0;
        for(Rotor r : enigma.getUsedRotors()) {
            if(isCancelled()){
                throw new TaskIsCanceledException();
            }
            if(dictionary.isPause())
                pause();
            if(r.getId() > 0){
                arr[i] = r.getId();
                i++;
            }
        }
        List<List<Integer>> permutations = new ArrayList<>();
        getPermutations(arr, arr.length, permutations);
        for(List<Integer> permutation: permutations){
            if(isCancelled()){
                throw new TaskIsCanceledException();
            }
            if(dictionary.isPause())
                pause();
            enigma.pickTempRotors(permutation);
            enigma.applyChanges();
            writeMachineToString();
            createTasksMedium();
        }

    }

    private void createTasksHardest() throws IOException{
        Integer[] arr = new Integer[enigma.getRotorsTotal()];
        for (int i = 0; i < enigma.getRotorsTotal(); i++) {
            arr[i] = i + 1;
        }
        List<List<Integer>> subsets = findAllSubsetsSizeRShell(arr, arr.length, enigma.getRotorsCount());
        for(List<Integer> subset: subsets){
            if(isCancelled()){
                throw new TaskIsCanceledException();
            }
            if(dictionary.isPause())
                pause();
            enigma.pickTempRotors(subset);
            enigma.applyChanges();
            writeMachineToString();
            createTasksHard();
        }


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

    public void findAllSubsetsSizeR(Integer[] arr, int n, int r, int index, Integer[] data, int i, List<List<Integer>> result){
        if(index == r) {
            result.add(new ArrayList<>(Arrays.asList(data)));
            return;
        }
        if(i >= n)
            return;
        data[index] = arr[i];
        findAllSubsetsSizeR(arr, n, r, index + 1, data, i + 1, result);
        findAllSubsetsSizeR(arr, n, r, index, data, i + 1, result);

    }

    public List<List<Integer>> findAllSubsetsSizeRShell(Integer[] arr, int n, int r){
        List<List<Integer>> result = new ArrayList<>();
        Integer[] data = new Integer[r];
        findAllSubsetsSizeR(arr, n, r, 0, data, 0, result);
        return result;
    }

    public void pause(){
        synchronized (lock){
            try{
                threadPool.pause();
                while(dictionary.isPause())
                    lock.wait();
                System.out.println("prod is paused");
                threadPool.resume();
            }catch(InterruptedException e){

            }
        }
    }
}
