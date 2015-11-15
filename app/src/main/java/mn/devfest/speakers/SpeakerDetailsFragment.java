package mn.devfest.speakers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mn.devfest.R;

/**
 * Fragment that displays details for a particular session
 *
 * @author bherbst
 */
public class SpeakerDetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_speaker_details, container, false);
    }
}
