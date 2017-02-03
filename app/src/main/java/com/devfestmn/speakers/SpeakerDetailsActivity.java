package com.devfestmn.speakers;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;

import com.devfestmn.R;
import com.devfestmn.base.SinglePaneActivity;

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

        // We need to wait for our content Fragment to exist before executing transitions
        supportPostponeEnterTransition();

        // Set up transitions for content coming into this screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.speaker_details);
            getWindow().setEnterTransition(transition);
            getWindow().setExitTransition(transition);
        }
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
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
