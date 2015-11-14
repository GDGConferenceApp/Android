package mn.devfest;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;

import mn.devfest.adapters.RecyclerViewAdapter;
import mn.devfest.adapters.TabPagerAdapter;
import mn.devfest.base.BaseActivity;


public class MainActivity extends BaseActivity implements RecyclerViewAdapter.OnItemClickListener, AppBarLayout.OnOffsetChangedListener {

    private ViewPager mViewPager;
    private TabPagerAdapter mAdapter;
    private AppBarLayout appBarLayout;

    ///////////////////////////////////////
    // LIFE CYCLE
    ///////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new TabPagerAdapter(this.getSupportFragmentManager());
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabsFromPagerAdapter(mAdapter);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onStop() {
        appBarLayout.removeOnOffsetChangedListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mAdapter.destroy();
        super.onDestroy();
    }

    ///////////////////////////////////////
    // SWIPE TO REFRESH FIX
    ///////////////////////////////////////

    int index = 0;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        index = i;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                PageFragment pageFragment = mAdapter.getFragment(mViewPager.getCurrentItem());
                if (pageFragment != null) {
                    if (index == 0) {
                        pageFragment.setSwipeToRefreshEnabled(true);
                    } else {
                        pageFragment.setSwipeToRefreshEnabled(false);
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    ///////////////////////////////////////
    // EVENT HANDLERS
    ///////////////////////////////////////

    @Override
    public void onItemClick(View view) {
        DetailActivity.launch(MainActivity.this, view.findViewById(R.id.image));
    }
}
