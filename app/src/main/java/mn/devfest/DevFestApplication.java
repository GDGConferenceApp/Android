package mn.devfest;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * DevFest application class
 * <p>
 * Performs application-wide configuration
 *
 * @author bherbst
 */
public class DevFestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    /**
     * Initialize app-wide configuration
     * <p>
     * The default implementation sets up the app for release. Do not call through to super()
     * if you do not want the release configuration.
     */
    protected void init() {
        Timber.plant(new ReleaseTree());
        Fabric.with(this, new Crashlytics());
        JodaTimeAndroid.init(this);
    }

    /**
     * Get a WvwApplication from a Context
     *
     * @param context The Context in which to get the WvwApplication
     * @return The WvwApplication associated with the given Context
     */
    public static DevFestApplication get(Context context) {
        return (DevFestApplication) context.getApplicationContext();
    }
}
