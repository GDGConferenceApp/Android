package mn.devfest.sessions.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import butterknife.Bind;
import butterknife.ButterKnife;
import mn.devfest.R;

/**
 * ViewHolder for grouping headers
 */
public class HeaderViewHolder extends RecyclerView.ViewHolder {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("h:mm a");

    @Bind(R.id.session_group_header)
    TextView mGroupNameView;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     * Bind a time to this header
     */
    public void bindTime(DateTime time) {
        String timeString = time.toLocalTime().toString(TIME_FORMAT);
        mGroupNameView.setText(timeString);
    }
}
