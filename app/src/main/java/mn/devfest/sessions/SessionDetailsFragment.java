package mn.devfest.sessions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;
import mn.devfest.api.model.Session;

/**
 * Fragment that displays details for a particular session
 *
 * @author bherbst
 * @author pfuentes
 */
public class SessionDetailsFragment extends Fragment {
    private static final String ARG_SESSION_ID = "sessionId";
    private static final String ARG_SESSION_PARCEL = "sessionParcel";

    @Bind(R.id.session_details_title)
    TextView mTitleTextview;
    @Bind(R.id.session_details_time)
    TextView mTimeTextview;
    @Bind(R.id.session_details_location)
    TextView mLocationTextview;
    @Bind(R.id.session_details_difficulty)
    TextView mDifficultyTextview;
    @Bind(R.id.session_details_description)
    TextView mDescriptionTextview;
    @Bind(R.id.session_details_speaker_layout)
    LinearLayout mSpeakerLayout;

    private Session mSession;

    //TODO remove parcel-related instantiation code
    public static SessionDetailsFragment newInstance(@NonNull String sessionId, @Nullable Session session) {
        Bundle args = new Bundle();
        args.putString(ARG_SESSION_ID, sessionId);

        SessionDetailsFragment frag = new SessionDetailsFragment();
        frag.setArguments(args);
        if (session != null) {
            args.putParcelable(ARG_SESSION_PARCEL, session);
        }

        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_SESSION_ID)) {
            //TODO get session from data layer
            //TODO remove parcel-related instantiation code
            if (args.containsKey(ARG_SESSION_PARCEL)) {
                mSession = args.getParcelable(ARG_SESSION_PARCEL);
            }
        } else {
            throw new IllegalStateException("SessionDetailsFragment requires a session ID passed via newInstance()");
        }

        //TODO Bind to the session
        getActivity().setTitle(mSession.getTitle());
        mTitleTextview.setText(mSession.getTitle());

        //TODO mTimeTextview.setText(mSession);

        mLocationTextview.setText(mSession.getRoom());
        //TODO mDifficultyTextview.setText(mSession.);
        mDescriptionTextview.setText(mSession.getDescription());
        addSpeakers(mSession.getSpeakers());
    }

    /**
     * Takes an array list of speaker IDs and adds a new SpeakerView to the Speaker layout
     * for each ID
     * @param speakers an array list of speaker IDs representing the speakers
     */
    private void addSpeakers(ArrayList<String> speakers) {
        if (speakers == null || speakers.size() == 0) {
            return;
        }

        for (String speakerId : speakers) {
            //TODO add SpeakerViews to the Speaker Layout
        }
    }

}
