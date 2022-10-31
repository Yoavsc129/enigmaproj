package mainWindow.tabs.encryptTab.subComp;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Key {
    private final KeyCode keyCode;
    private final BooleanProperty pressedProperty;

    public Key(final KeyCode keyCode) {
        this.keyCode = keyCode;
        this.pressedProperty = new SimpleBooleanProperty(this, "pressed");
    }

    public KeyCode getKeyCode() {
        return keyCode;
    }

    public boolean isPressed() {
        return pressedProperty.get();
    }

    public void setPressed(final boolean value) {
        pressedProperty.set(value);
    }

    public Node createNode() {
        final StackPane keyNode = new StackPane();
        keyNode.setFocusTraversable(true);
        installEventHandler(keyNode);

        final Rectangle keyBackground = new Rectangle(50, 50);
        keyBackground.fillProperty().bind(
                Bindings.when(pressedProperty)
                        .then(Color.RED)
                            .otherwise(Color.WHITE));
        keyBackground.setStroke(Color.BLACK);
        keyBackground.setStrokeWidth(2);
        keyBackground.setArcWidth(12);
        keyBackground.setArcHeight(12);

        final Text keyLabel = new Text(keyCode.getName());
        keyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        keyNode.getChildren().addAll(keyBackground, keyLabel);

        return keyNode;
    }

    private void installEventHandler(final Node keyNode) {
        final EventHandler<KeyEvent> keyEventHandler =
                keyEvent -> {
                    if (keyEvent.getCode() == keyEvent.getCode()) {
                        setPressed(keyEvent.getEventType()
                                == KeyEvent.KEY_PRESSED);
                        keyEvent.consume();
                    }
                };

        keyNode.setOnKeyPressed(keyEventHandler);
        keyNode.setOnKeyReleased(keyEventHandler);
    }
}
