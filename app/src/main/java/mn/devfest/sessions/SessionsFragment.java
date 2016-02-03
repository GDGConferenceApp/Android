package mn.devfest.sessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.DevFestApplication;
import mn.devfest.R;
import mn.devfest.api.DevFestDataSource;
import mn.devfest.api.model.Session;
import mn.devfest.api.model.Speaker;
import mn.devfest.data.sort.SessionTimeSort;
import mn.devfest.sessions.filter.OnCategoryFilterSelectedListener;
import mn.devfest.sessions.filter.SessionCategoryFilterDialog;
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
public class SessionsFragment extends Fragment implements DevFestDataSource.DataSourceListener, OnCategoryFilterSelectedListener {
    private static final String PREF_KEY_AUTOHIDE = "autohide_past_sessions";

    // TODO this shouldn't be static so we can localize
    private static final String ALL_CATEGORY = "All";

    @Bind(R.id.session_list_recyclerview)
    RecyclerView mSessionRecyclerView;

    @Bind(R.id.loading_progress)
    ProgressBar mLoadingView;

    private SessionListAdapter mAdapter;

    private DevFestDataSource mDataSource;
    private SharedPreferences mPreferences;
    private Subscription mDataUpdateSubscription;

    private boolean mAutohidePastSessions;
    private List<Session> mAllSessions;
    private Set<String> mAllCategories;
    private String mCategoryFilter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mAutohidePastSessions = mPreferences.getBoolean(PREF_KEY_AUTOHIDE, true);
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
                setSessions(mAllSessions);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCategoryFilterSelected(String category) {
        mCategoryFilter = category;

        // Force a re-filter
        setSessions(mAllSessions);
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
        mAllSessions = sessions;
        mDataUpdateSubscription = Observable.from(mAllSessions)
                .doOnNext(session -> addCategoryToCategoryList(session.getCategory()))
                .filter(session -> !(mAutohidePastSessions && hasSessionEnded(session)))
                .filter(session -> mCategoryFilter.equals(ALL_CATEGORY) || mCategoryFilter.equalsIgnoreCase(session.getCategory()))
                .toSortedList(new SessionTimeSort())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateDisplayedSessions);
    }

    /**
     * Add a category to the category list, replacing null categories with "All."
     */
    private void addCategoryToCategoryList(String category) {
        mAllCategories.add(category == null ? ALL_CATEGORY : category);
    }

    /**
     * Check if a session has ended
     * @param session The session
     * @return True if the given session has ended
     */
    private boolean hasSessionEnded(@NonNull Session session) {
        return session.getEndTime().isBeforeNow();
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
