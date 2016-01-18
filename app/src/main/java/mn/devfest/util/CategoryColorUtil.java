package mn.devfest.util;

import android.support.annotation.ColorRes;

import mn.devfest.R;

/**
 * Utility class that offers a method for converting a session's category to a color to delineate the
 * different categories
 *
 * @author bherbst
 */
public class CategoryColorUtil {
    private static final String CATEGORY_ANDROID = "android";
    private static final String CATEGORY_DESIGN = "design";
    private static final String CATEGORY_CHROMEWEB = "chromeweb";
    private static final String CATEGORY_CLOUD = "cloud";
    private static final String CATEGORY_IOT = "iot";
    private static final String CATEGORY_REACH = "reach";

    /**
     * Get the color associated with a particular category
     */
    @ColorRes
    public static int getColorResForCategory(String category) {
        if (category == null) {
            return R.color.category_default;
        }

        switch (category) {
            case CATEGORY_ANDROID:
                return R.color.category_android;

            case CATEGORY_DESIGN:
                return R.color.category_design;

            case CATEGORY_CHROMEWEB:
                return R.color.category_chromeweb;

            case CATEGORY_CLOUD:
                return R.color.category_cloud;

            case CATEGORY_IOT:
                return R.color.category_iot;

            case CATEGORY_REACH:
                return R.color.category_reach;

            default:
                return R.color.category_default;
        }
    }

    private CategoryColorUtil() {}
}
