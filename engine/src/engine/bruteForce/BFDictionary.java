package engine.bruteForce;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BFDictionary {

    private Trie words;

    private List<String> wordsList;

    private List<Character> excludedChars;

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
        while(excludedChars.contains(word.charAt(word.length() - 1)))
            word = word.substring(0,word.length() - 1);
        while(excludedChars.contains(word.charAt(0)))
            word = word.substring(1);

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
}
