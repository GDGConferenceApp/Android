package mn.devfest.api;

import java.util.ArrayList;

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
 */
public class DevFestDataSource implements Callback<Conference> {

    private final DevFestApi mApi;
    private final UserScheduleRepository mScheduleRepository;

    private Conference mConference;
    private DataSourceListener mDataSourceListener;

    public DevFestDataSource(DevFestApi api, UserScheduleRepository scheduleRepository) {
        this.mApi = api;
        this.mScheduleRepository = scheduleRepository;

        mApi.getConferenceInfo(this);
    }

    public void setListener(DataSourceListener listener) {
        mDataSourceListener = listener;
    }

    public ArrayList<Session> getSessions() {
        return mConference.schedule;
    }

    public ArrayList<Speaker> getSpeakers() {
        return mConference.speakers;
    }

    public ArrayList<Session> getUserSchedule() {
        //Remove sessions from the list that don't have an ID stored in the list of schedule IDs
        ArrayList<Session> sessions = getSessions();

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
        ArrayList<Session> onSessionsUpdate(ArrayList<Session> sessions);
        ArrayList<Speaker> onSpeakersUpdate(ArrayList<Speaker> speakers);
        ArrayList<Session> onUserScheduleUpdate(ArrayList<Session> userSchedule);
        //TODO delete these methods when they're not used any more
        ArrayList<Session> getSessions();
        ArrayList<Speaker> getSpeakers();
        ArrayList<Session> getSchedule();

    }
}
