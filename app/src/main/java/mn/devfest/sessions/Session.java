package mn.devfest.sessions;

/**
 * Model object representing sessions at the event
 *
 * @author Patrick Fuentes <pfuentes@nerdery.com>
 */
public class Session {
    private int id;
    private String mTitle;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
