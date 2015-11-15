package mn.devfest.sessions;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mn.devfest.R;

/**
 * Fragment that displays the available sessions
 *
 * @author bherbst
 */
public class SessionsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sessions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.view_session).setOnClickListener(clicked ->
                this.startActivity(new Intent(getContext(), SessionDetailsActivity.class)));
    }
}
