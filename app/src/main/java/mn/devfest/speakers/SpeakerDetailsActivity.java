package mn.devfest.speakers;

import android.support.v4.app.Fragment;

import mn.devfest.base.SinglePaneActivity;

/**
 * Activity that displays details for a particular speaker
 *
 * @author bherbst
 */
public class SpeakerDetailsActivity extends SinglePaneActivity {

    @Override
    protected Fragment onCreatePane() {
        return new SpeakerDetailsFragment();
    }
    
}
