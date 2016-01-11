package mn.devfest.schedule;

import java.util.ArrayList;

/**
 * Stores and provides the sessions that the user has added to their schedule.
 * This class must be notified any time a session is added or removed from a user's schedule.
 *
 * @author pfuentes
 */
public class ScheduleRepository {

    /**
     * Adds the session with the given ID to the user's schedule
     * @param sessionId ID of the session to be added
     */
    public void addSession(String sessionId) {
        //TODO implement
    }

    /**
     * Removes the session with the given ID from the user's schedule
     * @param sessionId ID of the session to be removed
     */
    public void removeSession(String sessionId) {
        //TODO implement
    }

    /**
     * Shares the list of IDs for all of the sessions in the user's schedule
     * @return List of IDs of the sessions in the user's schedule
     */
    public ArrayList<String> getScheduleIds() {
        //TODO implement
        return new ArrayList<>(); //TODO update return statement
    }

    /**
     * Checks if a given session is in the user's schedule
     * @param sessionId ID of the session to check for inclusion in the list
     * @return true if the session is in the user's schedule; otherwise false
     */
    public boolean isInSchedule(String sessionId) {
        //TODO implement
        return true; //TODO update return statement
    }
}
