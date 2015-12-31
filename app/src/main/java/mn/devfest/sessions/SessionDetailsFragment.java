package mn.devfest.sessions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    }

}
