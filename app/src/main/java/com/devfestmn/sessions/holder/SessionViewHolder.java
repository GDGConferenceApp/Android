package com.devfestmn.sessions.holder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.devfestmn.R;
import com.devfestmn.api.model.Session;
import com.devfestmn.sessions.details.SessionDetailsActivity;
import com.devfestmn.util.CategoryColorUtil;

/**
 * ViewHolder for session rows
 */
public class SessionViewHolder extends RecyclerView.ViewHolder {
    private Session mSession;
    private ToggleInScheduleListener mListener;

    @Bind(R.id.session_row_title)
    TextView mTitleTextView;

    @Bind(R.id.discipline_color)
    FrameLayout mDisciplineColor;

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
            boolean isInUserSchdedule = mListener.onToggleScheduleButtonClicked(mSession);
            updateScheduleToggleButton(isInUserSchdedule);
        });
    }

    /**
     * Bind to a new session
     * TODO this got gross. Come up with a better approach to in-schedule-status when we're not rushing in small bursts of time.
     *
     * @param session          The session that this ViewHolder will represent
     * @param isInUserSchedule Indicates whether this session is currently in the user's schedule
     * @param listener
     */
    public void bindSession(Session session, boolean isInUserSchedule, ToggleInScheduleListener listener) {
        mSession = session;
        mListener = listener;
        mTitleTextView.setText(session.getTitle());
        mRoomTextView.setText(session.getRoom());
        updateScheduleToggleButton(isInUserSchedule);

        Context context = mTitleTextView.getContext();
        int categoryColorRes = CategoryColorUtil.getColorResForCategory(session.getTrack());
        int categoryColor = ContextCompat.getColor(context, categoryColorRes);
        mDisciplineColor.setBackgroundColor(categoryColor);
        mTagView.setTextColor(categoryColor);

        mTagView.setText(session.getTrack());

        if (session.getTrack() == null) {
            mTagView.setVisibility(View.INVISIBLE);
        } else {
            mTagView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Changes the color and shape of the schedule-toggle button appropriately based on if the session is in
     * the user's schedule
     *
     * @param isInUserSchedule indicates if this session is in the user's schedule
     */
    private void updateScheduleToggleButton(boolean isInUserSchedule) {
        int iconRes = isInUserSchedule ? R.drawable.ic_star_rate_black_18dp : R.drawable.ic_star_hollow;
        int colorRes = isInUserSchedule ? R.color.colorAccent : R.color.mediumGray;

        Context context = mToggleScheduleButton.getContext();
        Drawable image = ContextCompat.getDrawable(mToggleScheduleButton.getContext(), iconRes);
        int color = ContextCompat.getColor(context, colorRes);

        mToggleScheduleButton.setImageDrawable(image);
        mToggleScheduleButton.setColorFilter(color);
    }

    /**
     * TODO this approach feels bad in general? Evaluate it when we're less concerned w/ shipping
     * TODO document when we finalize the approach
     */
    public interface ToggleInScheduleListener {
        boolean onToggleScheduleButtonClicked(Session session);
    }
}
