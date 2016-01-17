package mn.devfest.sessions;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import mn.devfest.sessions.holder.HeaderViewHolder;

/**
 * RecyclerView decoration that adds a line after each grouping of sessions (currently
 * grouped by starting time).
 *
 * @author bherbst
 */
public class SessionGroupDividerDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    private Drawable mDivider;

    public SessionGroupDividerDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            // We want dividers before each header, but not before the very first item in the list
            if (i != 0 && isGroupHeader(child, parent)) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getTop() + params.topMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (isGroupHeader(view, parent)) {
            outRect.set(0,  mDivider.getIntrinsicHeight(), 0, 0);
        }
    }

    /**
     * Check if a View is a group header
     * @param view The View to check
     * @param parent The parent RecyclerView
     * @return True if that View is a group header
     */
    public boolean isGroupHeader(View view, RecyclerView parent) {
        RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
        return holder instanceof HeaderViewHolder;
    }
}
