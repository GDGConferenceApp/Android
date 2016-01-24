package mn.devfest.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import mn.devfest.api.model.Conference;

/**
 * Stores and provides information about the conference. The app is shipped with a local copy of
 * the latest conference information. As we get newer information from the API, this is updated.
 * TODO evaluate if we want to use another persistence method, rather than SharedPreferences
 *
 * @author pfuentes
 */
public class ConferenceRepository {
    private static final String CONFERENCE_KEY = "CONFERENCE";

    Context mContext;
    SharedPreferences mSharedPreferences;
    Gson mGson;

    public ConferenceRepository(@NonNull Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mGson = new Gson();
    }

    /**
     * Removes any existing persisted conference data and replaces it with the passed value
     * If you try to pass null as an argument, the data is not updated.
     * To delete all existing conference data, pass an empty {@link Conference} object
     *
     * @param conference most up-to-date conference information available
     */
    public void setConference(@Nullable Conference conference) {
        if (conference == null) {
            return;
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String jsonString = mGson.toJson(conference);
        editor.putString(CONFERENCE_KEY, jsonString);
        editor.apply();
    }

    /**
     * Provides the most recently persisted conference data
     * If no conference data is persisted, an empty {@link Conference} object is returned
     *
     * @return persisted conference data
     */
    @NonNull
    public Conference getConference() {
        String jsonString = mSharedPreferences.getString(CONFERENCE_KEY, null);
        if (jsonString == null) {
            //TODO update this to return the locally stored conference data & update the documentation
            return new Conference();
        }
        return mGson.fromJson(jsonString, Conference.class);
    }

}
