package mainWindow.tabs.bruteTab.subComp;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class DictionaryWord {

    private String word;

    private Node wordNode;

    public DictionaryWord(String word) {
        this.word = word;
        wordNode = createNode();
    }

    public Node createNode(){
        final StackPane wordNode = new StackPane();
        final Rectangle wordBackground = new Rectangle(100, 50);
        wordBackground.setFill(Color.WHITE);
        wordBackground.setStroke(Color.BLACK);
        wordBackground.setStrokeWidth(2);
        wordBackground.setArcWidth(12);
        wordBackground.setArcHeight(12);

        final Text wordLabel = new Text(word);
        wordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        wordNode.getChildren().addAll(wordBackground, wordLabel);

        return wordNode;
    }

    public Node getWordNode() {
        return wordNode;
    }
}
