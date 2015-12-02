package mn.devfest.speakers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import mn.devfest.R;

/**
 * Fragment that displays the list of speakers
 *
 * @author bherbst
 * @author pfuentes
 */
public class SpeakerListFragment extends Fragment {

    @Bind(R.id.session_list_recyclerview)
    RecyclerView mSessionRecyclerView;

    private SpeakerListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_speaker_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.view_speaker_details).setOnClickListener(clicked ->
                this.startActivity(new Intent(getContext(), SpeakerDetailsActivity.class)));
    }
    
}
