package dream.fcard.model.cards;

import dream.fcard.util.json.JsonInterface;
import dream.fcard.util.json.jsontypes.JsonValue;
import javafx.scene.Node;

/**
 * Card that evaluates input as javascript code whose output has to match back of card.
 */
public class JavascriptCard implements FlashCard, JsonInterface {

    protected String front;
    protected String output;

    public JavascriptCard(String frontString, String outputString) {
        front = frontString;
        output = outputString;
    }

    @Override
    public String getFront() {
        return front;
    }

    @Override
    public String getBack() {
        return output;
    }

    @Override
    public JsonValue toJson() {
        return null;
    }

    @Override
    public Node renderFront() {
        return null;
    }

    @Override
    public Node renderBack() {
        return null;
    }

    @Override
    public Boolean evaluate(String in) {
        return null;
    }
}
