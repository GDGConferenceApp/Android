package mn.devfest.sessions;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liangfeizc.flowlayout.FlowLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mn.devfest.R;
import mn.devfest.api.model.Session;
import mn.devfest.api.model.Speaker;
import mn.devfest.view.SpeakerView;


/**
 * Fragment that displays details for a particular session
 *
 * @author bherbst
 * @author pfuentes
 */
public class SessionDetailsFragment extends Fragment{
    private static final String ARG_SESSION_ID = "sessionId";
    private static final String ARG_SESSION_PARCEL = "sessionParcel";
    private static final String TIME_FORMAT = "h:mma";

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
    @Bind(R.id.session_details_tag_layout)
    FlowLayout mTagLayout;
    @Bind(R.id.session_details_speaker_heading)
    TextView mSpeakerHeading;
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
        return inflater.inflate(R.layout.fragment_session_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        //Set the session member variable
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

        //Bind to the session
        bindViewsToSession();
    }

    /**
     * Bind the views to the session member variable
     * TODO consider using view binding
     */
    private void bindViewsToSession() {
        getActivity().setTitle(mSession.getTitle());
        mTitleTextview.setText(mSession.getTitle());
        String start = mSession.getStartTime().toLocalTime().toString(TIME_FORMAT);
        String end = mSession.getEndTime().toLocalTime().toString(TIME_FORMAT);
        String startToEnd = String.format(getResources().getString(R.string.start_to_end_time), start, end);
        mTimeTextview.setText(startToEnd);
        mLocationTextview.setText(mSession.getRoom());
        displayTags(mSession.getTags());
        //TODO mDifficultyTextview.setText(mSession.);
        mDescriptionTextview.setText(mSession.getDescription());
        displaySpeakers(mSession.getSpeakers());
    }

    /**
     * Takes an array list of tags and adds new tag views to the Tag layout for each
     * @param tags an array list of tags associated with the session
     */
    private void displayTags(@Nullable ArrayList<String> tags) {
        //TODO uncomment
//        if (tags == null) {
//            return;
//        }

        //TODO delete dummy data and use 'tags' parameter instead
        ArrayList<String> dummyData = new ArrayList<>();
        dummyData.add("Cool");
        dummyData.add("Hard");
        dummyData.add("Really Hard");
        dummyData.add("Security");
        dummyData.add("Android");
        for (String tag : dummyData) {
            TextView textView = new TextView(getActivity());
            textView.setBackgroundResource(R.color.colorWhite);
            textView.setText(tag);
            mTagLayout.addView(textView);
        }
    }

    /**
     * Takes an array list of speaker IDs and adds a new SpeakerView to the Speaker layout
     * for each ID
     * @param speakers an array list of speaker IDs representing the speakers
     */
    private void displaySpeakers(@Nullable ArrayList<String> speakers) {
        if (speakers == null || speakers.size() == 0) {
            mSpeakerHeading.setVisibility(View.GONE);
            return;
        } else if (speakers.size() == 1) {
            mSpeakerHeading.setText(R.string.speaker_heading_singular);
        }

        //Clear any residual views
        mSpeakerLayout.removeAllViews();

        //Add SpeakerViews to the Speaker Layout
        for (String speakerId : speakers) {
            //TODO Speaker speaker = mDataSourceListener.getSpeaker(speakerId);
            //TODO delete this dummy speaker
            Speaker speaker = new Speaker();
            speaker.setId("DummyID");
            speaker.setName("John Doe");
            speaker.bio = getString(R.string.body_copy_placeholder);
            speaker.company = "Mentor Mate";
            speaker.image = "http://digventures.com/leiston-abbey/wp-content/uploads/placeholder-man-grid-240x268.png";
            speaker.twitter = "pfue";
            speaker.website = "google.com";
            SpeakerView speakerView = new SpeakerView(getActivity());
            speakerView.setSpeaker(speaker);
            mSpeakerLayout.addView(speakerView);
        }
    }

    @OnClick(R.id.rate_session)
    void onRateClicked() {
        Intent rateSession = new Intent(getContext(), RateSessionActivity.class);
        rateSession.putExtra(RateSessionActivity.EXTRA_SESSION_ID, mSession.getId());
        startActivity(rateSession);
    }
}
