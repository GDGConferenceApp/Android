package mn.devfest.sessions;

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
 * @author pfuentes
 */
public class SessionDetailsFragment extends Fragment {
    private static final String ARG_SESSION_ID = "sessionId";

    public static SessionDetailsFragment newInstance(int speakerId) {
        Bundle args = new Bundle();
        args.putInt(ARG_SESSION_ID, speakerId);

        SessionDetailsFragment frag = new SessionDetailsFragment();
        frag.setArguments(args);

        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_details, container, false);
    }
}
