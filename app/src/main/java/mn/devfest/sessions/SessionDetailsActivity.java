package mn.devfest.sessions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import mn.devfest.base.SinglePaneActivity;

/**
 * Activity that displays details for a particular session
 *
 * @author bherbst
 */
public class SessionDetailsActivity extends SinglePaneActivity {
    public static final String EXTRA_SESSION_ID = "sessionId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeIconMode(HOME_MODE_UP);
    }

    @Override
    protected Fragment onCreatePane() {
        return new SessionDetailsFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // For now, just finish the activity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
