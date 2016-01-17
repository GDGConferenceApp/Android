package mn.devfest.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents all of the sessions and speakers for a given conference
 * Created by chris.black on 12/5/15.
 */
public class Conference {
    private double version = 1;
    private List<Session> schedule = new ArrayList<>();
    private List<Speaker> speakers = new ArrayList<>();

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public List<Session> getSchedule() {
        return Collections.unmodifiableList(schedule);
    }

    public void setSchedule(List<Session> schedule) {
        this.schedule = new ArrayList<>(schedule);
    }

    public List<Speaker> getSpeakers() {
        return Collections.unmodifiableList(speakers);
    }

    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = new ArrayList<>(speakers);
    }

    public String toString() {
        return "Version: " + version + " has " + schedule.size() + " sessions and " + speakers.size() + " speakers";
    }
}
