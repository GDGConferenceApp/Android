package mn.devfest.sessions.rating;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mn.devfest.R;
import mn.devfest.api.DevFestDataSource;
import mn.devfest.api.model.Feedback;

/**
 * Fragment that allows the user to rate a session
 *
 * @author bherbst
 */
public class RateSessionFragment extends Fragment {
    private static final String ARG_SESSION_ID = "arg_session_id";

    @Bind(R.id.speaker_rating) RatingBar mSpeakerBar;
    @Bind(R.id.content_rating) RatingBar mContentBar;
    @Bind(R.id.session_rating) RatingBar mSessionBar;

    private String mSessionId;
    private DevFestDataSource mDataSource;

    /**
     * Get a new RateSessionFragment for a given session
     * @param sessionId The ID of the session to rate
     * @return A new RatSessionFragment for the given session
     */
    public static RateSessionFragment newInstance(String sessionId) {
        Bundle args = new Bundle();
        args.putString(ARG_SESSION_ID, sessionId);

        RateSessionFragment frag =  new RateSessionFragment();
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSessionId = getActivity().getIntent().getStringExtra(RateSessionActivity.EXTRA_SESSION_ID);
        //TODO initialize the feedback API
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rate_session, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mDataSource == null) {
            mDataSource = DevFestDataSource.getInstance(context);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.submit_feedback_button)
    void onSubmitClicked() {
        int speaker = (int) mSpeakerBar.getRating();
        int content = (int) mContentBar.getRating();
        int session = (int) mSessionBar.getRating();

        Feedback feedback = new Feedback(speaker, content, session);
        mDataSource.setSessionFeedback(mSessionId, feedback);

        // We are going to assume this succeeded and trust that Firebase will sync properly.
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }
}
