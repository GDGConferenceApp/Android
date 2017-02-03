package com.devfestmn.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Stores and provides the sessions that the user has added to their schedule.
 * This class must be notified any time a session is added or removed from a user's schedule.
 *
 * @author pfuentes
 */
public class UserScheduleRepository {
    private static final String SCHEDULE_ID_STRING_SET_TAG = "SCHEDULE_IDS";

    Context mContext;
    SharedPreferences mSharedPreferences;

    public UserScheduleRepository(@NonNull Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /**
     * Adds the session with the given ID to the user's schedule
     * @param sessionId ID of the session to be added
     */
    public void addSession(String sessionId) {
        ArrayList<String> scheduleIds = getScheduleIds();
        scheduleIds.add(sessionId);
        setScheduleIdStringSet(scheduleIds);
    }

    /**
     * Removes the session with the given ID from the user's schedule
     * @param sessionId ID of the session to be removed
     */
    public void removeSession(String sessionId) {
        ArrayList<String> scheduleIds = getScheduleIds();
        scheduleIds.remove(sessionId);
        setScheduleIdStringSet(scheduleIds);
    }

    /**
     * Shares the list of IDs for all of the sessions in the user's schedule
     * @return List of IDs of the sessions in the user's schedule
     */
    @NonNull
    public ArrayList<String> getScheduleIds() {
        Set<String> scheduleIdSet = mSharedPreferences.getStringSet(SCHEDULE_ID_STRING_SET_TAG, new HashSet<>());
        return new ArrayList<>(scheduleIdSet);
    }

    /**
     * Checks if a given session is in the user's schedule
     * @param sessionId ID of the session to check for inclusion in the list
     * @return true if the session is in the user's schedule; otherwise false
     */
    public boolean isInSchedule(String sessionId) {
        ArrayList<String> scheduleIds = getScheduleIds();
        return scheduleIds.contains(sessionId);
    }


    public void setScheduleIdStringSet(@NonNull ArrayList<String> scheduleIds) {
        Set<String> stringSet = new HashSet<>(scheduleIds);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putStringSet(SCHEDULE_ID_STRING_SET_TAG, stringSet);
        editor.commit();
    }
}
