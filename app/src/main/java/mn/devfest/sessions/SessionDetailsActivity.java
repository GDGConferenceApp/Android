package mn.devfest.sessions;

import android.support.v4.app.Fragment;

import mn.devfest.base.SinglePaneActivity;

/**
 * Activity that displays details for a particular session
 *
 * @author bherbst
 */
public class SessionDetailsActivity extends SinglePaneActivity {

    @Override
    protected Fragment onCreatePane() {
        return new SessionDetailsFragment();
    }

}
