package com.devfestmn.sessions.filter;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devfestmn.R;

/**
 * Dialog that allows the user to select a session category filter
 *
 * The calling Fragment should set itself as the target via {@link #setTargetFragment(Fragment, int)},
 * and should implement {@link OnCategoryFilterSelectedListener} to receive a callback when a
 * filter is selected.
 *
 * This allows the target fragment to be maintained through pause/resume events without resorting
 * to dirty tricks.
 *
 * @author bherbst
 */
public class SessionCategoryFilterDialog extends AppCompatDialogFragment implements OnCategoryFilterSelectedListener {
    private static final String ARG_KEY_CATEGORIES = "categories";

    public static SessionCategoryFilterDialog newInstance(String... categories) {
        Bundle args = new Bundle();
        args.putStringArray(ARG_KEY_CATEGORIES, categories);

        SessionCategoryFilterDialog dialog = new SessionCategoryFilterDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.sessions_menu_filter);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_sessions_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView categoriesView = (RecyclerView) view.findViewById(R.id.categories_list);
        categoriesView.setLayoutManager(new LinearLayoutManager(getContext()));

        String[] categories = getArguments().getStringArray(ARG_KEY_CATEGORIES);
        SessionCategoryFilterAdapter adapter = new SessionCategoryFilterAdapter(categories, this);
        categoriesView.setAdapter(adapter);
    }

    @Override
    public void onCategoryFilterSelected(String category) {
        Fragment target = getTargetFragment();
        if (target != null && target instanceof OnCategoryFilterSelectedListener) {
            ((OnCategoryFilterSelectedListener) target).onCategoryFilterSelected(category);
        }
        dismiss();
    }
}
