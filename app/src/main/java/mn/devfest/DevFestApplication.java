package mn.devfest;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;

/**
 * DevFest application class
 *
 * Performs application-wide configuration
 *
 * @author bherbst
 */
public class DevFestApplication extends Application {

    private DevFestGraph mGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        init();
        buildComponent();
    }

    /**
     * Build the Dagger component
     */
    private void buildComponent() {
        mGraph = DevFestComponent.Initializer.init(this);
    }

    /**
     * Get the Dagger component
     */
    public DevFestGraph component() {
        return mGraph;
    }

    /**
     * Initialize app-wide configuration
     *
     * The default implementation sets up the app for release. Do not call through to super()
     * if you do not want the release configuration.
     */
    protected void init() {
        Timber.plant(new ReleaseTree());
    }

    /**
     * Get a WvwApplication from a Context
     * @param context The Context in which to get the WvwApplication
     * @return The WvwApplication associated with the given Context
     */
    public static DevFestApplication get(Context context) {
        return (DevFestApplication) context.getApplicationContext();
    }
}
