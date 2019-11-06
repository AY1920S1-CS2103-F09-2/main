//@@author nattanyz
package dream.fcard.util.stats;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;

import dream.fcard.logic.stats.Session;
import dream.fcard.logic.stats.SessionList;

/**
 * Utilities for easily manipulating and getting data from SessionList objects.
 */
public class SessionListUtil {

    /**
     * Retrieves a sublist of the given SessionList, containing only sessions which started AFTER
     * the given cutoff.
     * @param initialSessionList The SessionList to get the sublist from.
     * @param cutoff The LocalDateTime to be used as the cutoff time.
     * @return A sublist of the given SessionList, containing only sessions which started after the given cutoff.
     */
    public static SessionList getSublistAfterCutoff(SessionList initialSessionList, LocalDateTime cutoff) {
        SessionList sublist = new SessionList();
        ArrayList<Session> sessionArrayList = initialSessionList.getSessionArrayList();

        for (Session session : sessionArrayList) {
            if (session.getSessionStart().isAfter(cutoff)) {
                sublist.addSession(session);
            }
        }

        return sublist;
    }

    /**
     * Retrieves a sublist of the given SessionList, containing only sessions which started within
     * the previous week.
     * @param initialSessionList The SessionList to get the sublist from.
     * @return A sublist of the given SessionList, containing only sessions which started within the previous week.
     */
    public static SessionList getSublistForThisWeek(SessionList initialSessionList) {
        LocalDateTime cutoff = DateTimeUtil.getLastWeekCutoffDate(LocalDateTime.now());
        return getSublistAfterCutoff(initialSessionList, cutoff);
    }

    /**
     * Given the score of a test as a String, convert it to a String representing the percentage
     * of correct answers, rounded to 2 decimal places.
     * @param score The score to be converted, as a String.
     * @return The score as a double, representing the percentage of correct answers.
     */
    public static String getScoreAsPercentage(String score) {
        int finalScore = Integer.parseInt(score.split("/")[0]);
        int maxScore = Integer.parseInt(score.split("/")[1]);
        double scoreAsDouble = (finalScore / (double) maxScore) * 100;

        DecimalFormat decimalFormat = new DecimalFormat("#.##"); // rounds to 2 d.p.
        return decimalFormat.format(scoreAsDouble);
    }
}
