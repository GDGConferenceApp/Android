package mn.devfest.api;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import mn.devfest.R;
import mn.devfest.api.model.Conference;
import mn.devfest.api.model.Session;
import mn.devfest.api.model.Speaker;
import mn.devfest.schedule.UserScheduleRepository;

/**
 * This is the source of session, schedule, and speaker information. This acts as a general
 * contractor that can coordinate between various subcontractor classes including but not limited to
 * local and remote data sources.
 * TODO make singleton
 *
 * Created by chris.black on 12/5/15.
 */
public class DevFestDataSource {

    private Conference mConference;
    private UserScheduleRepository mScheduleRepository;
    private DataSourceListener mDataSourceListener;

    public DevFestDataSource(Context context, DataSourceListener listener) {
        mDataSourceListener = listener;
        //TODO inject this
        mScheduleRepository = new UserScheduleRepository(context);

        //Reading source from local file
        InputStream inputStream = context.getResources().openRawResource(R.raw.firebase);
        String jsonString = readJsonFile(inputStream);

        mConference = new Conference();//gson.fromJson(jsonString, Conference.class);
        JsonParser p = new JsonParser();
        JsonObject jsonobject = p.parse(jsonString).getAsJsonObject();
        mConference.parseSessions(jsonobject.getAsJsonObject("schedule"));
        mConference.parseSpeakers(jsonobject.getAsJsonObject("speakers"));
        mConference.version = jsonobject.get("versionNum").getAsDouble();
        System.out.println(mConference.toString());

        onConferenceUpdated();
    }

    private String readJsonFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte bufferByte[] = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(bufferByte)) != -1) {
                outputStream.write(bufferByte, 0, length);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
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
