package mainWindow.tasks;

import engine.bruteForce.BFDictionary;
import engine.bruteForce.Decipher;

import machine.Machine;
import mainWindow.tabs.bruteTab.subComp.agentResults.AgentResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;


public class BruteForceTask implements Runnable{

    String userInput;

    String SavedEnigma;

    Machine enigma;

    char[] abc;

    BFDictionary dictionary;

    long start;

    long finish;

    BlockingQueue resultQueue;





    public BruteForceTask(String userInput, String savedEnigma, BFDictionary dictionary, long start, long finish, BlockingQueue resultQueue) throws IOException, ClassNotFoundException {
        this.userInput = userInput;
        SavedEnigma = savedEnigma;
        enigma = Decipher.readMachineFromString(savedEnigma);
        abc = enigma.getABC().toCharArray();
        this.dictionary = dictionary;
        this.start = start;
        this.finish = finish;
        this.resultQueue = resultQueue;

    }
//A finish queue


    @Override
    public void run() {
        long missionScope = finish - start + 1;
        String decodedInput;
        String[] words;
        List<String> results = new ArrayList<>();
        Long taskStart = System.nanoTime();
        for (int i = 0; i < missionScope; i++) {
            enigma.setInitialTempRotorsPosition(getPositions(start));
            start++;
            decodedInput = enigma.encodeStringNoPlugs(userInput).trim();
            words = decodedInput.split(" ");//Space could be a constant
            if(checkWords(words)) {
                results.add(enigma.getMachineSpecs().format());
                results.add(decodedInput);
            }
        }
        long taskEnd = System.nanoTime();
        dictionary.calculateNewAverage(taskEnd - taskStart);
        if(results.size() > 0) {
            try {
                resultQueue.put(new AgentResult(Thread.currentThread().getName(), taskEnd - taskStart, results));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        //System.out.println("Task Done!");
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
}
