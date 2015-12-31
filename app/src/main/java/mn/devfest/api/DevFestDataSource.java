package mn.devfest.api;

import android.content.Context;
import android.util.JsonReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import mn.devfest.R;
import mn.devfest.api.model.Conference;
import mn.devfest.api.model.Session;
import mn.devfest.api.model.Speaker;

/**
 * Created by chris.black on 12/5/15.
 */
public class DevFestDataSource {

    private Conference conference;

    public DevFestDataSource(Context context) {
        //Reading source from local file
        InputStream inputStream = context.getResources().openRawResource(R.raw.firebase);
        String jsonString = readJsonFile(inputStream);

        conference = new Conference();//gson.fromJson(jsonString, Conference.class);
        JsonParser p = new JsonParser();
        JsonObject jsonobject = p.parse(jsonString).getAsJsonObject();
        conference.parseSessions(jsonobject.getAsJsonObject("schedule"));
        conference.parseSpeakers(jsonobject.getAsJsonObject("speakers"));
        conference.version = jsonobject.get("versionNum").getAsDouble();
        System.out.println(conference.toString());
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
        return conference.schedule;
    }

    public ArrayList<Speaker> getSpeakers() {
        return conference.speakers;
    }

    public interface DataSourceCallback {

        ArrayList<Session> getSessions();
        ArrayList<Speaker> getSpeakers();

    }
}
