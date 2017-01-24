package mn.devfest.sessions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private DevFestDataSource mDataSource;

    /*
     * This map is a list of session grouping names, keyed by where they will be inserted
     * in the session list. For example, the pair (5, "3:00pm") corresponds to a header
     * of "3:00pm" inserted before the fifth session in the list.
     */
    private Map<Integer, DateTime> mHeaders;

    public SessionListAdapter(DevFestDataSource dataSource) {
        mDataSource = dataSource;
        mSessions = new ArrayList<>(0);
    }

    @Override
    public int getItemViewType(int position) {
        return mHeaders.containsKey(position) ? TYPE_HEADER : TYPE_SESSION;
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
            Session session = mSessions.get(position);

            boolean inSchedule = mDataSource.isInUserSchedule(session.getId());
            sessionHolder.bindSession(session, inSchedule, this);
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            DateTime groupTime = mHeaders.get(position);

            headerHolder.bindTime(groupTime);
        }
    }

    @Override
    public int getItemCount() {
        // The sessions list includes dummy items for headers.
        // See generateHeaders()
        return mSessions.size();
    }

    /**
     * Set the session list to display.
     * @param sessions The sessions. This class assumes that the sessions passed here are already sorted
     *                 based on their time in ascending order.
     */
    public final void setSessions(List<Session> sessions) {
        // Create a defensive copy because we will be mangling the list
        // See generateHeaders()
        mSessions = new ArrayList<>(sessions.size());
        mSessions.addAll(sessions);

        mHeaders = generateHeaders();
    }

    /**
     * Generate the headers for a given list of sessions
     * @return A map of positions in the list to headers for those positions
     */
    private Map<Integer, DateTime> generateHeaders() {
        // Our conference has 10 time slots- for now, just start with that
        Map<Integer, DateTime> headers = new HashMap<>(10);

        if (mSessions.size() > 0) {
            // Put in the first item
            Session firstSession = mSessions.get(0);
            DateTime lastTime = firstSession.getStartDateTime();
            headers.put(0, lastTime);

            // See below.
            mSessions.add(0, null);

            // Put in the rest of the headers
            for (int i = 1; i < mSessions.size(); i++) {
                Session session = mSessions.get(i);

                if (!session.getStartDateTime().isEqual(lastTime)) {
                    // We have found a new group!
                    lastTime = session.getStartDateTime();
                    headers.put(i, lastTime);

                    // Put in a dummy item to represent the header in the session list
                    // This is a bit unorthodox, but it makes indexing into the headers and sessions lists
                    // much easier when binding to the data.
                    mSessions.add(i, null);

                    // Skip the next item, since it is the dummy item we just inserted
                    i++;
                }
            }

        }

        return headers;
    }
    
    @Override
    public boolean onToggleScheduleButtonClicked(Session session) {
        if (mDataSource.isInUserSchedule(session.getId())) {
            mDataSource.removeFromUserSchedule(session.getId());
            return false;
        } else {
            mDataSource.addToUserSchedule(session.getId());
            return true;
        }
    }
}
