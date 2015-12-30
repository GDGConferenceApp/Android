package mn.devfest.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by chris.black on 12/5/15.
 */
public class Session {
    public boolean all;
    public String description;
    public Date endTime;
    public String room;
    public ArrayList<String> speakers;
    public Date startTime;
    public String title;
    public String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
