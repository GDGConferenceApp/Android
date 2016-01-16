package mn.devfest.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents all of the sessions and speakers for a given conference
 * Created by chris.black on 12/5/15.
 */
public class Conference {
    private double version = 1;

    private List<Session> schedule = new ArrayList<>();

    private List<Speaker> speakers = new ArrayList<>();
    public String toString() {
        return "Version: " + version + " has " + schedule.size() + " sessions and " + speakers.size() + " speakers";
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public List<Session> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<Session> schedule) {
        this.schedule = schedule;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }
}
