package mn.devfest.sessions;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import mn.devfest.R;
import mn.devfest.api.DevFestDataSource;
import mn.devfest.api.model.Session;
import mn.devfest.sessions.holder.HeaderViewHolder;
import mn.devfest.sessions.holder.SessionViewHolder;

/**
 * Adapter for presenting the list of sessions
 *
 * @author pfuentes
 */
public class SessionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SessionViewHolder.ToggleInScheduleListener {
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_SESSION = 2;

    private List<Session> mSessions;
    private List<Session> mSchedule;
    private SparseArray<Session> mPositionSessionMap;
    private DevFestDataSource mDataSource;
    private int totalSize;

    /*
     * This map is a list of session grouping names, keyed by where they will be inserted
     * in the session list. For example, the pair (5, "3:00pm") corresponds to a header
     * of "3:00pm" inserted before the fifth session in the list.
     */
    private SparseArray<DateTime> mHeaders;

    public SessionListAdapter(DevFestDataSource dataSource) {
        mDataSource = dataSource;
        mSchedule = new ArrayList<>(0);
        mSessions = new ArrayList<>(0);
        mPositionSessionMap = new SparseArray<>();
    }

    @Override
    public int getItemViewType(int position) {
        return mHeaders.get(position) != null ? TYPE_HEADER : TYPE_SESSION;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_HEADER:
                View header = inflater.inflate(R.layout.row_session_group_header, parent, false);
                return new HeaderViewHolder(header);

            case TYPE_SESSION:
                View session = inflater.inflate(R.layout.row_session, parent, false);
                return new SessionViewHolder(session);

            default:
                throw new IllegalArgumentException("Unknown view type " + viewType);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SessionViewHolder) {
            SessionViewHolder sessionHolder = (SessionViewHolder) holder;
            Session session = mPositionSessionMap.get(position);

            boolean inSchedule = mSchedule.contains(session);
            sessionHolder.bindSession(session, inSchedule, this);
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            DateTime groupTime = mHeaders.get(position);

            headerHolder.bindTime(groupTime);
        }
    }

    @Override
    public int getItemCount() {
        return totalSize;
    }

    /**
     * Set the session list to display.
     * @param sessions The sessions. This class assumes that the sessions passed here are already sorted
     *                 based on their time in ascending order.
     */
    public final void setSessions(List<Session> sessions) {
        mSessions = new ArrayList<>(sessions.size());
        mSessions.addAll(sessions);

        generateHeadersAndSessionMap();
    }

    /**
     * Set the user's schedule
     * @param schedule The new schedule.
     */
    public final void setSchedule(List<Session> schedule) {
        mSchedule = schedule;
    }

    /**
     * Generate the headers for a given list of sessions
     * @return A map of positions in the list to headers for those positions
     */
    private void generateHeadersAndSessionMap() {
        // Our conference has 10 time slots- for now, just start with that
        SparseArray<DateTime> headers = new SparseArray<>();
        mPositionSessionMap.clear();

        int adapterPosition = 0;
        if (mSessions.size() > 0) {
            // Put in the first item
            Session firstSession = mSessions.get(0);
            DateTime lastTime = firstSession.getStartDateTime();
            headers.put(adapterPosition, lastTime);
            adapterPosition++;

            // put in the first session. Since we start with a header, the first session is at position 1.
            mPositionSessionMap.put(adapterPosition, mSessions.get(0));
            adapterPosition++;

            // Put in the rest of the headers and sessions
            // Since we start with a header, the first session starts at index 1
            for (int i = 1; i < mSessions.size(); i++) {
                Session session = mSessions.get(i);
                mPositionSessionMap.put(adapterPosition, session);
                adapterPosition++;

                if (!session.getStartDateTime().isEqual(lastTime)) {
                    // We have found a new group!
                    lastTime = session.getStartDateTime();
                    headers.put(adapterPosition, lastTime);
                    adapterPosition++;
                }
            }

            totalSize = adapterPosition;
        } else {
            totalSize = 0;
        }

        mHeaders = headers;
    }
    
    @Override
    public boolean onToggleScheduleButtonClicked(Session session) {
        if (mSchedule.contains(session)) {
            mDataSource.removeFromUserSchedule(session.getId());
            return false;
        } else {
            mDataSource.addToUserSchedule(session.getId());
            return true;
        }
    }
}
