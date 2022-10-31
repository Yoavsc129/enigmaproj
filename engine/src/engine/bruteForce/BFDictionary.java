package engine.bruteForce;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BFDictionary {

    private Trie words;

    private List<String> wordsList;

    private List<Character> excludedChars;

    private float averageTime = 0;

    private long tasksDone = 0;

    private boolean pause = false;

    public BFDictionary(Collection<String> words, List<Character> excludedChars) {
        this.words = new Trie(words);
        this.excludedChars = excludedChars;
        wordsList = new ArrayList<>(words);
        wordsList.sort(null);
    }

    public boolean inDictionary(String word){
        return words.find(word, true);
    }

    public String removeExcludedChars(String word){
        if(word.length() > 0) {
            while (excludedChars.contains(word.charAt(word.length() - 1))){
                word = word.substring(0, word.length() - 1);
                if(word.length() == 0)
                    return word;
            }
            while (excludedChars.contains(word.charAt(0))){
                word = word.substring(1);
                if(word.length() == 0)
                    return word;
            }
        }

        return word;
    }

    public List<String> getWordsList() {
        return wordsList;
    }

    public List<String> dictionarySuggest(String prefix) {
        if(prefix == "")
            return wordsList;
        else return words.suggest(prefix);
    }

    public synchronized void calculateNewAverage(long time){

        tasksDone +=1;
        averageTime = (averageTime + time)/tasksDone;

    }

    public float getAverageTime() {
        return averageTime;
    }

    public long getTasksDone() {
        return tasksDone;
    }

    public void reset(){
        averageTime = 0;
        tasksDone = 0;
    }

    public void pause(){
        pause = true;
    }
    public void resume(){
        pause = false;
    }

    public boolean isPause() {
        return pause;
    }
}
