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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.DevFestApplication;
import mn.devfest.R;
import mn.devfest.api.DevFestDataSource;
import mn.devfest.api.model.Session;
import mn.devfest.api.model.Speaker;
import mn.devfest.data.sort.SessionTimeSort;
import mn.devfest.sessions.SessionListAdapter;
import mn.devfest.view.decoration.DividerItemDecoration;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Fragment that displays the user's schedule
 *
 * @author bherbst
 * @author pfuentes
 */
public class UserScheduleFragment extends Fragment implements DevFestDataSource.DataSourceListener {

    @Bind(R.id.schedule_recyclerview)
    RecyclerView mScheduleRecyclerView;

    @Bind(R.id.loading_progress)
    ProgressBar mLoadingView;

    @Bind(R.id.empty_image)
    ImageView mEmptyImage;

    @Bind(R.id.empty_text)
    TextView mEmptyText;

    private SessionListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private DevFestDataSource mDataSource;
    private Subscription mDataUpdateSubscription;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mDataSource == null) {
            mDataSource = DevFestApplication.get(getActivity()).component().datasource();
        }
        mDataSource.setDataSourceListener(this);
        mDataSource.updateConferenceInfo();
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

        mAdapter = new SessionListAdapter(mDataSource);
        mScheduleRecyclerView.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mScheduleRecyclerView.setLayoutManager(mLinearLayoutManager);
        mScheduleRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        setSchedule(mDataSource.getUserSchedule());
    }

    @Override
    public void onResume() {
        super.onResume();
        //Refresh the UI with the latest data
        setSchedule(mDataSource.getUserSchedule());
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mDataUpdateSubscription != null) {
            mDataUpdateSubscription.unsubscribe();
        }
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
     * Notify this fragment that we have a new list of sessions for the user's schedule
     *
     * @param sessions the sessions to update the UI with
     */
    public void setSchedule(List<Session> sessions) {
        mDataUpdateSubscription = Observable.from(sessions)
                .toSortedList(new SessionTimeSort())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateDisplayedSessions);
    }

    /**
     * Update the which sessions are displayed
     *
     * @param sessions The sessions to display
     */
    private void updateDisplayedSessions(List<Session> sessions) {
        if (mAdapter != null) {
            mAdapter.setSessions(sessions);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSessionsUpdate(List<Session> sessions) {
        //TODO handle redundant onSession and onSchedule updates
        setSchedule(mDataSource.getUserSchedule());
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
