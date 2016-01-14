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
 *
 * Created by chris.black on 12/5/15.
 */
public class DevFestDataSource {

    private Conference mConference;
    private UserScheduleRepository mScheduleRepository;

    public DevFestDataSource(Context context) {
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
        //TODO inject this
        mScheduleRepository = new UserScheduleRepository();
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

    public ArrayList<Speaker> getUserSchedule() {
        //TODO implement
        return new ArrayList<>();
    }

    /**
     * Listener for updates from the data source
     */
    public interface DataSourceListener {
        ArrayList<Session> onSessionsUpdate();
        ArrayList<Speaker> onSpeakersUpdate();
        ArrayList<Session> onUserScheduleUpdate();
    }
}
