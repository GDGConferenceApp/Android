package mn.devfest.speakers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.DevFestApplication;
import mn.devfest.R;
import mn.devfest.api.DevFestDataSource;
import mn.devfest.api.model.Session;
import mn.devfest.api.model.Speaker;
import mn.devfest.view.decoration.DividerItemDecoration;

/**
 * Fragment that displays the list of speakers
 *
 * @author bherbst
 * @author pfuentes
 */
public class SpeakerListFragment extends Fragment implements DevFestDataSource.DataSourceListener {

    @Bind(R.id.speaker_list_recyclerview)
    RecyclerView mSpeakerRecyclerView;

    private SpeakerListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private List<Speaker> mSpeakerData = new ArrayList<>();
    private DevFestDataSource mDataSource;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_speaker_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mAdapter = new SpeakerListAdapter();
        mAdapter.setSpeakers(mSpeakerData);
        mSpeakerRecyclerView.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mSpeakerRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSpeakerRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDataSource = DevFestApplication.get(getActivity()).component().datasource();
        mDataSource.setDataSourceListener(this);
        mSpeakerData = mDataSource.getSpeakers();
    }

    /**
     * Clear callback on detach to prevent null reference errors after the view has been
     */
    @Override
    public void onDetach() {
        super.onDetach();
        //TODO cleanup resources
    }

    @Override
    public List<Session> onSessionsUpdate(List<Session> sessions) {
        //Intentionally left blank; no UI update currently required
        return null;
    }

    @Override
    public List<Speaker> onSpeakersUpdate(List<Speaker> speakers) {
        //TODO address update
        return null;
    }

    @Override
    public List<Session> onUserScheduleUpdate(List<Session> userSchedule) {
        //Intentionally left blank; no UI update currently required
        return null;
    }
}
