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
import android.widget.ProgressBar;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.DevFestApplication;
import mn.devfest.R;
import mn.devfest.api.DevFestDataSource;
import mn.devfest.api.model.Session;
import mn.devfest.api.model.Speaker;
import mn.devfest.data.sort.SessionTimeSort;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Fragment that displays the available sessions
 *
 * @author bherbst
 * @author pfuentes
 */
public class SessionsFragment extends Fragment implements DevFestDataSource.DataSourceListener {

    @Bind(R.id.session_list_recyclerview)
    RecyclerView mSessionRecyclerView;

    @Bind(R.id.loading_progress)
    ProgressBar mLoadingView;

    private SessionListAdapter mAdapter;

    private DevFestDataSource mDataSource;
    private Subscription mDataUpdateSubscription;

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
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        getActivity().setTitle(getResources().getString(R.string.sessions_title));
        mAdapter = new SessionListAdapter(mDataSource);
        mSessionRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mSessionRecyclerView.setLayoutManager(linearLayoutManager);
        mSessionRecyclerView.addItemDecoration(new SessionGroupDividerDecoration(getContext()));
        mSessionRecyclerView.addItemDecoration(new SessionDividerDecoration(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        //Refresh the UI with the latest data
        List<Session> sessions = mDataSource.getSessions();

        if (sessions.size() == 0) {
            mLoadingView.setVisibility(View.VISIBLE);
        } else {
            setSessions(sessions);
        }
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
        ButterKnife.unbind(this);
        //TODO cleanup resources
    }

    /**
     * Notify this fragment that we have a new list of sessions for this conference
     *
     * These sessions are not guaranteed to be displayed. The user may have a filter set up that
     * hides one or more sessions.
     *
     * @param sessions the new sessions
     */
    public void setSessions(List<Session> sessions) {
        mDataUpdateSubscription = Observable.from(sessions)
                .toSortedList(new SessionTimeSort())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateDisplayedSessions);
    }

    /**
     * Update the which sessions are displayed
     * @param sessions The sessions to display
     */
    private void updateDisplayedSessions(List<Session> sessions) {
        mAdapter.setSessions(sessions);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSessionsUpdate(List<Session> sessions) {
        setSessions(sessions);
        mLoadingView.setVisibility(View.GONE);
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
