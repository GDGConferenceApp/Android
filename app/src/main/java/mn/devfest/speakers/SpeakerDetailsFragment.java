package mn.devfest.speakers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.DevFestApplication;
import mn.devfest.R;
import mn.devfest.api.DevFestDataSource;
import mn.devfest.api.model.Speaker;
import mn.devfest.view.SpeakerView;

/**
 * Fragment that displays details for a particular session
 *
 * @author bherbst
 */
public class SpeakerDetailsFragment extends Fragment {
    private static final String ARG_SPEAKER_ID = "speakerId";

    @Bind(R.id.speaker)
    SpeakerView mSpeakerView;

    private DevFestDataSource mDataSource;
    private Speaker mSpeaker;

    public static SpeakerDetailsFragment newInstance(String speakerId) {
        Bundle args = new Bundle();
        args.putString(ARG_SPEAKER_ID, speakerId);

        SpeakerDetailsFragment frag = new SpeakerDetailsFragment();
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mDataSource == null) {
            mDataSource = DevFestApplication.get(getActivity()).component().datasource();
        }
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
            String speakerId = args.getString(ARG_SPEAKER_ID);

            mSpeaker = mDataSource.getSpeakerById(speakerId);
        } else {
            throw new IllegalStateException("SpeakerDetailsFragment requires a speaker ID passed via newInstance()");
        }

        mSpeakerView.setSpeaker(mSpeaker);
    }
}
