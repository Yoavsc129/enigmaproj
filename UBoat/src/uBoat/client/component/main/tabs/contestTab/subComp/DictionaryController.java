package uBoat.client.component.main.tabs.contestTab.subComp;

import engine.Engine;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DictionaryController {

    @FXML
    private TextField searchTF;

    @FXML
    private FlowPane wordsFP;

    private final Map<String, DictionaryWord> words;

    Engine engine;

    public DictionaryController() {
        words = new HashMap<>();
    }

    public void setupDictionary(){
        List<String> words = engine.getWordsList();
        for(String word: words){
            DictionaryWord newWord = new DictionaryWord(word);
            this.words.put(word, newWord);
            wordsFP.getChildren().add(newWord.getWordNode());
        }
    }

    @FXML
    void searchAction(KeyEvent event) {
        String prefix = searchTF.getText() + event.getCharacter();
        if(event.getCharacter().equals("\b"))
            prefix = prefix.substring(0, prefix.length() - 1);
        List<String> suggestions = engine.dictionarySuggest(prefix.toUpperCase());
        List<Node> nodes = new ArrayList<>();
        for(String word: suggestions)
            nodes.add(words.get(word).getWordNode());


        wordsFP.getChildren().setAll(nodes);
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }
}
