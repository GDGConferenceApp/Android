package mn.devfest.schedule;

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
import mn.devfest.sessions.SessionListAdapter;
import mn.devfest.view.decoration.DividerItemDecoration;

/**
 * Fragment that displays the user's schedule
 *
 * @author bherbst
 * @author pfuentes
 */
public class UserScheduleFragment extends Fragment implements DevFestDataSource.DataSourceListener {

    @Bind(R.id.schedule_recyclerview)
    RecyclerView mScheduleRecyclerView;

    private SessionListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private List<Session> mScheduleSessions = new ArrayList<>();
    private DevFestDataSource mDataSource;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mDataSource == null) {
            mDataSource = DevFestApplication.get(getActivity()).component().datasource();
        }
        mDataSource.setDataSourceListener(this);
        setSchedule(mDataSource.getUserSchedule());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mAdapter = new SessionListAdapter();
        mAdapter.setSessions(mScheduleSessions);
        mScheduleRecyclerView.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mScheduleRecyclerView.setLayoutManager(mLinearLayoutManager);
        mScheduleRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public void onResume() {
        super.onResume();
        //Refresh the UI with the latest data
        setSchedule(mDataSource.getUserSchedule());
    }

    /**
     * TODO update documentation
     */
    @Override
    public void onDetach() {
        super.onDetach();
        //TODO cleanup resources
    }

    /**
     * Updates the data set, and notifies the adapter of the data set change
     * @param sessions the sessions to update the UI with
     */
    public void setSchedule(List<Session> sessions) {
        mScheduleSessions = sessions;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSessionsUpdate(List<Session> sessions) {
        setSchedule(sessions);
    }

    @Override
    public void onSpeakersUpdate(List<Speaker> speakers) {
        //Intentionally left blank; no action currently needed
    }

    @Override
    public void onUserScheduleUpdate(List<Session> userSchedule) {
        setSchedule(userSchedule);
    }
}
