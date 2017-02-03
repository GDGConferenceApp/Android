package com.devfestmn.schedule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devfestmn.MainActivity;
import com.devfestmn.R;
import com.devfestmn.api.DevFestDataSource;
import com.devfestmn.api.model.Session;
import com.devfestmn.api.model.Speaker;
import com.devfestmn.base.BaseActivity;
import com.devfestmn.data.sort.SessionTimeSort;
import com.devfestmn.sessions.SessionListAdapter;
import com.devfestmn.view.decoration.DividerItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
public class UserScheduleFragment extends Fragment implements DevFestDataSource.DataSourceListener, DevFestDataSource.UserScheduleListener, DevFestDataSource.AuthListener {

    @Bind(R.id.user_agenda_recyclerview)
    RecyclerView mScheduleRecyclerView;

    @Bind(R.id.empty_agenda_view_login_text)
    TextView mLoginPrompt;

    @Bind(R.id.empty_agenda_login_button)
    Button mLoginButton;

    @Bind(R.id.loading_progress)
    ProgressBar mLoadingView;

    @Bind(R.id.schedule_empty_view)
    LinearLayout mEmptyView;

    private SessionListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private DevFestDataSource mDataSource;
    private Subscription mDataUpdateSubscription;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mDataSource == null) {
            //TODO initialize properly
            mDataSource = DevFestDataSource.getInstance(context);
            mDataSource.setDataSourceListener(this);
            mDataSource.setUserScheduleListener(this);
            mDataSource.setAuthListener(this);
        }
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

        getActivity().setTitle(getResources().getString(R.string.user_agenda_title));
    }

    @Override
    public void onScheduleUpdate(List<Session> schedule) {
        setSchedule(schedule);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Refresh the UI with the latest data
        List<Session> userSchedule = mDataSource.getUserSchedule();
        if (mDataSource.getSessions().isEmpty()) {
            mLoadingView.setVisibility(View.VISIBLE);
            showEmptyView(false);
        } else if (userSchedule.size() == 0) {
            mLoadingView.setVisibility(View.GONE);
            showEmptyView(true);
        } else {
            mLoadingView.setVisibility(View.GONE);
            showEmptyView(false);
            setSchedule(userSchedule);
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mDataUpdateSubscription != null) {
            mDataUpdateSubscription.unsubscribe();
        }
    }

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
            // The session list and schedule are the same here
            mAdapter.setSessions(sessions);
            mAdapter.setSchedule(sessions);
            mAdapter.notifyDataSetChanged();
        }
        mLoadingView.setVisibility(View.GONE);
    }

    /**
     * Updates the UI to show if the list is empty or not
     */
    private void showEmptyView(boolean listIsEmpty) {
        if (listIsEmpty) {
            mLoginPrompt.setVisibility(mDataSource.isSignedIn() ? View.GONE : View.VISIBLE);
            mLoginButton.setVisibility(mDataSource.isSignedIn() ? View.GONE : View.VISIBLE);
        }

        mEmptyView.setVisibility(listIsEmpty ? View.VISIBLE : View.GONE);
        mScheduleRecyclerView.setVisibility(listIsEmpty ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.empty_agenda_login_button)
    protected void onLoginClicked() {
        //TODO re-architect to avoid casts
        BaseActivity activity = (BaseActivity) getActivity();
        activity.signIn();
    }

    @OnClick(R.id.empty_agenda_go_to_sessions_button)
    protected void onGoToSessionsClicked() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.navigateToTopLevelFragment(R.id.nav_sessions, true);
    }

    @Override
    public void onSessionsUpdate(List<Session> sessions) {
        //TODO handle redundant onSession and onSchedule updates
        List<Session> schedule = mDataSource.getUserSchedule();
        setSchedule(schedule);
        mLoadingView.setVisibility(View.GONE);
        showEmptyView(schedule.isEmpty());
    }

    @Override
    public void onSpeakersUpdate(List<Speaker> speakers) {
        //Intentionally left blank; no action currently needed
    }

    @Override
    public void onUserScheduleUpdate(List<Session> userSchedule) {
        setSchedule(userSchedule);
        mLoadingView.setVisibility(View.GONE);
        showEmptyView(userSchedule.isEmpty());
    }

    @Override
    public void onAuthEvent() {
        if (mEmptyView.getVisibility() == View.VISIBLE) {
            int visibility = mDataSource.isSignedIn() ? View.GONE : View.VISIBLE;
            mLoginPrompt.setVisibility(visibility);
            mLoginButton.setVisibility(visibility);
        }
    }
}
