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
 *
 * Created by chris.black on 12/5/15.
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

        mApi.getConferenceInfo(this);
    }

    @NonNull
    public List<Session> getSessions() {
        if (mConference == null) {
            return new ArrayList<>();
        }
        if (mConference.schedule == null) {
            return new ArrayList<>();
        }
        return mConference.schedule;
    }

    @NonNull
    public List<Speaker> getSpeakers() {
        if (mConference == null) {
            return new ArrayList<>();
        }
        if (mConference.speakers == null) {
            return new ArrayList<>();
        }
        return mConference.speakers;
    }

    @NonNull
    public List<Session> getUserSchedule() {
        //Remove sessions from the list that don't have an ID stored in the list of schedule IDs
        List<Session> sessions = getSessions();
        
        if (sessions.size() == 0) {
            return sessions;
        }

        // We use a loop that goes backwards so we can remove items as we iterate over the list without
        // running into a concurrent modification issue or altering the indices of items
        for (int i = sessions.size() - 1; i >= 0; i--) {
            Session session = sessions.get(i);
            if (!mScheduleRepository.getScheduleIds().contains(session.getId())) {
                sessions.remove(i);
            }
        }
        return sessions;
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
        List<Session> onSessionsUpdate(List<Session> sessions);
        List<Speaker> onSpeakersUpdate(List<Speaker> speakers);
        List<Session> onUserScheduleUpdate(List<Session> userSchedule);
    }
}
