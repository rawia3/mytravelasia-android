package com.twormobile.mytravelasia.philippines;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.twormobile.mytravelasia.philippines.db.MtaPhProvider;
import com.twormobile.mytravelasia.philippines.feed.PoiDetailsFragment;
import com.twormobile.mytravelasia.philippines.feed.PoiListFragment;
import com.twormobile.mytravelasia.philippines.feed.PoiMapFragment;
import com.twormobile.mytravelasia.philippines.feed.PoiPhotoActivity;
import com.twormobile.mytravelasia.philippines.http.FeedDetailIntentService;
import com.twormobile.mytravelasia.philippines.http.FeedListIntentService;
import com.twormobile.mytravelasia.philippines.ui.CarouselPhotoFragment;
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
public class MainActivity extends BaseMtaFragmentActivity
        implements PoiListFragment.Callbacks, PoiDetailsFragment.Callbacks, CarouselPhotoFragment.Callbacks {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_FEED_LIST_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiListFragment";
    private static final String TAG_DETAILS_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiDetailsFragment";
    private static final String TAG_MAP_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiMapFragment";

    private boolean mIsDualPane;
    private boolean mIsRefreshing;
    private double[] mCoords;
    private String mProfileId;
    private String[] mNavItems;
    private DrawerLayout mDlContainer;
    private ListView mLvNav;
    private ActionBarDrawerToggle mDrawerToggle;
    private BroadcastReceiver mFeedListBroadcastReceiver;
    private BroadcastReceiver mFeedDetailBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setOrientationLock();
        mIsDualPane = findViewById(R.id.fl_map_container) != null;
        Intent intent = getIntent();
        mCoords = intent.getDoubleArrayExtra(AppConstants.ARG_CURRENT_LOCATION);
        mProfileId = null != savedInstanceState
                ? savedInstanceState.getString(AppConstants.ARG_FB_PROFILE_ID)
                : intent.getStringExtra(AppConstants.ARG_FB_PROFILE_ID);

        Log.d(TAG, "profile id " + mProfileId);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mFeedListBroadcastReceiver,
                new IntentFilter(FeedListIntentService.BROADCAST_GET_FEED_LIST));
        LocalBroadcastManager.getInstance(this).registerReceiver(mFeedDetailBroadcastReceiver,
                new IntentFilter(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        onNextPage(1L);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mFeedListBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mFeedDetailBroadcastReceiver);
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

        if (null != menu) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

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
        Intent getDetailsIntent = new Intent(MainActivity.this, FeedDetailIntentService.class);

        getDetailsIntent.putExtra(FeedDetailIntentService.EXTRAS_FEED_ID, feedId);
        startService(getDetailsIntent);
    }

    @Override
    public void onNextPage(long page) {
        Intent getFeedIntent = new Intent(MainActivity.this, FeedListIntentService.class);
        PoiListFragment poiListFragment = (PoiListFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG_FEED_LIST_FRAGMENT);

        if (poiListFragment != null) poiListFragment.showFooter();

        getFeedIntent.putExtra(FeedListIntentService.EXTRAS_FEED_FETCH_PAGE, page);
        startService(getFeedIntent);
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

    @Override
    public void onPhotoClicked(String url) {
        Intent intent = new Intent(MainActivity.this, PoiPhotoActivity.class);

        intent.putExtra(AppConstants.ARG_PHOTO_URL, url);
        startActivity(intent);
    }

    @Override
    public void onViewMap(double latitude, double longitude, String name) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        PoiMapFragment poiMapFragment = new PoiMapFragment();
        Bundle args = new Bundle();

        args.putDouble(PoiMapFragment.ARGS_LAT, latitude);
        args.putDouble(PoiMapFragment.ARGS_LNG, longitude);
        args.putString(PoiMapFragment.ARGS_NAME, name);

        poiMapFragment.setArguments(args);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fl_list_container, poiMapFragment, TAG_MAP_FRAGMENT)
                .commit();
    }

    private void initBroadcastReceivers() {
        mFeedListBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "received broadcast");

                boolean isFailed = intent.hasExtra(FeedListIntentService.BROADCAST_GET_FEED_LIST_FAILED)
                        || intent.hasExtra(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL_FAILED);

                if (!mIsRefreshing && isFailed) {
                    Toast.makeText(MainActivity.this, "Failed to retrieve feeds", Toast.LENGTH_LONG).show();
                } else if (intent.hasExtra(FeedListIntentService.BROADCAST_GET_FEED_LIST_SUCCESS)) {
                    Log.d(TAG, "successfully retrieved feeds");
                    long[] pageData = intent.getLongArrayExtra(FeedListIntentService.BROADCAST_GET_FEED_LIST_SUCCESS);

                    if (null == pageData) return;

                    long currentPage = pageData[0];
                    long totalPages = pageData[1];
                    PoiListFragment poiListFragment = (PoiListFragment) getSupportFragmentManager()
                            .findFragmentByTag(TAG_FEED_LIST_FRAGMENT);

                    poiListFragment.setCurrentPage(currentPage);
                    poiListFragment.setTotalPages(totalPages);
                    poiListFragment.hideFooter();

                    if (currentPage < totalPages && currentPage < 5) onNextPage(currentPage + 1L);
                }
            }
        };

        mFeedDetailBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL_FAILED)
                        || intent.hasExtra(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL_FAILED)) {
                    Toast.makeText(MainActivity.this, "Failed to retrieve details", Toast.LENGTH_LONG).show();
                } else if (intent.hasExtra(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL_SUCCESS)) {
                    Log.d(TAG, "displaying detail fragment");
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment poiDetailsFragment = new PoiDetailsFragment();
                    Bundle args = new Bundle();

                    Log.d(TAG, "poi detail name " + intent.getParcelableExtra(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL_SUCCESS));
                    args.putParcelable(PoiDetailsFragment.ARG_POI_DETAILS,
                            intent.getParcelableExtra(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL_SUCCESS));
                    poiDetailsFragment.setArguments(args);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.fl_list_container, poiDetailsFragment, TAG_DETAILS_FRAGMENT)
                            .commit();
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