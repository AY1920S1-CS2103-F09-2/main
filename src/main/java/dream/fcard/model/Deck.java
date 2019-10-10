package dream.fcard.model;

import java.util.ArrayList;

import dream.fcard.model.cards.FlashCard;
import dream.fcard.util.json.JsonInterface;
import dream.fcard.util.json.jsontypes.JsonValue;

import javafx.scene.Node;

/**
 * Collection of cards.
 */
public class Deck implements JsonInterface {
    private String deckName;
    private ArrayList<FlashCard> cards;

    // Testing sample deck with no name
    // can remove subsequently
    public Deck() {
        cards = new ArrayList<>();
        deckName = "untitled";
    }

    public Deck(String name) {
        cards = new ArrayList<>();
        deckName = name;
    }

    public Deck(ArrayList<FlashCard> initialCards, String name) {
        cards = initialCards;
        deckName = name;
    }

    public String getDeckName() {
        return deckName;
    }

    /**
     * Adds new card to the ArrayList of card objects.
     */
    public void addNewCard(FlashCard newCard) {
        cards.add(newCard);
    }

    @Override
    public JsonValue toJson() {
        return null;
    }

    /**
     * Render all the cards of the deck in a list.
     *
     * @return node of list
     */
    public Node renderListView() {
        return null;
    }

    /**
     * Render tile icon of this deck to display alongside other decks.
     *
     * @return node of tile
     */
    public Node renderTileView() {
        return null;
    }
}