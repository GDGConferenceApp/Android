package com.devfestmn.sessions.filter;

/**
 * Callback for when a category is selected from the filter dialog
 *
 * @author bherbst
 */
public interface OnCategoryFilterSelectedListener {

    /**
     * Called when a category is selected
     * @param category The selected category
     */
    void onCategoryFilterSelected(String category);
}
