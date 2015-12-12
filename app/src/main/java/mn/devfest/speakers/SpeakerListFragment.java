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

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;
import mn.devfest.view.decoration.DividerItemDecoration;

/**
 * Fragment that displays the list of speakers
 *
 * @author bherbst
 * @author pfuentes
 */
public class SpeakerListFragment extends Fragment {

    @Bind(R.id.speaker_list_recyclerview)
    RecyclerView mSpeakerRecyclerView;

    private SpeakerListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speaker_list, container, false);
        ButterKnife.bind(this, view);

        //TODO set data real date on adapter & remove dummy data
        ArrayList<Speaker> dummyData = new ArrayList<>();
        for (int i = 0; i < 10; i ++) {
            Speaker speaker = new Speaker();
            speaker.setId(i);
            speaker.setName("Speaker #" + i);
            dummyData.add(speaker);
        }

        mAdapter = new SpeakerListAdapter();
        mAdapter.setSpeakers(dummyData);
        mSpeakerRecyclerView.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mSpeakerRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSpeakerRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.view_speaker_details).setOnClickListener(clicked ->
                this.startActivity(new Intent(getContext(), SpeakerDetailsActivity.class)));
    }
    
}
