package mn.devfest.sessions.holder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;
import mn.devfest.api.model.Session;
import mn.devfest.sessions.SessionDetailsActivity;
import mn.devfest.util.CategoryColorUtil;

/**
 * ViewHolder for session rows
 */
public class SessionViewHolder extends RecyclerView.ViewHolder {
    private Session mSession;
    private ToggleInScheduleListener mListener;

    @Bind(R.id.session_row_title)
    TextView mTitleTextView;

    @Bind(R.id.session_row_tag)
    TextView mTagView;

    @Bind(R.id.session_row_room)
    TextView mRoomTextView;

    @Bind(R.id.row_session_toggle_schedule_button)
    ImageButton mToggleScheduleButton;

    public SessionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(view -> {
            Context context = mTitleTextView.getContext();

            Intent sessionDetails = new Intent(context, SessionDetailsActivity.class);
            sessionDetails.putExtra(SessionDetailsActivity.EXTRA_SESSION_ID, mSession.getId());
            context.startActivity(sessionDetails);
        });

        mToggleScheduleButton.setOnClickListener(view -> {
            int resourceId = mListener.onToggleScheduleButtonClicked(mSession);
            mToggleScheduleButton.setImageDrawable(ContextCompat.getDrawable(mToggleScheduleButton.getContext(), resourceId));
        });
    }

    /**
     * Bind to a new session
     * TODO this got gross. Come up with a better approach to in-schedule-status when we're not rushing in small bursts of time.
     * @param session The session that this ViewHolder will represent
     * @param drawableRes The
     * @param listener
     */
    public void bindSession(Session session, @DrawableRes int drawableRes, ToggleInScheduleListener listener) {
        mSession = session;
        mListener = listener;
        mTitleTextView.setText(session.getTitle());
        mRoomTextView.setText(session.getRoom());
        mToggleScheduleButton.setImageDrawable(ContextCompat.getDrawable(mToggleScheduleButton.getContext(), drawableRes));

        Context context = mTitleTextView.getContext();
        int categoryColorRes = CategoryColorUtil.getColorResForCategory(session.getCategory());
        int categoryColor = ContextCompat.getColor(context, categoryColorRes);
        mTagView.setBackgroundColor(categoryColor);

        mTagView.setText(session.getCategory());

        if (session.getCategory() == null) {
            mTagView.setVisibility(View.INVISIBLE);
        } else {
            mTagView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * TODO this approach feels bad in general? Evaluate it when we're less concerned w/ shipping
     * TODO document when we finalize the approach
     */
    public interface ToggleInScheduleListener {
        @DrawableRes
        int onToggleScheduleButtonClicked(Session session);
    }
}
