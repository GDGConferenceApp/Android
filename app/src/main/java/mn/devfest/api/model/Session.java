package mn.devfest.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Represents a session at the conference (e.g. – a talk, workshop, etc)
 * Created by chris.black on 12/5/15.
 */
public class Session implements Parcelable {
    private boolean all;
    private String description;
    private DateTime endTime;
    private String room;
    private ArrayList<String> speakers;
    private DateTime startTime;
    private String title;

    private ArrayList<String> tags;

    private String id;

    protected Session(Parcel in) {
        all = in.readByte() != 0;
        description = in.readString();
        endTime = new DateTime(in.readLong());
        room = in.readString();
        speakers = in.createStringArrayList();
        startTime = new DateTime(in.readLong());
        title = in.readString();
        tags = in.createStringArrayList();
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (all ? 1 : 0));
        dest.writeString(description);
        dest.writeLong(endTime.getMillis());
        dest.writeString(room);
        dest.writeStringList(speakers);
        dest.writeLong(startTime.getMillis());
        dest.writeString(title);
        dest.writeStringList(tags);
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
        return endTime;
    }

    public String getRoom() {
        return room;
    }

    public ArrayList<String> getSpeakers() {
        return speakers;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public DateTime getStartTime() {
        return startTime;
    }
}
