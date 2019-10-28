package dream.fcard.model.cards;

import dream.fcard.logic.storage.Schema;
import dream.fcard.util.json.exceptions.JsonWrongValueException;
import dream.fcard.util.json.jsontypes.JsonArray;
import dream.fcard.util.json.jsontypes.JsonObject;
import dream.fcard.util.json.jsontypes.JsonValue;
import java.util.ArrayList;

import dream.fcard.model.TestCase;

/**
 * Card that evaluates input as javascript code whose output has to match back of card.
 */
public class JavaCard extends FlashCard {

    private String question;
    private ArrayList<TestCase> testCases;

    public JavaCard(String question, ArrayList<TestCase> testCases) {
        this.question = question;
        this.testCases = testCases;
    }

    @Override
    public Boolean evaluate(String in) {
        //TODO: Evaluating Java code can be done inside here
        return null;
    }

    @Override
    public String getFront() {
        return question;
    }

    @Override
    public String getBack() {
        //irrelevant
        return null;
    }

    @Override
    public JsonValue toJson() {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        for(TestCase t : testCases) {
            try {
                arr.add(t.toJson().getObject());
            } catch(JsonWrongValueException e) {
                System.out.println("testcase is expected to be an object");
            }
        }
        obj.put(Schema.FRONT_FIELD, question);
        obj.put(Schema.JAVA_CASES, arr);
        return new JsonValue(obj);
    }

    @Override
    public void editFront(String newText) {
        //irrelevant
    }

    @Override
    public void editBack(String newText) {
        //irrelevant
    }

    @Override
    public boolean hasChoices() {
        return false;
    }
}
