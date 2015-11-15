package mn.devfest.sessions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import mn.devfest.base.SinglePaneActivity;

/**
 * Activity that allows the user to rate a session
 *
 * @author bherbst
 */
public class RateSessionActivity extends SinglePaneActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeIconMode(HOME_MODE_UP);
    }

    @Override
    protected Fragment onCreatePane() {
        return new RateSessionFragment();
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
