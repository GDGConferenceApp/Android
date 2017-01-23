package mn.devfest.sessions;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
import timber.log.Timber;


/**
 * Fragment that displays the available sessions
 *
 * @author bherbst
 * @author pfuentes
 */
public class SessionsFragment extends Fragment implements DevFestDataSource.DataSourceListener, OnCategoryFilterSelectedListener, SessionViewHolder.ToggleInScheduleListener {
    private static final String PREF_KEY_AUTOHIDE = "autohide_past_sessions";

    // TODO this shouldn't be static so we can localize
    public static final String ALL_CATEGORY = "All";
    public static final int MINUTES_BEFORE_ENDTIME_TO_SHOW_SESSION_FEEDBACK = 20;
    public static final String DEVFEST_2017_KEY = "devfest2017";
    public static final String SESSIONS_CHILD_KEY = "schedule";
    public static final String SPEAKERS_CHILD_KEY = "speakers";

    @Bind(R.id.session_list_recyclerview)
    RecyclerView mSessionRecyclerView;

    @Bind(R.id.loading_progress)
    ProgressBar mLoadingView;

    private SharedPreferences mPreferences;
    private Subscription mDataUpdateSubscription;

    private boolean mAutohidePastSessions;
    private List<Session> mAllSessions;
    private Set<String> mAllCategories;
    private String mCategoryFilter;
    private DatabaseReference mFirebaseDatabaseReference;
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

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.child(DEVFEST_2017_KEY)
                .child(SESSIONS_CHILD_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAllSessions.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Timber.d("Snapshot is: " + snapshot.toString());
                    Session session = snapshot.getValue(Session.class);
                    session.setId(snapshot.getKey());
                    mAllSessions.add(session);
                }
                mAdapter.setSessions(mAllSessions);
                mAdapter.notifyDataSetChanged();
                Timber.d(mAllSessions.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(this.getClass().getSimpleName(), "Failed to read value.", databaseError.toException());
            }
        });
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mLayoutManager = new LinearLayoutManager(getContext());
        getActivity().setTitle(getResources().getString(R.string.sessions_title));
        mAdapter = new SessionListAdapter(null);
        mSessionRecyclerView.setAdapter(mAdapter);
        mSessionRecyclerView.setLayoutManager(mLayoutManager);
        mSessionRecyclerView.addItemDecoration(new SessionGroupDividerDecoration(getContext()));
        mSessionRecyclerView.addItemDecoration(new SessionDividerDecoration(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        /*TODO Refresh the UI with the latest data and display loading view if necessary
        if (sessions.size() == 0) {
            mLoadingView.setVisibility(View.VISIBLE);
        } else {
            setSessions(sessions);
        }*/
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
        //TODO understand why this may be null
        if (session.getEndTime() == null) {
            return false;
        }
        return session.getEndTime().minusMinutes(MINUTES_BEFORE_ENDTIME_TO_SHOW_SESSION_FEEDBACK).isBeforeNow();
    }

    /**
     * Update the which sessions are displayed
     * @param sessions The sessions to display
     */
    private void updateDisplayedSessions(List<Session> sessions) {
       //TODO handle filtering sessions
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
