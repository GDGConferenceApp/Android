package com.devfestmn.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import java.util.ArrayList;

import timber.log.Timber;

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
    private String startTime;
    private DateTime startDateTime;
    private String endTime;
    private DateTime endDateTime;
    private String room;
    private ArrayList<String> speakers;
    private String title;
    private String track;

    private String id;

    public Session() {
        // Default constructor required for calls to DataSnapshot.getValue(Session.class)
    }

    protected Session(Parcel in) {
        all = in.readByte() != 0;
        description = in.readString();
        endTime = in.readString();
        room = in.readString();
        speakers = in.createStringArrayList();
        startTime = in.readString();
        title = in.readString();
        track = in.readString();
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (all ? 1 : 0));
        dest.writeString(description);
        dest.writeString(endTime);
        dest.writeString(room);
        dest.writeStringList(speakers);
        dest.writeString(startTime);
        dest.writeString(title);
        dest.writeString(track);
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

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStartDateTime(DateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(DateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    @Nullable
    public DateTime getStartDateTime() {
        if (this.startDateTime == null) {
            try {
                //TODO handle failed parsing better
                this.startDateTime = DateTime.parse(startTime);
            } catch (Exception e) {
                Timber.e(e, "Failed to parse startTime into a DateTime. startTime = %s", startTime);
            }
        }
        return startDateTime;
    }

    @Nullable
    public DateTime getEndDateTime() {
        if (this.endDateTime == null) {
            try {
                //TODO handle failed parsing better
                this.endDateTime = DateTime.parse(endTime);
            } catch (Exception e) {
                Timber.e(e, "Failed to parse endTime into a DateTime. endTime = %s", endTime);
            }
        }
        return endDateTime;
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

    /**
     * @return the string representing the timestamp of the starting time of the session
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @return the string representing the timestamp of the ending time of the session
     */
    public String getEndTime() {
        return endTime;
    }

    public String getRoom() {
        return room;
    }

    public ArrayList<String> getSpeakers() {
        return speakers;
    }

    public String getTrack() {
        return track;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        if (all != session.all) return false;
        if (getDescription() != null ? !getDescription().equals(session.getDescription()) : session.getDescription() != null)
            return false;
        if (getStartTime() != null ? !getStartTime().equals(session.getStartTime()) : session.getStartTime() != null)
            return false;
        if (getStartDateTime() != null ? !getStartDateTime().equals(session.getStartDateTime()) : session.getStartDateTime() != null)
            return false;
        if (getEndTime() != null ? !getEndTime().equals(session.getEndTime()) : session.getEndTime() != null)
            return false;
        if (getEndDateTime() != null ? !getEndDateTime().equals(session.getEndDateTime()) : session.getEndDateTime() != null)
            return false;
        if (getRoom() != null ? !getRoom().equals(session.getRoom()) : session.getRoom() != null)
            return false;
        if (getSpeakers() != null ? !getSpeakers().equals(session.getSpeakers()) : session.getSpeakers() != null)
            return false;
        if (getTitle() != null ? !getTitle().equals(session.getTitle()) : session.getTitle() != null)
            return false;
        if (getTrack() != null ? !getTrack().equals(session.getTrack()) : session.getTrack() != null)
            return false;
        return getId() != null ? getId().equals(session.getId()) : session.getId() == null;

    }

    @Override
    public int hashCode() {
        int result = (all ? 1 : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getStartTime() != null ? getStartTime().hashCode() : 0);
        result = 31 * result + (getStartDateTime() != null ? getStartDateTime().hashCode() : 0);
        result = 31 * result + (getEndTime() != null ? getEndTime().hashCode() : 0);
        result = 31 * result + (getEndDateTime() != null ? getEndDateTime().hashCode() : 0);
        result = 31 * result + (getRoom() != null ? getRoom().hashCode() : 0);
        result = 31 * result + (getSpeakers() != null ? getSpeakers().hashCode() : 0);
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getTrack() != null ? getTrack().hashCode() : 0);
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }
}
