package com.twormobile.mytravelasia.philippines;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
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
import android.widget.Toast;
import com.twormobile.mytravelasia.philippines.db.MtaPhProvider;
import com.twormobile.mytravelasia.philippines.feed.PoiListFragment;
import com.twormobile.mytravelasia.philippines.http.FeedDetailIntentService;
import com.twormobile.mytravelasia.philippines.http.FeedListIntentService;
import com.twormobile.mytravelasia.philippines.util.AppConstants;
import com.twormobile.mytravelasia.philippines.util.Log;

import java.io.File;

/**
 * An activity that manages the feed and map fragments. If the device has a smallest width of >= 530dp, it will show
 * the feed list fragment beside the map fragment. Otherwise, they will be shown sequentially, with the feed list
 * fragment taking precedence.
 *
 * @author avendael
 */
public class MainActivity extends BaseMtaFragmentActivity implements PoiListFragment.Callbacks {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_FEED_LIST_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiListFragment";

    private boolean mIsDualPane;
    private boolean mIsRefreshing;
    private int mFeedType = FeedListIntentService.FEED_TYPE_NEARBY;
    private double[] mCoords;
    private String mProfileId;
    private String[] mNavItems;
    private DrawerLayout mDlContainer;
    private ListView mLvNav;
    private ActionBarDrawerToggle mDrawerToggle;
    private BroadcastReceiver mFeedListBroadcastReceiver;

    private boolean mInitialLoad = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setOrientationLock();
//        mIsDualPane = findViewById(R.id.fl_map_container) != null;
        mIsDualPane = false;
        Intent intent = getIntent();
        mCoords = intent.getDoubleArrayExtra(AppConstants.ARG_CURRENT_LOCATION);
        mProfileId = null != savedInstanceState
                ? savedInstanceState.getString(AppConstants.ARG_FB_PROFILE_ID)
                : intent.getStringExtra(AppConstants.ARG_FB_PROFILE_ID);

        Log.d(TAG, "current profile id " + mProfileId);

        initSideNav();
        initBroadcastReceivers();

        ActionBar actionBar = getActionBar();

        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_list_container, new PoiListFragment(), TAG_FEED_LIST_FRAGMENT)
                .commit();

        setTitle("Near Me");
        resetInitialFeed(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        localBroadcastManager.registerReceiver(mFeedListBroadcastReceiver,
                new IntentFilter(FeedListIntentService.BROADCAST_GET_FEED_LIST));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void resetInitialFeed(boolean reset) {
        if(reset){
            mInitialLoad = true;
        }

        if(mInitialLoad) {
            ContentResolver contentResolver = getContentResolver();
            contentResolver.delete(MtaPhProvider.POI_URI, null, null);

            PoiListFragment poiListFragment = (PoiListFragment) getSupportFragmentManager()
                    .findFragmentByTag(TAG_FEED_LIST_FRAGMENT);
            if (poiListFragment != null) poiListFragment.setCurrentPage(0);

            onNextPage(1L);
            mInitialLoad = false;
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        localBroadcastManager.unregisterReceiver(mFeedListBroadcastReceiver);

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

//        if (null != menu) {
//            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AppConstants.ARG_FB_PROFILE_ID, mProfileId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onPoiSelected(long feedId) {
        Intent detailsIntent = new Intent(this, PoiDetailsActivity.class);
        detailsIntent.putExtra(FeedDetailIntentService.EXTRAS_FEED_ID, feedId);
        detailsIntent.putExtra(AppConstants.ARG_FB_PROFILE_ID, mProfileId);
        startActivity(detailsIntent);
    }

    @Override
    public void onNextPage(long page) {
        // show footer when loading next page
        PoiListFragment poiListFragment = (PoiListFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG_FEED_LIST_FRAGMENT);
        if (poiListFragment != null) poiListFragment.showFooter();

        Intent feedIntent = new Intent(MainActivity.this, FeedListIntentService.class);
        feedIntent.putExtra(FeedListIntentService.EXTRAS_FEED_FETCH_PAGE, page);
        feedIntent.putExtra(FeedListIntentService.EXTRAS_FEED_TYPE, mFeedType);
        feedIntent.putExtra(FeedListIntentService.EXTRAS_COORDS, mCoords);
        startService(feedIntent);
    }

    @Override
    public void onPullToRefresh() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                getContentResolver().delete(MtaPhProvider.POI_URI, null, null);
//                clearApplicationData();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mIsRefreshing = true;
                onNextPage(0);
            }
        }.execute();
    }

    private void initBroadcastReceivers() {
        mFeedListBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "received broadcast");

                boolean isFailed = intent.hasExtra(FeedListIntentService.BROADCAST_GET_FEED_LIST_FAILED);

                if (!mIsRefreshing && isFailed) {
                    Log.d(TAG, "FEED: failed retrieved feeds");
                    Toast.makeText(MainActivity.this, "Failed to retrieve feeds", Toast.LENGTH_LONG).show();
                } else if (intent.hasExtra(FeedListIntentService.BROADCAST_GET_FEED_LIST_SUCCESS)) {
                    Log.d(TAG, "FEED: successfully retrieved feeds");
                    long[] pageData = intent.getLongArrayExtra(FeedListIntentService.BROADCAST_GET_FEED_LIST_SUCCESS);

                    if (null == pageData) return;

                    long currentPage = pageData[0];
                    long totalPages = pageData[1];
                    PoiListFragment poiListFragment = (PoiListFragment) getSupportFragmentManager()
                            .findFragmentByTag(TAG_FEED_LIST_FRAGMENT);

                    poiListFragment.setCurrentPage(currentPage);
                    poiListFragment.setTotalPages(totalPages);
                    poiListFragment.hideFooter();

//                    if (currentPage < totalPages && currentPage < 5) onNextPage(currentPage + 1L);
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
                    case 1: // Nearby
                        mFeedType = FeedListIntentService.FEED_TYPE_NEARBY;

                        resetInitialFeed(true);
                        setTitle("Near Me");
                        mDlContainer.closeDrawers();

                        break;
                    case 2: // News
                        mFeedType = FeedListIntentService.FEED_TYPE_NEWS;

                        resetInitialFeed(true);
                        setTitle("News Feed");
                        mDlContainer.closeDrawers();

                        break;
                    case 3: // Featured
                        mFeedType = FeedListIntentService.FEED_TYPE_FEATURED;

                        resetInitialFeed(true);
                        setTitle("Featured");
                        mDlContainer.closeDrawers();

                        break;

                    case 4: // Most Viewed
                        mFeedType = FeedListIntentService.FEED_TYPE_MOST_VIEWED;

                        resetInitialFeed(true);
                        setTitle("Most Viewed");
                        mDlContainer.closeDrawers();

                        break;
                    case 5: // Recent
                        mFeedType = FeedListIntentService.FEED_TYPE_RECENT;

                        resetInitialFeed(true);
                        setTitle("Recent");
                        mDlContainer.closeDrawers();

                        break;
                }
            }
        });

        mDlContainer.setDrawerListener(mDrawerToggle);
        mDlContainer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    }

    private void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache != null ? cache.getParent() : null);

        if (appDir.exists()) {
            String[] children = appDir.list();

            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null) {
            if (dir.isDirectory()) {
                String[] children = dir.list();

                for (String aChildren : children) {
                    boolean success = deleteDir(new File(dir, aChildren));

                    if (!success) return false;
                }
            }

            return dir.delete();
        }

        return false;
    }
}