package mn.devfest.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris.black on 12/5/15.
 */
public class Conference {
    public double version = 1;
    public List<Session> schedule = new ArrayList<>();
    public List<Speaker> speakers = new ArrayList<>();

    public String toString() {
        return "Version: " + version + " has " + schedule.size() + " sessions and " + speakers.size() + " speakers";
    }
}
