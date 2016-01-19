package mn.devfest.api.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chris.black on 12/5/15.
 */
public class Speaker implements Parcelable {
    public String name;
    public String bio;
    public String company;
    public String image;
    public String twitter;
    public String website;
    public String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public String getCompany() {
        return company;
    }

    public String getImage() {
        return image;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getWebsite() {
        return website;
    }

    public Speaker() {
        // TODO shouldn't be needed once all screens are getting real data
    }

    protected Speaker(Parcel in) {
        name = in.readString();
        bio = in.readString();
        company = in.readString();
        image = in.readString();
        twitter = in.readString();
        website = in.readString();
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(bio);
        dest.writeString(company);
        dest.writeString(image);
        dest.writeString(twitter);
        dest.writeString(website);
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Speaker> CREATOR = new Creator<Speaker>() {
        @Override
        public Speaker createFromParcel(Parcel in) {
            return new Speaker(in);
        }

        @Override
        public Speaker[] newArray(int size) {
            return new Speaker[size];
        }
    };
}
