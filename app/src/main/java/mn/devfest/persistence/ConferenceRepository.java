package mn.devfest.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import mn.devfest.api.model.Conference;

/**
 * Stores and provides information about the conference. The app is shipped with a local copy of
 * the latest conference information. As we get newer information from the API, this is updated.
 *
 * @author pfuentes
 */
public class ConferenceRepository {

    Context mContext;
    SharedPreferences mSharedPreferences;

    public ConferenceRepository(@NonNull Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /**
     * Removes any existing persisted conference data and replaces it with the passed value
     *
     * @param conference most up-to-date conference information available
     */
    public void setConference(@Nullable Conference conference) {
        //TODO implement
    }

    /**
     * Provides the most recently persisted conference data
     *
     * @return persisted conference data
     */
    @NonNull
    public Conference getConference() {
        //TODO implement and remove dummy return
        return new Conference();
    }

}
