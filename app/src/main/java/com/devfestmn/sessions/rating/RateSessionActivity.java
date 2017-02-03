package com.devfestmn.sessions.rating;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.devfestmn.base.SinglePaneActivity;

/**
 * Activity that allows the user to rate a session
 *
 * @author bherbst
 */
public class RateSessionActivity extends SinglePaneActivity {
    public static final String EXTRA_SESSION_ID = "extra_session_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeIconMode(HOME_MODE_UP);
    }

    @Override
    protected Fragment onCreatePane() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_SESSION_ID)) {
            String sessionId = extras.getString(EXTRA_SESSION_ID);
            return RateSessionFragment.newInstance(sessionId);
        } else {
            throw new IllegalStateException("RateSessionActivity requires a session ID passed in via EXTRA_SESSION_ID");
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
