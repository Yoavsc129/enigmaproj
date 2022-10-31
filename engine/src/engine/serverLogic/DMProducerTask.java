package engine.serverLogic;

import engine.bruteForce.BFDictionary;
import engine.bruteForce.Difficulty;
import engine.bruteForce.tasks.DecodeTask;
import machine.Machine;
import machine.Reflector;
import machine.Rotor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class DMProducerTask implements Runnable{

    private final int TASK_LIMIT = 300;

    private List<DecodeTask> currTasks;

    private Machine enigma;

    private Difficulty difficulty;

    private String userInput;

    private int missionScope;

    private int totalTasks;


    private int posPermutation;

    private BFDictionary dictionary;

    BlockingQueueWraper wrapedBlockingQueue;

    public DMProducerTask(Machine enigma, Difficulty difficulty, String userInput, int missionScope, int totalTasks, int posPermutation, BFDictionary dictionary, BlockingQueueWraper wrapedBlockingQueue) {
        this.enigma = enigma;
        this.difficulty = difficulty;
        this.userInput = userInput;
        this.missionScope = missionScope;
        this.totalTasks = totalTasks;
        this.posPermutation = posPermutation;
        this.dictionary = dictionary;
        this.wrapedBlockingQueue = wrapedBlockingQueue;
        currTasks = new ArrayList<>();
    }

    @Override
    public void run() {
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
            System.out.println(e.getMessage());
        }
        while(!currTasks.isEmpty()){
            currTasks = wrapedBlockingQueue.addTasks(currTasks);
        }
        //all done
    }

    private void createTasksEasy(){
        int start = 0;
        int end = missionScope - 1;
        for (int i = 0; i < posPermutation; i++) {
            currTasks.add(new DecodeTask(userInput, enigma, dictionary, start, end));
            if(currTasks.size() >= TASK_LIMIT){
                currTasks = wrapedBlockingQueue.addTasks(currTasks);
            }
            start+=missionScope;
            end+=missionScope;
            if(end >= totalTasks)
                end = totalTasks - 1;

        }

    }
    private void createTasksMedium() throws IOException{
        for (int i = 0; i < enigma.getReflectorsTotal(); i++) {
            enigma.pickTempReflector(Reflector.ReflectorID.values()[i].name());
            enigma.applyChanges();
            createTasksEasy();
        }
    }

    private void createTasksHard() throws IOException{
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
            createTasksMedium();
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

    /*public void findAllSubsetsSizeR(Integer[] arr, int n, int r, int index, Integer[] data, int i, List<List<Integer>> result){
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
    }*/
}
