package mn.devfest.api.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mn.devfest.api.model.Conference;
import mn.devfest.api.model.Session;
import mn.devfest.api.model.Speaker;

/**
 * Adapts the JSON for a conference into something useable in the app
 *
 * @author bherbst
 */
public class ConferenceTypeAdapter implements JsonDeserializer<Conference> {
    @Override
    public Conference deserialize(JsonElement jsonElement, Type typeOF,
                                  JsonDeserializationContext context) throws JsonParseException {

        // TODO we can probably do this better
        Conference conference = new Conference();
        JsonObject conferenceObject = jsonElement.getAsJsonObject();
        conference.setSchedule(parseSessions(context, conferenceObject.getAsJsonObject("schedule")));
        conference.setSpeakers(parseSpeakers(context, conferenceObject.getAsJsonObject("speakers")));
        conference.setVersion(conferenceObject.get("versionNum").getAsDouble());

        return conference;
    }

    public List<Session> parseSessions(JsonDeserializationContext context, JsonObject object) {
        Set<Map.Entry<String,JsonElement>> entrySet = object.entrySet();
        List<Session> sessions = new ArrayList<>(entrySet.size());

        for(Map.Entry<String,JsonElement> entry: entrySet) {
            try {
                JsonObject obj = object.getAsJsonObject(entry.getKey());
                Session session = context.deserialize(obj, Session.class);
                session.setId(entry.getKey());
                sessions.add(session);
            } catch (ClassCastException e) {
                // Do nothing
            }
        }

        return sessions;
    }

    public List<Speaker> parseSpeakers(JsonDeserializationContext context, JsonObject object) {
//        Gson gson = new GsonBuilder()
//                .setDateFormat("yyyy-MM-dd'T'HH:mm")
//                .create();
        Set<Map.Entry<String,JsonElement>> entrySet = object.entrySet();
        List<Speaker> speakers = new ArrayList<>(entrySet.size());

        for(Map.Entry<String,JsonElement> entry: entrySet) {
            try {
                JsonObject obj = object.getAsJsonObject(entry.getKey());
                Speaker speaker = context.deserialize(obj, Speaker.class);
                speaker.id = entry.getKey();
                speakers.add(speaker);
            } catch (ClassCastException e) {
                // Do nothing
            }
        }

        return speakers;
    }
}