package mn.devfest.api.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
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
public class ConferenceTypeAdapter implements JsonDeserializer<Conference>, JsonSerializer<Conference> {
    private static final String VERSION_NUMBER_KEY = "versionNum";
    private static final String SCHEDULE_KEY = "schedule";
    private static final String SPEAKER_KEY = "speakers";

    @Override
    public Conference deserialize(JsonElement jsonElement, Type typeOF,
                                  JsonDeserializationContext context) throws JsonParseException {

        // TODO we can probably do this better
        Conference conference = new Conference();
        JsonObject conferenceObject = jsonElement.getAsJsonObject();
        conference.setSchedule(parseSessions(context, conferenceObject.getAsJsonObject(SCHEDULE_KEY)));
        conference.setSpeakers(parseSpeakers(context, conferenceObject.getAsJsonObject(SPEAKER_KEY)));
        conference.setVersion(conferenceObject.get(VERSION_NUMBER_KEY).getAsDouble());

        return conference;
    }

    public Map<String, Session> parseSessions(JsonDeserializationContext context, JsonObject object) {
        Set<Map.Entry<String,JsonElement>> entrySet = object.entrySet();
        Map<String, Session> sessions = new HashMap<>(entrySet.size());

        for(Map.Entry<String,JsonElement> entry: entrySet) {
            try {
                JsonObject obj = object.getAsJsonObject(entry.getKey());
                Session session = context.deserialize(obj, Session.class);
                session.setId(entry.getKey());
                sessions.put(session.getId(), session);
            } catch (ClassCastException e) {
                // Do nothing
            }
        }

        return sessions;
    }

    public Map<String, Speaker> parseSpeakers(JsonDeserializationContext context, JsonObject object) {
        Set<Map.Entry<String,JsonElement>> entrySet = object.entrySet();
        Map<String, Speaker> speakers = new HashMap<>(entrySet.size());

        for(Map.Entry<String,JsonElement> entry: entrySet) {
            try {
                JsonObject obj = object.getAsJsonObject(entry.getKey());
                Speaker speaker = context.deserialize(obj, Speaker.class);
                speaker.id = entry.getKey();
                speakers.put(speaker.id, speaker);
            } catch (ClassCastException e) {
                // Do nothing
            }
        }

        return speakers;
    }

    @Override
    public JsonElement serialize(Conference src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(VERSION_NUMBER_KEY, src.getVersion());

        final JsonElement jsonSchedule = context.serialize(src.getSchedule());
        jsonObject.add(SCHEDULE_KEY, jsonSchedule);

        final JsonElement jsonSpeakers = context.serialize(src.getSpeakers());
        jsonObject.add(SPEAKER_KEY, jsonSpeakers);

        return jsonObject;
    }
}