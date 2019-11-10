//@@author nattanyz
package dream.fcard.logic.stats;

import dream.fcard.logic.storage.Schema;
import dream.fcard.util.json.JsonInterface;
import dream.fcard.util.json.exceptions.JsonWrongValueException;
import dream.fcard.util.json.jsontypes.JsonArray;
import dream.fcard.util.json.jsontypes.JsonObject;
import dream.fcard.util.json.jsontypes.JsonValue;
import java.util.HashMap;

/**
 * Contains statistics pertaining to the user's deck test sessions.
 */
public class DeckStats extends Stats implements JsonInterface {

    /** Data structure mapping decks to their corresponding SessionLists. */
    private HashMap<String, SessionList> deckHashMap;

    /** The current Session the user is engaging in. */
    private Session currentSession;

    /** The name of the current Deck the user is testing themselves on, if any. */
    private String currentDeck;

    /** Constructs a new instance of DeckStats with no stored data. */
    public DeckStats() {
        this.deckHashMap = new HashMap<>();
        this.currentSession = null;
        this.currentDeck = null;
        logger.info("New DeckStats object created.");
    }

    /** Gets the hash map containing session lists for all decks. */
    public HashMap<String, SessionList> getDeckHashMap() {
        return this.deckHashMap;
    }

    /** Ends the current session, and sets its score. */
    public void endCurrentSession(String score) {
        this.currentSession.setScore(score);
        endCurrentSession();
    }

    /** Sets the name of the deck under test. */
    public void setCurrentDeck(String deckName) {
        this.currentDeck = deckName;
    }

    /** Gets the name of the deck under test, if any. */
    public String getCurrentDeck() {
        return this.currentDeck;
    }

    /** Resets the name of the deck under test, when no test is being conducted. */
    public void resetCurrentDeck() {
        this.currentDeck = null;
    }

    @Override
    public void startCurrentSession() {
        // assert that currentDeck is not null?
        String currentDeck = this.getCurrentDeck();
        if (currentDeck == null) {

        }

        // replace with assert?
        if (this.currentSession != null) {
            endCurrentSession(); // should not occur, but should terminate just in case
            logger.info("Existing test session detected. Terminating it first...");
        }

        logger.info("Starting a test session of deck " + currentDeck + "...");

        this.currentSession = new Session();
    }

    @Override
    public void endCurrentSession() {
        // assert that currentDeck is not null?
        String currentDeck = this.getCurrentDeck();
        if (currentDeck == null) {

        }

        if (this.currentSession == null) {
            logger.info("Current test session not found!");
            return;
        }

        try {
            this.currentSession.endSession();
            this.addSessionToDeckSessionList(this.currentSession, currentDeck);

            // reset currentSession to null since this is terminated
            this.currentSession = null;

            logger.info("Ending the current test session...");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Current test session not found?");
        }
    }

    @Override
    public Session getCurrentSession() {
        return this.currentSession;
    }

    // methods relating to changes to decks

    /** Returns the SessionList related to the specified deck. */
    public SessionList getSessionListForDeck(String deckName) {
        // assumes that the deck can be found? or throw exception?
        return this.deckHashMap.get(deckName);
    }

    /** Returns true if there are no decks in the DeckStats object. */
    public boolean hasNoDecks() {
        return this.deckHashMap.isEmpty();
    }

    /** Adds the given Session to the SessionList corresponding to the given deck. */
    public void addSessionToDeckSessionList(Session session, String deckName) {
        // if deck not found in hashmap, throw deck not found exception?
        SessionList sessionList = this.getSessionListForDeck(deckName);
        sessionList.addSession(session);
    }

    /**
     * Creates a new SessionList corresponding to a new deck with the specified name.
     * Assumes that no issues with the creating operation exist, i.e. have already been dealt with.
     * @param deckName The name of the deck to be added.
     */
    public void createNewDeck(String deckName) {
        this.deckHashMap.put(deckName, new SessionList());
    }

    /**
     * Renames a deck.
     * Assumes that no issues with the renaming operation exist, i.e. have already been dealt with.
     * @param oldDeckName The original name of the deck.
     * @param newDeckName The new name of the deck.
     */
    public void renameDeck(String oldDeckName, String newDeckName) {
        SessionList sessionList = this.deckHashMap.remove(oldDeckName);
        this.deckHashMap.put(newDeckName, sessionList);
    }

    /**
     * Deletes a deck.
     * @param deckName The name of the deck to be deleted.
     */
    public void deleteDeck(String deckName) {
        this.deckHashMap.remove(deckName);
    }

    @Override
    public JsonValue toJson() {
        JsonObject obj = new JsonObject();
        JsonArray names = new JsonArray();
        JsonArray sessions = new JsonArray();
        for (String k : deckHashMap.keySet()) {
            try {
                names.add(k);
                sessions.add(deckHashMap.get(k).toJson().getObject());
            } catch (JsonWrongValueException e) {
                System.out.println("SESSIONS MUST BE OBJECT");
            }
        }
        obj.put(Schema.STATS_DECK_STRINGS, names);
        obj.put(Schema.STATS_DECK_SESSIONS, sessions);
        return new JsonValue(obj);
    }
}
