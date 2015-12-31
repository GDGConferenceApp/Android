package mn.devfest.api.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by chris.black on 12/5/15.
 */
public class Conference {
    public double version = 1;
    public ArrayList<Session> schedule = new ArrayList<>();
    public ArrayList<Speaker> speakers = new ArrayList<>();

    public String toString() {
        return "Version: " + version + " has " + schedule.size() + " sessions and " + speakers.size() + " speakers";
    }

    public void parseSessions(JsonObject object) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm")
                .create();
        Set<Map.Entry<String,JsonElement>> entrySet = object.entrySet();
        for(Map.Entry<String,JsonElement> entry:entrySet){
            Log.d("SCHEDULE", entry.getKey());
            try {
                JsonObject obj = object.getAsJsonObject(entry.getKey());
                Session session = gson.fromJson(obj, Session.class);
                session.setId(entry.getKey());
                schedule.add(session);
            } catch (ClassCastException e) {
                // Do nothing
            }
        }
    }

    public void parseSpeakers(JsonObject object) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm")
                .create();
        Set<Map.Entry<String,JsonElement>> entrySet = object.entrySet();
        for(Map.Entry<String,JsonElement> entry:entrySet){
            Log.d("SPEAKER", entry.getKey());
            try {
                JsonObject obj = object.getAsJsonObject(entry.getKey());
                Speaker speaker = gson.fromJson(obj, Speaker.class);
                speaker.id = entry.getKey();
                speakers.add(speaker);
            } catch (ClassCastException e) {
                // Do nothing
            }
        }
    }
}
