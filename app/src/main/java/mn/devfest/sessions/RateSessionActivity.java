package mn.devfest.sessions;

import android.support.v4.app.Fragment;

import mn.devfest.base.SinglePaneActivity;

/**
 * Activity that allows the user to rate a session
 *
 * @author bherbst
 */
public class RateSessionActivity extends SinglePaneActivity {

    @Override
    protected Fragment onCreatePane() {
        return new RateSessionFragment();
    }

}
