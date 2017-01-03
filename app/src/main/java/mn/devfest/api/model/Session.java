package mn.devfest.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Represents a session at the conference (e.g. – a talk, workshop, etc)
 *
 * @author cblack
 * @author bherbst
 * @author pfuentes
 */
public class Session implements Parcelable {
    private boolean all;
    private String description;
    private String endTime;
    private String room;
    private ArrayList<String> speakers;
    private String startTime;
    private String title;

    private String category;

    private String id;

    public Session() {

    }

    protected Session(Parcel in) {
        all = in.readByte() != 0;
        description = in.readString();
        endTime = in.readString();
        room = in.readString();
        //TODO speakers = in.createStringArrayList();
        startTime = in.readString();
        title = in.readString();
        category = in.readString();
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (all ? 1 : 0));
        dest.writeString(description);
        dest.writeString(endTime);
        dest.writeString(room);
        //TODO dest.writeStringList(speakers);
        dest.writeString(startTime);
        dest.writeString(title);
        dest.writeString(category);
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public DateTime getEndTime() {
        return new DateTime(endTime);
    }

    public String getRoom() {
        return room;
    }

    public ArrayList<String> getSpeakers() {
        return speakers;
    }

    public String getCategory() {
        return category;
    }

    public DateTime getStartTime() {
        return new DateTime(startTime);
    }
}
