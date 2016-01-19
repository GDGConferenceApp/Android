package mn.devfest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import mn.devfest.base.BaseActivity;
import mn.devfest.map.MapFragment;
import mn.devfest.schedule.UserScheduleFragment;
import mn.devfest.sessions.SessionsFragment;
import mn.devfest.speakers.SpeakerListFragment;

/**
 * Main DevFest Activity. Handle navigation between top-level screens
 */
public class MainActivity extends BaseActivity {
    /**
     * Intent extra specifying a navigation destination
     *
     * The value associated with this extra should be the ID of the selected navigation item
     */
    public static final String EXTRA_NAVIGATION_DESTINATION = "navigation_destination";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_pane);

        if (savedInstanceState == null) {
            int navId = getIntent().getIntExtra(EXTRA_NAVIGATION_DESTINATION, R.id.nav_schedule);
            navigateToTopLevelFragment(navId, false);
        }
    }

    @Override
    protected void navigateTo(int navItemId) {
        navigateToTopLevelFragment(navItemId, true);
    }

    /**
     * Navigate to a top-level Fragment
     * @param navItemId The ID of the top-level Fragment in the nav drawer
     * @param addToBackStack True to add this transition to the back stack
     */
    private void navigateToTopLevelFragment(int navItemId, boolean addToBackStack) {
        Fragment destination;
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

            default:
                throw new IllegalArgumentException("Unknown item ID " + navItemId);
        }

        FragmentTransaction transaction= getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_container, destination);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }
}
