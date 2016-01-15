package mn.devfest.sessions;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;
import mn.devfest.api.model.Session;

/**
 * Adapter for presenting the list of sessions
 *
 * @author pfuentes
 */
public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.SessionViewHolder> {

    private List<Session> mSessions;

    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SessionViewHolder holder, int position) {
        Session session = mSessions.get(position);
        if (session == null) {
            return;
        }
        holder.mTitleTextView.setText(session.getTitle());
    }

    @Override
    public int getItemCount() {
        return mSessions == null ? 0 : mSessions.size();
    }

    public void setSessions(List<Session> sessions) {
        mSessions = sessions;
    }

    public class SessionViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.session_row_title)
        TextView mTitleTextView;

        public SessionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view -> {
                Context context = mTitleTextView.getContext();
                int adapterPosition = getAdapterPosition();
                Session session = mSessions.get(adapterPosition);

                Intent sessionDetails = new Intent(context, SessionDetailsActivity.class);
                sessionDetails.putExtra(SessionDetailsActivity.EXTRA_SESSION_ID, session.getId());
                sessionDetails.putExtra(SessionDetailsActivity.EXTRA_SESSION_PARCEL, session);
                context.startActivity(sessionDetails);
            });

        }
    }
}
