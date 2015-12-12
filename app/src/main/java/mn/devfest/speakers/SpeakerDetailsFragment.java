package mn.devfest.speakers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;

/**
 * Fragment that displays details for a particular session
 *
 * @author bherbst
 */
public class SpeakerDetailsFragment extends Fragment {
    private static final String ARG_SPEAKER_ID = "speakerId";

    @Bind(R.id.bio) TextView mBio;

    private Speaker mSpeaker;

    public static SpeakerDetailsFragment newInstance(int speakerId) {
        Bundle args = new Bundle();
        args.putInt(ARG_SPEAKER_ID, speakerId);

        SpeakerDetailsFragment frag = new SpeakerDetailsFragment();
        frag.setArguments(args);

        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_speaker_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_SPEAKER_ID)) {
            int speakerId = args.getInt(ARG_SPEAKER_ID);

            // TODO get real speaker
            mSpeaker = new Speaker();
            mSpeaker.setId(speakerId);
            mSpeaker.setName("Speaker " + speakerId);
        } else {
            throw new IllegalStateException("SpeakerDetailsFragment requires a speaker ID passed via newInstance()");
        }

        // Bind to the speaker
        getActivity().setTitle(mSpeaker.getName());
        mBio.setText(R.string.speaker_bio_placeholder);
    }
}
