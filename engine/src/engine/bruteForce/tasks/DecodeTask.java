package engine.bruteForce.tasks;

import engine.bruteForce.BFDictionary;
import machine.Machine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class DecodeTask implements Runnable{

    private String userInput;

    private Machine enigma;

    private char[] abc;

    private BFDictionary dictionary;

    private int start;

    private int finish;

    private BlockingQueue resultQueue;

    private MyLock lock;



    public DecodeTask(String userInput, Machine enigma, BFDictionary dictionary, int start, int finish) {
        this.userInput = userInput;
        this.enigma = enigma;
        abc = enigma.getABC().toCharArray();
        this.dictionary = dictionary;
        this.start = start;
        this.finish = finish;
    }

    public void threadSetup(BlockingQueue resultQueue, MyLock lock){
        this.resultQueue = resultQueue;
        this.lock = lock;
    }


    @Override
    public void run() {
        int missionScope = finish - start + 1;
        String decodedInput;
        String[] words;
        //List<String> results = new ArrayList<>();
        List<TaskResult> taskResults = new ArrayList<>();
        String positions;
        for (int i = 0; i < missionScope; i++) {
            positions = getPositions(start);
            enigma.setInitialTempRotorsPosition(positions);
            enigma.applyChanges();
            start++;
            decodedInput = enigma.encodeStringNoPlugs(userInput).trim();
            //System.out.println(decodedInput);
            words = decodedInput.split(" ");//Space could be a constant
            if(checkWords(words)) {
                /*results.add(enigma.getMachineSpecs().format());
                results.add(decodedInput);*/
                taskResults.add(new TaskResult(decodedInput, enigma.getMachineSpecs().format()));
            }
        }
        try {
            resultQueue.put(new MissionResult(taskResults, missionScope));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        lock.countdown();


    }

    private String getPositions(long count){
        StringBuilder res = new StringBuilder();

        while(count > 0){
            res.insert(0, abc[(int)(count % abc.length)]);
            count = count/ abc.length;
        }
        for (int i = res.length(); i < enigma.getRotorsCount(); i++)
            res.insert(0, abc[0]);


        return res.toString();
    }

    private boolean checkWords(String[] words){
        for(String s : words){
            s = dictionary.removeExcludedChars(s);
            if(!dictionary.inDictionary(s))
                return false;
        }
        return true;
    }

    public int getStart() {
        return start;
    }
}
