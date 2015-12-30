package mn.devfest.api.model;

/**
 * Created by chris.black on 12/5/15.
 */
public class Speaker {
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
}
