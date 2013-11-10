package com.twormobile.mytravelasia;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.twormobile.mytravelasia.feed.PoiListFragment;
import com.twormobile.mytravelasia.http.FeedIntentService;
import com.twormobile.mytravelasia.util.Log;

/**
 * An activity that manages the feed and map fragments. If the device has a smallest width of >= 530dp, it will show
 * the feed list fragment beside the map fragment. Otherwise, they will be shown sequentially, with the feed list
 * fragment taking precedence.
 *
 * @author avendael
 */
public class MainActivity extends BaseMtaFragmentActivity implements PoiListFragment.Callbacks {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_FEED_LIST = "com.twormobile.mytravelasia.feed.PoiListFragment";

    private boolean mIsDualPane;
    private String[] mNavItems;
    private DrawerLayout mDlContainer;
    private ListView mLvNav;
    private ActionBarDrawerToggle mDrawerToggle;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setOrientationLock();
        mIsDualPane = findViewById(R.id.fl_map_container) != null;

        initBroadcastReceiver();
        initSideNav();

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_list_container, new PoiListFragment(), TAG_FEED_LIST)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(FeedIntentService.BROADCAST_GET_FEED));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO: handle search queries
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_menu_options, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public void onPoiSelected(int position) {
        // TODO
    }

    private void initBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(FeedIntentService.BROADCAST_GET_FEED_FAILED)) {
                    Toast.makeText(MainActivity.this, "Failed to retrieve feeds", Toast.LENGTH_LONG).show();
                } else if (intent.hasExtra(FeedIntentService.BROADCAST_GET_FEED_SUCCESS)) {
                    Log.d(TAG, "successfully retrieved feeds");
                    int[] pageData = intent.getIntArrayExtra(FeedIntentService.BROADCAST_GET_FEED_SUCCESS);
                    PoiListFragment poiListFragment = (PoiListFragment) getSupportFragmentManager()
                            .findFragmentByTag(TAG_FEED_LIST);

                    poiListFragment.setCurrentPage(pageData[0]);
                    poiListFragment.setTotalPages(pageData[1]);
                }
            }
        };
    }

    private void initSideNav() {
        mNavItems = getResources().getStringArray(R.array.nav_items);
        mDlContainer = (DrawerLayout) findViewById(R.id.dl_main);
        mLvNav = (ListView) findViewById(R.id.lv_sidenav);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDlContainer, R.drawable.ic_drawer, R.string.lbl_mytravel_ph, R.string.app_name);

        mLvNav.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mNavItems));
        mLvNav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Home
                        Intent homeIntent = new Intent(MainActivity.this, StartUpActivity.class);

                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                        finish();
                        break;
                }
            }
        });
        mDlContainer.setDrawerListener(mDrawerToggle);
        mDlContainer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    }
}