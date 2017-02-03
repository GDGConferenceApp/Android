package com.devfestmn.sessions.filter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.devfestmn.R;
import com.devfestmn.util.CategoryColorUtil;

/**
 * Adapter for the session category filter dialog's list of categories
 *
 * @author bherbst
 */
public class SessionCategoryFilterAdapter extends RecyclerView.Adapter<SessionCategoryFilterAdapter.SessionCategoryHolder> {
    private final String[] mCategories;
    private final OnCategoryFilterSelectedListener mListener;

    public SessionCategoryFilterAdapter(String[] categories, @NonNull OnCategoryFilterSelectedListener listener) {
        mCategories = categories;
        mListener = listener;
    }

    @Override
    public SessionCategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.dialog_categories_row, parent, false);

        return new SessionCategoryHolder(row);
    }

    @Override
    public void onBindViewHolder(SessionCategoryHolder holder, int position) {
        holder.bind(mCategories[position], mListener);
    }

    @Override
    public int getItemCount() {
        return mCategories.length;
    }

    static class SessionCategoryHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.category_name)
        TextView categoryName;

        @Bind(R.id.category_icon)
        ImageView categoryIcon;

        private View container;

        public SessionCategoryHolder(View itemView) {
            super(itemView);
            container = itemView;
            ButterKnife.bind(this, itemView);
        }

        public void bind(String category, OnCategoryFilterSelectedListener listener) {
            // Capitalize first letter of each category
            StringBuilder label = new StringBuilder(category.toLowerCase());
            label.setCharAt(0, Character.toUpperCase(label.charAt(0)));

            categoryName.setText(label);

            int colorRes = CategoryColorUtil.getColorResForCategory(category);
            int colorInt = ContextCompat.getColor(categoryIcon.getContext(), colorRes);
            categoryIcon.setColorFilter(colorInt);

            container.setOnClickListener(view -> listener.onCategoryFilterSelected(category));
        }
    }
}
