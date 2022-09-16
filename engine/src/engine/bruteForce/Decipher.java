package engine.bruteForce;

import engine.bruteForce.tasks.ProduceDecryptionTasks;
import engine.bruteForce.tasks.TestingClass;
import javafx.concurrent.Task;
import machine.Machine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.concurrent.*;

public class Decipher {

    private int agentsCount;


    private BFDictionary bfDictionary;

    private String savedEnigma;

    private Machine enigma;

    private String userInput;

    private int missionScope = 1;





    public Decipher(int agentsCount, String dictionary, String excludedChars) {
        this.agentsCount = agentsCount;
        Set<String> words = new HashSet<>();
        List<Character> excludedCharsList = new ArrayList<>();
        String[] parsedDictionary = dictionary.trim().split(" ");
        for(char c : excludedChars.toCharArray())
            excludedCharsList.add(c);
        for(String word: parsedDictionary) {
            word = removeExcludedChars(word, excludedCharsList);
            words.add(word.toUpperCase());
        }
        bfDictionary = new BFDictionary(words, excludedCharsList);
    }

    private String removeExcludedChars(String word, List<Character> excludedChars){
        for(Character c: excludedChars){
            while(word.endsWith(c.toString()))
                word = word.substring(0,word.length() - 1);
            while(word.startsWith(c.toString()))
                word = word.substring(1);
        }

        return word;
    }

    public void setSavedEnigma(String savedEnigma) throws IOException, ClassNotFoundException {
        this.savedEnigma = savedEnigma;
        enigma = readMachineFromString(savedEnigma);
    }

    public static Machine readMachineFromString(String savedEnigma) throws IOException, ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode(savedEnigma);
        ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(data));
        Machine res  = (Machine) in.readObject();
        in.close();
        return res;
    }
    public void bruteForce(String userInput, Difficulty difficulty) throws IOException, ClassNotFoundException, InterruptedException {
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(1000);
        //CustomThreadPoolExecutor threadPool = new CustomThreadPoolExecutor(2, agentsCount, 5, TimeUnit.SECONDS, blockingQueue);
        Task<Boolean> task = new ProduceDecryptionTasks(savedEnigma, blockingQueue, difficulty, enigma.getABC(), userInput, missionScope, bfDictionary);
        Thread t = new Thread(new TestingClass(savedEnigma, blockingQueue, difficulty, enigma.getABC(), userInput, missionScope, bfDictionary, agentsCount));
        new Thread(task).start();
        //threadPool.prestartAllCoreThreads();
        t.start();
        /*threadPool.shutdown();
        threadPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);*/

    }

    public void setMissionScope(int missionScope) {
        this.missionScope = missionScope;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public boolean inDictionary(String word){
        word = bfDictionary.removeExcludedChars(word);
        return bfDictionary.inDictionary(word);
    }

    public int getAgentsCount() {
        return agentsCount;
    }

    public List<String> getWordsList(){
        return bfDictionary.getWordsList();
    }

    public List<String> dictionarySuggest(String prefix) {
        List<String> list = bfDictionary.dictionarySuggest(prefix);
        return list;
    }


}
