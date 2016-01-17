package mn.devfest.sessions.holder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;
import mn.devfest.api.model.Session;
import mn.devfest.sessions.SessionDetailsActivity;

/**
 * ViewHolder for session rows
 */
public class SessionViewHolder extends RecyclerView.ViewHolder {
    private Session mSession;

    @Bind(R.id.session_row_title)
    TextView mTitleTextView;

    public SessionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(view -> {
            Context context = mTitleTextView.getContext();

            Intent sessionDetails = new Intent(context, SessionDetailsActivity.class);
            sessionDetails.putExtra(SessionDetailsActivity.EXTRA_SESSION_ID, mSession.getId());
            sessionDetails.putExtra(SessionDetailsActivity.EXTRA_SESSION_PARCEL, mSession);
            context.startActivity(sessionDetails);
        });

    }

    /**
     * Bind to a new session
     *
     * @param session The session that this ViewHolder will represent
     */
    public void bindSession(Session session) {
        mSession = session;
        mTitleTextView.setText(session.getTitle());
    }
}
