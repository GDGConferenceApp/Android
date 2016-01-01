package mn.devfest.sessions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import mn.devfest.api.model.Session;
import mn.devfest.base.SinglePaneActivity;

/**
 * Activity that displays details for a particular session
 *
 * @author bherbst
 * @author pfuentes
 */
public class SessionDetailsActivity extends SinglePaneActivity {
    public static final String EXTRA_SESSION_ID = "sessionId";
    public static final String EXTRA_SESSION_PARCEL = "sessionParcel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeIconMode(HOME_MODE_UP);
    }

    @Override
    protected Fragment onCreatePane() {
        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(EXTRA_SESSION_ID)) {
            String sessionId = extras.getString(EXTRA_SESSION_ID);
            return SessionDetailsFragment.newInstance(sessionId, null);
            //TODO remove parcel-related instantiation code
        } else if (extras.containsKey(EXTRA_SESSION_PARCEL)) {
            Session session = extras.getParcelable(EXTRA_SESSION_PARCEL);
            return SessionDetailsFragment.newInstance(session.getId(), session);
        } else {
            throw new IllegalStateException("SessionDetailsActivity requires an EXTRA_SESSION_ID");
        }

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
