//@@author nattanyz
package dream.fcard.logic.stats;

/**
 * Represents the user's statistics. Contains a SessionList containing all the user's login sessions.
 */
public class UserStats extends Stats {

    /** List of Sessions the user has engaged in. */
    private SessionList sessionList;

    /** The current Session the user is engaging in. */
    private Session currentSession;

    /** Constructs a new instance of UserStats with no stored data. */
    public UserStats() {
        this.sessionList = new SessionList();
        this.currentSession = null;
        logger.info("New UserStats object created.");
    }

    /** Sets the sessionList of the current Stats object to the given newSessionList. */
    public void setSessionList(SessionList newSessionList) {
        this.sessionList = newSessionList;
    }

    /** Gets the list of sessions. */
    public SessionList getSessionList() {
        return this.sessionList;
    }

    @Override
    public void startCurrentSession() {
        // replace with assert?
        if (this.currentSession != null) {
            endCurrentSession(); // should not occur, but should terminate just in case
            logger.info("Existing login session detected. Terminating it first...");
        }

        // debug (change to Logger when implemented)
        logger.info("Starting a login session...");

        this.currentSession = new Session(); // currentSession should be null
    }

    @Override
    public void endCurrentSession() {
        // assert current session is not null?
        if (this.currentSession == null) {
            logger.info("Current login session not found!");
            return;
        }

        try {
            this.currentSession.endSession();
            this.sessionList.addSession(currentSession);

            // reset currentSession to null since this is terminated
            this.currentSession = null;

            // debug (change to Logger when implemented)
            logger.info("Ending the current login session...");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Current login session not found?");
        }
    }

    @Override
    public Session getCurrentSession() {
        return this.currentSession;
    }
}
