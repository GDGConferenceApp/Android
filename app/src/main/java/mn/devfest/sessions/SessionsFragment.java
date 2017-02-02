package mn.devfest.sessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;
import mn.devfest.api.DevFestDataSource;
import mn.devfest.api.model.Session;
import mn.devfest.api.model.Speaker;
import mn.devfest.data.sort.SessionTimeSort;
import mn.devfest.sessions.filter.OnCategoryFilterSelectedListener;
import mn.devfest.sessions.filter.SessionCategoryFilterDialog;
import mn.devfest.sessions.holder.SessionViewHolder;
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
public class SessionsFragment extends Fragment implements OnCategoryFilterSelectedListener, SessionViewHolder.ToggleInScheduleListener, DevFestDataSource.DataSourceListener {
    private static final String PREF_KEY_AUTOHIDE = "autohide_past_sessions";

    // TODO this shouldn't be static so we can localize
    public static final String ALL_CATEGORY = "All";
    public static final int MINUTES_BEFORE_ENDTIME_TO_SHOW_SESSION_FEEDBACK = 20;

    @Bind(R.id.session_list_recyclerview)
    RecyclerView mSessionRecyclerView;

    @Bind(R.id.loading_progress)
    ProgressBar mLoadingView;

    private SharedPreferences mPreferences;
    private Subscription mDataUpdateSubscription;
    private DevFestDataSource mDataSource;

    private boolean mAutohidePastSessions;
    private List<Session> mAllSessions;
    private List<Session> mSchedule;
    private Set<String> mAllCategories;
    private String mCategoryFilter;
    private SessionListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    public boolean onToggleScheduleButtonClicked(Session session) {
        //TODO handle toggling schedule
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mDataSource = DevFestDataSource.getInstance(getContext());
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mAutohidePastSessions = mPreferences.getBoolean(PREF_KEY_AUTOHIDE, true);
        mDataSource.setDataSourceListener(this);
        mAllSessions = new ArrayList<>(0);
        mAllCategories = new HashSet<>(7);
        mCategoryFilter = ALL_CATEGORY;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.session_list_menu, menu);

        MenuItem autohide = menu.findItem(R.id.session_menu_autohide);
        autohide.setChecked(mAutohidePastSessions);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sessions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mLayoutManager = new LinearLayoutManager(getContext());
        getActivity().setTitle(getResources().getString(R.string.sessions_title));
        mAdapter = new SessionListAdapter(mDataSource);
        mSessionRecyclerView.setAdapter(mAdapter);
        mSessionRecyclerView.setLayoutManager(mLayoutManager);
        mSessionRecyclerView.addItemDecoration(new SessionGroupDividerDecoration(getContext()));
        mSessionRecyclerView.addItemDecoration(new SessionDividerDecoration(getContext()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mDataSource == null) {
            //TODO initialize properly
            mDataSource = DevFestDataSource.getInstance(getContext());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO Display loading view if necessary
        updateSchedule();
        setSessions(mDataSource.getSessions());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.session_menu_filter:
                String[] categories = new String[mAllCategories.size()];
                mAllCategories.toArray(categories);
                SessionCategoryFilterDialog dialog = SessionCategoryFilterDialog.newInstance(categories);
                dialog.setTargetFragment(this, 0);
                dialog.show(getFragmentManager(), "category_filter");
                return true;

            case R.id.session_menu_autohide:
                // Update item state
                item.setChecked(!item.isChecked());

                // Update preferences
                mAutohidePastSessions = item.isChecked();
                mPreferences.edit().putBoolean(PREF_KEY_AUTOHIDE, mAutohidePastSessions).apply();

                // Force a re-filter
                setSessions(mDataSource.getSessions());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCategoryFilterSelected(String category) {
        mCategoryFilter = category;

        // Force a re-filter
        setSessions(mDataSource.getSessions());
    }

    private void updateSchedule() {
        List<Session> oldSchedule;
        if (mSchedule != null) {
            oldSchedule = mSchedule;
        } else {
            oldSchedule = new ArrayList<>(0);
        }
        mSchedule = Collections.unmodifiableList(mDataSource.getUserSchedule());

        DiffUtil.DiffResult sessionDiffResult = mDataSource.calculateScheduleDiff(mAllSessions, oldSchedule, mSchedule);
        mAdapter.setSchedule(mSchedule);
        sessionDiffResult.dispatchUpdatesTo(mAdapter);
    }

    private void checkForNewSessions(List<Session> latestSessions) {
        // TODO See #39 - this diff method doesn't take headers into account, thus reporting incorrect diffs
        //DiffUtil.DiffResult sessionDiffResult = mDataSource.calculateSessionDiff(mAllSessions, latestSessions);
        mAllSessions = latestSessions;
        mAdapter.setSessions(mAllSessions);
        mAdapter.notifyDataSetChanged();
        //sessionDiffResult.dispatchUpdatesTo(mAdapter);
    }

    /**
     * Notify this fragment that we have a new list of sessions for this conference
     * <p>
     * These sessions are not guaranteed to be displayed. The user may have a filter set up that
     * hides one or more sessions.
     *
     * @param sessions the new sessions
     */
    public void setSessions(List<Session> sessions) {
        mAllSessions = sessions;
        mDataUpdateSubscription = Observable.from(mAllSessions)
                .doOnNext(session -> addCategoryToCategoryList(session.getTrack()))
                .filter(session -> !(mAutohidePastSessions && hasSessionEnded(session)))
                .filter(session -> mCategoryFilter.equals(ALL_CATEGORY) || mCategoryFilter.equalsIgnoreCase(session.getTrack()))
                .toSortedList(new SessionTimeSort())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::checkForNewSessions);
    }

    /**
     * Add a category to the category list, replacing null categories with "All."
     */
    private void addCategoryToCategoryList(String category) {
        mAllCategories.add(category == null ? ALL_CATEGORY : category);
    }

    /**
     * Check if a session has ended
     *
     * @param session The session
     * @return True if the given session has ended
     */
    private boolean hasSessionEnded(@NonNull Session session) {
        //TODO understand why this may be null
        if (session.getEndDateTime() == null) {
            return false;
        }
        return session.getEndDateTime().minusMinutes(MINUTES_BEFORE_ENDTIME_TO_SHOW_SESSION_FEEDBACK).isBeforeNow();
    }

    /**
     * Update the which sessions are displayed
     *
     * @param sessions The sessions to display
     */
    private void updateDisplayedSessions(List<Session> sessions) {
        //TODO handle filtering sessions
    }

    @Override
    public void onSessionsUpdate(List<Session> sessions) {
        setSessions(mDataSource.getSessions());
    }

    @Override
    public void onSpeakersUpdate(List<Speaker> speakers) {
        //Intentionally ignored
    }

    @Override
    public void onUserScheduleUpdate(List<Session> userSchedule) {
        updateSchedule();
    }
}
