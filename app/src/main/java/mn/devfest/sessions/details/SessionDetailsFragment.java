package mn.devfest.sessions.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liangfeizc.flowlayout.FlowLayout;

import org.joda.time.DateTime;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mn.devfest.DevFestApplication;
import mn.devfest.R;
import mn.devfest.api.DevFestDataSource;
import mn.devfest.api.model.Session;
import mn.devfest.api.model.Speaker;
import mn.devfest.sessions.rating.RateSessionActivity;
import mn.devfest.view.SpeakerView;


/**
 * Fragment that displays details for a particular session
 *
 * @author bherbst
 * @author pfuentes
 */
public class SessionDetailsFragment extends Fragment {
    private static final String ARG_SESSION_ID = "sessionId";
    private static final String TIME_FORMAT = "h:mma";

    @Bind(R.id.session_details_fab)
    FloatingActionButton mFab;
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

    private DevFestDataSource mDataSource;

    private Session mSession;
    private boolean mSessionHasEnded = false;

    public static SessionDetailsFragment newInstance(@NonNull String sessionId) {
        Bundle args = new Bundle();
        args.putString(ARG_SESSION_ID, sessionId);

        SessionDetailsFragment frag = new SessionDetailsFragment();
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
        return inflater.inflate(R.layout.fragment_session_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        //Set the session member variable
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_SESSION_ID)) {
            //Get session from data layer
            String sessionId = args.getString(ARG_SESSION_ID);
            mSession = mDataSource.getSessionById(sessionId);
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
        //Don't continue if we have no session data; making data disappear would be disruptive
        if (mSession == null) {
            return;
        }

        getActivity().setTitle(getResources().getString(R.string.sessions_title));
        mTitleTextview.setText(mSession.getTitle());
        String start;
        if (mSession.getStartTime() == null) {
            start = "?";
        } else {
            start = mSession.getStartTime().toLocalTime().toString(TIME_FORMAT);
        }
        String end;
        if (mSession.getEndTime() == null) {
            end = "?";
        } else {
            end = mSession.getEndTime().toLocalTime().toString(TIME_FORMAT);
        }
        String startToEnd = String.format(getResources().getString(R.string.start_to_end_time), start, end);
        mTimeTextview.setText(startToEnd);
        mLocationTextview.setText(mSession.getRoom());
        // TODO category
        //displayTags(mSession.getTags());
        //TODO mDifficultyTextview.setText(mSession.);
        mDescriptionTextview.setText(mSession.getDescription());
        displaySpeakers(mSession.getSpeakers());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFabAppearance();
    }

    /**
     * Takes an array list of tags and adds new tag views to the Tag layout for each
     * To clear the tags, call with an empty List of tags for the parameter
     *
     * @param tags an array list of tags associated with the session
     */
    private void displayTags(@NonNull ArrayList<String> tags) {
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
     *
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
            Speaker speaker = mDataSource.getSpeakerById(speakerId);
            SpeakerView speakerView = new SpeakerView(getActivity());
            speakerView.setSpeaker(speaker, false);
            mSpeakerLayout.addView(speakerView);
        }
    }

    /**
     * Updates the button appearance to indicate if the session is in the user's schedule
     */
    private void updateFabAppearance() {
        updateHasSessionEnded();

        //TODO behave differently depending on if the event has ended
        int resourceId = (mDataSource.isInUserSchedule(mSession.getId()))
                ? R.drawable.ic_remove_outline : R.drawable.ic_add_outline;
        Drawable icon = ContextCompat.getDrawable(getContext(), resourceId);
        mFab.setImageDrawable(icon);
    }

    /**
     * Updates the member variable that indicates in the session has ended yet or not
     */
    private void updateHasSessionEnded() {
        DateTime endTime = mSession.getEndTime();

        //Handle missing endTime or startTime
        if (endTime == null) {
            if (mSession.getStartTime() == null) {
                //TODO find a good way to deal with having no start or end time
                return;
            }

            //If we don't know the end time, switch over 8 hours after it started
            DateTime eightHoursAgo = new DateTime().minusHours(8);
            mSessionHasEnded = !mSession.getStartTime().isBefore(eightHoursAgo);
        } else {
            mSessionHasEnded = !endTime.isBeforeNow();
        }
    }

    @OnClick(R.id.session_details_fab)
    public void onToggleInUserScheduleButtonClick(View view) {

        //TODO determine if we should rateSession()

        toggleInUserSchedule();
        updateFabAppearance();
    }

    /**
     * Toggles the status of the session being in or out of the user's schedule
     */
    private void toggleInUserSchedule() {
        String sessionId = mSession.getId();
        if (mDataSource.isInUserSchedule(sessionId)) {
            mDataSource.removeFromUserSchedule(sessionId);
        } else {
            mDataSource.addToUserSchedule(sessionId);
        }
    }

    /**
     * Kicks off an activity to rate this session
     */
    private void rateSession() {
        Intent rateSession = new Intent(getContext(), RateSessionActivity.class);
        rateSession.putExtra(RateSessionActivity.EXTRA_SESSION_ID, mSession.getId());
        startActivity(rateSession);
    }
}
