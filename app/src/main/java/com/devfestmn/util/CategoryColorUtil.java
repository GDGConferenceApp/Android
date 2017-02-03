package com.devfestmn.util;

import android.support.annotation.ColorRes;

import com.devfestmn.R;

/**
 * Utility class that offers a method for converting a session's category to a color to delineate the
 * different categories
 *
 * @author bherbst
 */
public class CategoryColorUtil {
    private static final String CATEGORY_ANDROID = "android";
    private static final String CATEGORY_DESIGN = "design";
    private static final String CATEGORY_WEB = "web";
    private static final String CATEGORY_CLOUD = "cloud";
    private static final String CATEGORY_IOT = "iot";

    /**
     * Get the color associated with a particular category
     */
    @ColorRes
    public static int getColorResForCategory(String category) {
        if (category == null) {
            return R.color.category_default;
        }

        category = category.toLowerCase();

        switch (category) {
            case CATEGORY_ANDROID:
                return R.color.category_android;

            case CATEGORY_DESIGN:
                return R.color.category_design;

            case CATEGORY_WEB:
                return R.color.category_web;

            case CATEGORY_CLOUD:
                return R.color.category_cloud;

            case CATEGORY_IOT:
                return R.color.category_iot;

            default:
                return R.color.category_default;
        }
    }

    private CategoryColorUtil() {}
}
