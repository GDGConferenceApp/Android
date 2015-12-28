package mn.devfest.api.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chris.black on 12/5/15.
 */
public class Conference {
    String name;
    String id;
    double version;
    Date publishDate;
    ArrayList<Session> sessions;

    public String toString() {
        return name + " " + id + " " + version + " " + publishDate;
    }
}
