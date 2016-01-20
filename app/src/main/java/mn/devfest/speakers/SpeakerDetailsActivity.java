package mn.devfest.speakers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import mn.devfest.base.SinglePaneActivity;

/**
 * Activity that displays details for a particular speaker
 *
 * @author bherbst
 */
public class SpeakerDetailsActivity extends SinglePaneActivity {
    public static final String EXTRA_SPEAKER_ID = "speakerId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeIconMode(HOME_MODE_UP);
    }

    @Override
    protected Fragment onCreatePane() {
        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(EXTRA_SPEAKER_ID)) {
            String speakerId = extras.getString(EXTRA_SPEAKER_ID);
            return SpeakerDetailsFragment.newInstance(speakerId);
        } else {
            throw new IllegalStateException("SpeakerDetailsActivity requires an EXTRA_SPEAKER_ID");
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
