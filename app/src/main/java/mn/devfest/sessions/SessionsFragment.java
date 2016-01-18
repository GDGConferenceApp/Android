package mn.devfest.sessions;

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
 * Fragment that displays the available sessions
 *
 * @author bherbst
 * @author pfuentes
 */
public class SessionsFragment extends Fragment implements DevFestDataSource.DataSourceListener {

    @Bind(R.id.session_list_recyclerview)
    RecyclerView mSessionRecyclerView;

    private SessionListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private List<Session> mSessions = new ArrayList<>();
    private DevFestDataSource mDataSource;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sessions, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mDataSource == null) {
            mDataSource = DevFestApplication.get(getActivity()).component().datasource();
        }
        mDataSource.setDataSourceListener(this);
        setSessions(mDataSource.getSessions());
    }

    @Override
    public void onResume() {
        super.onResume();
        //Refresh the UI with the latest data
        setSessions(mDataSource.getSessions());
    }

    /**
     * TODO update documentation
     */
    @Override
    public void onDetach() {
        super.onDetach();
        //TODO cleanup resources
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mAdapter = new SessionListAdapter();
        mAdapter.setSessions(mSessions);
        mSessionRecyclerView.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mSessionRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSessionRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
    }

    /**
     * Updates the data set, and notifies the adapter of the data set change
     * @param sessions the sessions to update the UI with
     */
    public void setSessions(List<Session> sessions) {
        mSessions = sessions;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSessionsUpdate(List<Session> sessions) {
        setSessions(sessions);
    }

    @Override
    public void onSpeakersUpdate(List<Speaker> speakers) {
        //TODO address update
    }

    @Override
    public void onUserScheduleUpdate(List<Session> userSchedule) {
        //TODO address update
    }
}
