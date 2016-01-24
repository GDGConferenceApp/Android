package mn.devfest.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import mn.devfest.api.model.Conference;
import mn.devfest.api.model.Session;
import mn.devfest.api.model.Speaker;
import mn.devfest.persistence.ConferenceRepository;
import mn.devfest.persistence.UserScheduleRepository;
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
    private final ConferenceRepository mConferenceRepository;

    private Conference mConference;
    //TODO move to an array of listeners?
    private DataSourceListener mDataSourceListener;

    public DevFestDataSource(DevFestApi api, UserScheduleRepository scheduleRepository, ConferenceRepository conferenceRepository) {
        this.mApi = api;
        this.mScheduleRepository = scheduleRepository;
        this.mConferenceRepository = conferenceRepository;
        updateConferenceInfo();
    }

    /**
     * Checks the API for fresh info.
     * If fresh info is available, it's persisted locally for future reference
     * If fresh info isn't available, we fall back to the local store
     */
    public void updateConferenceInfo() {
        //Check the API for fresh info
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

        return new ArrayList<>(mConference.getSchedule().values());
    }

    @Nullable
    public Session getSessionById(String id) {
        if (mConference == null) {
            return null;
        }

        return mConference.getSchedule().get(id);
    }

    @NonNull
    public List<Speaker> getSpeakers() {
        if (mConference == null) {
            return new ArrayList<>();
        }
        if (mConference.getSpeakers() == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(mConference.getSpeakers().values());
    }

    @Nullable
    public Speaker getSpeakerById(String id) {
        if (mConference == null) {
            return null;
        }

        return mConference.getSpeakers().get(id);
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
     *
     * @param sessionId ID of the session to be added
     */
    public void addToUserSchedule(String sessionId) {
        mScheduleRepository.addSession(sessionId);
    }

    /**
     * Removes the session with the given ID from the user's schedule
     *
     * @param sessionId ID of the session to be removed
     */
    public void removeFromUserSchedule(String sessionId) {
        mScheduleRepository.removeSession(sessionId);
    }

    /**
     * Checks if a given session is in the user's schedule
     *
     * @param sessionId ID of the session to check for inclusion in the list
     * @return true if the session is in the user's schedule; otherwise false
     */
    public boolean isInUserSchedule(String sessionId) {
        return mScheduleRepository.isInSchedule(sessionId);
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
        mConferenceRepository.setConference(conference);
        onConferenceUpdated();

        //TODO remove this after debugging is complete
        mConferenceRepository.getConference();
    }

    @Override
    public void failure(RetrofitError error) {
        Timber.e(error, "Failed to retrieve conference info.");
        mConference = mConferenceRepository.getConference();
        onConferenceUpdated();
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
