package mn.devfest.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import mn.devfest.MainActivity;
import mn.devfest.R;

/**
 * Base activity for all screens in the app
 *
 * @author bherbst
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HOME_MODE_DRAWER, HOME_MODE_UP})
    public @interface HomeMode {}

    public static final int HOME_MODE_DRAWER = 1;
    public static final int HOME_MODE_UP = 2;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // We will enable transitions for all screens
            supportRequestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            supportRequestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        }

        super.onCreate(savedInstanceState);
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
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    navigateTo(menuItem.getItemId());
                    return true;
                });
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
}