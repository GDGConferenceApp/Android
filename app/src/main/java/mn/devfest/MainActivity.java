package mn.devfest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import mn.devfest.base.BaseActivity;
import mn.devfest.map.MapFragment;
import mn.devfest.schedule.UserScheduleFragment;
import mn.devfest.sessions.SessionsFragment;
import mn.devfest.speakers.SpeakerListFragment;
import timber.log.Timber;

/**
 * Main DevFest Activity. Handle navigation between top-level screens
 */
public class MainActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    /**
     * Intent extra specifying a navigation destination
     * <p>
     * The value associated with this extra should be the ID of the selected navigation item
     */
    public static final String EXTRA_NAVIGATION_DESTINATION = "navigation_destination";

    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 1111;

    private FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleApiClient mGoogleApiClient;

    private GoogleSignInAccount mGoogleAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_pane);

        if (savedInstanceState == null) {
            int navId = getIntent().getIntExtra(EXTRA_NAVIGATION_DESTINATION, R.id.nav_schedule);
            navigateToTopLevelFragment(navId, false);
        }
        initializeGoogleAuth();


        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                //User is signed in
                Timber.d("onAuthStateChanged() with user signed in as %s", user.getDisplayName());
            } else {
                //User is signed out
                Timber.d("onAuthStateChanged() with user signed out");
            }
        };
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
    protected void navigateTo(int navItemId) {
        navigateToTopLevelFragment(navItemId, true);
    }


    //TODO evaluate if we want this public, or if we should try something else like listeners

    /**
     * Navigate to a top-level Fragment
     *
     * @param navItemId      The ID of the top-level Fragment in the nav drawer
     * @param addToBackStack True to add this transition to the back stack
     */
    public void navigateToTopLevelFragment(int navItemId, boolean addToBackStack) {
        Fragment destination = null;
        switch (navItemId) {
            case R.id.nav_schedule:
                destination = new UserScheduleFragment();
                break;

            case R.id.nav_sessions:
                destination = new SessionsFragment();
                break;

            case R.id.nav_speakers:
                destination = new SpeakerListFragment();
                break;

            case R.id.nav_map:
                destination = new MapFragment();
                break;

            case R.id.nav_login_or_logout:
                if (mGoogleAccount == null) {
                    signIn();
                } else {
                    signOut();
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown item ID " + navItemId);
        }

        if (destination != null) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_container, destination);
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
    }

    public void signOut() {
        mFirebaseAuth.signOut();
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

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            Timber.d("handleSignInResult successful login");
            // Signed in successfully, show authenticated UI.
            mGoogleAccount = result.getSignInAccount();
            //TODO update the UI
        } else {
            Timber.d("handleSignInResult login failed");
            // Signed out, show unauthenticated UI.
            //TODO update the UI
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO handle connection failure
    }
}
