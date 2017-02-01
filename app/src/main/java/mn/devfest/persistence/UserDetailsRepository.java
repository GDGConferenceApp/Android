package mn.devfest.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Stores and provides the details for the currently logged in user
 */
public class UserDetailsRepository {

    private static final String NAME_KEY = "USER_NAME";
    private static final String EMAIL_KEY = "USER_EMAIL";
    private static final String PHOTO_URL_KEY = "USER_PHOTO_URL";
    private final Context mContext;
    SharedPreferences mSharedPreferences;

    public UserDetailsRepository(@NonNull Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void setUserName(String name) {
        saveStringValue(NAME_KEY, name);
    }

    public void setUserEmail(String email) {
        saveStringValue(EMAIL_KEY, email);
    }

    public void setPhotoUri(Uri photoUri) {
        String urlString = photoUri == null ? "" : photoUri.toString();
        saveStringValue(PHOTO_URL_KEY, urlString);
    }

    @Nullable
    public String getUserName() {
        return mSharedPreferences.getString(NAME_KEY, null);
    }

    @Nullable
    public String getUserEmail() {
        return mSharedPreferences.getString(EMAIL_KEY, null);
    }

    @Nullable
    public Uri getPhotoUri() {
        return Uri.parse(mSharedPreferences.getString(PHOTO_URL_KEY, null));
    }

    private void saveStringValue(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Removes all stored user details
     */
    public void clearUserDetails() {
        setPhotoUri(null);
        setUserEmail(null);
        setUserName(null);
    }
}
