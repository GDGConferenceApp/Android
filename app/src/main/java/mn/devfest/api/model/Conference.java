package mn.devfest.api.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents all of the sessions and speakers for a given conference
 * Created by chris.black on 12/5/15.
 */
public class Conference {
    private double version = 1;
    private Map<String, Session> schedule = new HashMap<>(0);
    private Map<String, Speaker> speakers = new HashMap<>(0);

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public Map<String, Session> getSchedule() {
        return Collections.unmodifiableMap(schedule);
    }

    public void setSchedule(Map<String, Session> schedule) {
        this.schedule = new HashMap<>(schedule);
    }

    public Map<String, Speaker> getSpeakers() {
        return Collections.unmodifiableMap(speakers);
    }

    public void setSpeakers(Map<String, Speaker> speakers) {
        this.speakers = new HashMap<>(speakers);
    }

    public String toString() {
        return "Version: " + version + " has " + schedule.size() + " sessions and " + speakers.size() + " speakers";
    }
}
