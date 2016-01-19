package mn.devfest.api;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import mn.devfest.api.model.Conference;
import mn.devfest.api.model.Session;
import mn.devfest.api.model.Speaker;
import mn.devfest.schedule.UserScheduleRepository;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * This is the source of session, schedule, and speaker information. This acts as a general
 * contractor that can coordinate between various subcontractor classes including but not limited to
 * local and remote data sources.
 * <p>
 * Created by chris.black on 12/5/15.
 *
 * @author bherbst
 * @author pfuentes
 */
public class DevFestDataSource implements Callback<Conference> {

    private final DevFestApi mApi;
    private final UserScheduleRepository mScheduleRepository;

    private Conference mConference;
    //TODO move to an array of listeners?
    private DataSourceListener mDataSourceListener;

    public DevFestDataSource(DevFestApi api, UserScheduleRepository scheduleRepository) {
        this.mApi = api;
        this.mScheduleRepository = scheduleRepository;

        // TODO this is a terrible place to fetch the API data.
        // It isn't clear what thread this is called on, typically shouldn't happen in a constructor,
        // and doesn't allow for easy refreshing if data if that is necessary in the future.
        mApi.getConferenceInfo(this);
    }

    @NonNull
    public List<Session> getSessions() {
        if (mConference == null) {
            return new ArrayList<>();
        }
        if (mConference.getSchedule() == null) {
            return new ArrayList<>();
        }

        return mConference.getSchedule();
    }

    @NonNull
    public List<Speaker> getSpeakers() {
        if (mConference == null) {
            return new ArrayList<>();
        }
        if (mConference.getSpeakers() == null) {
            return new ArrayList<>();
        }

        return mConference.getSpeakers();
    }

    @NonNull
    public List<Session> getUserSchedule() {
        // Find sessions with an ID matching the user's saved session IDs
        List<Session> sessions = getSessions();
        List<Session> userSessions = new ArrayList<>();

        if (sessions.size() == 0) {
            return sessions;
        }

        // We use a loop that goes backwards so we can remove items as we iterate over the list without
        // running into a concurrent modification issue or altering the indices of items
        for (int i = sessions.size() - 1; i >= 0; i--) {
            Session session = sessions.get(i);
            if (mScheduleRepository.getScheduleIds().contains(session.getId())) {
                userSessions.add(session);
            }
        }
        return userSessions;
    }

    /**
     * Adds the session with the given ID to the user's schedule
     * TODO decide if we want this pass-through to maintain the general contractor paradigm
     * @param sessionId ID of the session to be added
     */
    public void addToUserSchedule(String sessionId) {
        mScheduleRepository.addSession(sessionId);
    }

    /**
     * Removes the session with the given ID from the user's schedule
     * TODO decide if we want this pass-through to maintain the general contractor paradigm
     * @param sessionId ID of the session to be removed
     */
    public void removeFromUserSchedule(String sessionId) {
        mScheduleRepository.removeSession(sessionId);
    }

    public void setDataSourceListener(DataSourceListener listener) {
        mDataSourceListener = listener;
    }

    private void onConferenceUpdated() {
        //Notify listener
        mDataSourceListener.onSessionsUpdate(getSessions());
        mDataSourceListener.onSpeakersUpdate(getSpeakers());
        mDataSourceListener.onUserScheduleUpdate(getUserSchedule());
    }

    @Override
    public void success(Conference conference, Response response) {
        mConference = conference;
        onConferenceUpdated();
    }

    @Override
    public void failure(RetrofitError error) {
        Timber.e(error, "Failed to retrieve conference info.");
    }

    /**
     * Listener for updates from the data source
     */
    public interface DataSourceListener {
        //These methods are for updating the listener
        void onSessionsUpdate(List<Session> sessions);

        void onSpeakersUpdate(List<Speaker> speakers);

        void onUserScheduleUpdate(List<Session> userSchedule);
    }
}
