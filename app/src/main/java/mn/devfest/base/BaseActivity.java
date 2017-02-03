package mn.devfest.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import mn.devfest.MainActivity;
import mn.devfest.R;
import mn.devfest.api.DevFestDataSource;
import mn.devfest.persistence.UserDetailsRepository;
import mn.devfest.speakers.SpeakerImageTransformation;
import timber.log.Timber;

/**
 * Base activity for all screens in the app
 *
 * @author bherbst
 */
public abstract class BaseActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, DevFestDataSource.LoginPromptListener {
    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 1111;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HOME_MODE_DRAWER, HOME_MODE_UP})
    public @interface HomeMode {}

    public static final int HOME_MODE_DRAWER = 1;
    public static final int HOME_MODE_UP = 2;

    private ImageView mUserImageview;

    private TextView mUserNameTextview;

    private TextView mUserEmailTextview;

    private MenuItem mLoginLogoutMenuItem;

    private DrawerLayout mDrawerLayout;

    private DevFestDataSource mDataSource;

    private FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // We will enable transitions for all screens
            supportRequestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            supportRequestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);
        mDataSource = DevFestDataSource.getInstance(this);
        mDataSource.setLoginPromptListener(this);
        initializeGoogleAuth();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupToolbarActionBar();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent()
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void initializeGoogleAuth() {
        // Configure Google Sign In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //Build a GoogleApiClient with access to the Google Sign-In API and the options specified
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                //User is signed in
                Timber.d("onAuthStateChanged() with user signed in as %s", user.getDisplayName());
                onLoggedIn();
            } else {
                //User is signed out
                Timber.d("onAuthStateChanged() with user signed out");
                onLoggedOut();
            }
        };
    }

    /**
     * Set up the toolbar
     */
    private void setupToolbarActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

        }

        // Default to showing the nav drawer icon
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            setHomeIconMode(HOME_MODE_DRAWER);
        }
    }

    /**
     * Set what mode to use for the home icon
     */
    protected void setHomeIconMode(@HomeMode int mode) {
        final ActionBar ab = getSupportActionBar();
        if (ab == null) {
            return;
        }

        switch (mode) {
            case HOME_MODE_DRAWER:
                ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
                break;

            case HOME_MODE_UP:
                ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
                break;
        }
    }

    /**
     * Set up the navigation drawer
     */
    private void setupNavDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        View navHeaderLayout = navigationView.getHeaderView(0);
        mUserImageview = (ImageView) navHeaderLayout.findViewById(R.id.nav_header_user_profile_image);
        mUserEmailTextview = (TextView) navHeaderLayout.findViewById(R.id.nav_header_email_textview);
        mUserNameTextview = (TextView) navHeaderLayout.findViewById(R.id.nav_header_name_textview);
        mLoginLogoutMenuItem = navigationView.getMenu().findItem(R.id.nav_login_or_logout);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    handleMenuClick(menuItem.getItemId());
                    return true;
                });
    }

    private void handleMenuClick(int itemId) {
        //Handle auth requests, or navigate
        if (itemId == R.id.nav_login_or_logout) {
            if (mFirebaseAuth.getCurrentUser() == null) {
                signIn();
            } else {
                signOut();
            }
        } else {
            navigateTo(itemId);
        }
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
    }

    public void signOut() {
        mFirebaseAuth.signOut();
        mDataSource.setGoogleAccount(null);
    }

    /**
     * Navigate to a navigation drawer item
     *
     * The default behavior is to launch a {@link MainActivity} with {@link MainActivity#EXTRA_NAVIGATION_DESTINATION}
     * set to the navigation drawer item's ID
     *
     * @param navItemId The ID of the navigation drawer item that should be displayed
     */
    protected void navigateTo(int navItemId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_NAVIGATION_DESTINATION, navItemId);
        startActivity(intent);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            Timber.d("handleSignInResult successful authentication change");
            // Signed in successfully, show authenticated UI.
            mDataSource.setGoogleAccount(result.getSignInAccount());
            if (result.getSignInAccount() != null) {
                onLoggedIn();
            } else {
                // Signed out, show unauthenticated UI.
                onLoggedOut();
            }
        } else {
            Timber.d("handleSignInResult authentication change failed");
        }
    }

    private void onLoggedIn() {
        Timber.d("onLoggedIn called");
        mLoginLogoutMenuItem.setTitle(R.string.drawer_item_logout);
        UserDetailsRepository userDetails = mDataSource.getUserDetailsRepository();
        if (userDetails != null) {
            bindUserDetailsToNavDrawer(userDetails);
        }
    }

    private void onLoggedOut() {
        Timber.d("onLoggedOut called");
        mLoginLogoutMenuItem.setTitle(R.string.drawer_item_login);
        mDataSource.clearUserDetails();
        mDataSource.setGoogleAccount(null);
        mUserImageview.setVisibility(View.GONE);
        mUserEmailTextview.setVisibility(View.GONE);
        mUserNameTextview.setVisibility(View.GONE);
    }

    private void bindUserDetailsToNavDrawer(UserDetailsRepository userDetails) {
        if (userDetails.getPhotoUri() != null) {
            Picasso.with(this)
                    .load(userDetails.getPhotoUri())
                    .transform(new SpeakerImageTransformation())
                    .placeholder(R.drawable.ic_account_circle_white_48dp)
                    .into(mUserImageview);
            mUserImageview.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(userDetails.getUserName())) {
            mUserNameTextview.setText(userDetails.getUserName());
            mUserNameTextview.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(userDetails.getUserEmail())) {
            mUserEmailTextview.setText(userDetails.getUserEmail());
            mUserEmailTextview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void promptUserToLogin() {
        Snackbar.make(
                findViewById(android.R.id.content),
                R.string.snackbar_login_message,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.drawer_item_login, view -> signIn())
                .show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO handle connection failure
    }
}