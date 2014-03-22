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
import android.os.Parcelable;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.twormobile.mytravelasia.philippines.db.MtaPhProvider;
import com.twormobile.mytravelasia.philippines.feed.PoiCommentsFragment;
import com.twormobile.mytravelasia.philippines.feed.PoiDetailsFragment;
import com.twormobile.mytravelasia.philippines.feed.PoiListFragment;
import com.twormobile.mytravelasia.philippines.feed.PoiMapFragment;
import com.twormobile.mytravelasia.philippines.feed.PoiPhotoActivity;
import com.twormobile.mytravelasia.philippines.http.CreateCommentIntentService;
import com.twormobile.mytravelasia.philippines.http.DeleteCommentIntentService;
import com.twormobile.mytravelasia.philippines.http.EditCommentIntentService;
import com.twormobile.mytravelasia.philippines.http.FeedDetailIntentService;
import com.twormobile.mytravelasia.philippines.http.FeedListIntentService;
import com.twormobile.mytravelasia.philippines.http.LikeIntentService;
import com.twormobile.mytravelasia.philippines.model.CommentEntry;
import com.twormobile.mytravelasia.philippines.model.PoiDetails;
import com.twormobile.mytravelasia.philippines.ui.CarouselPhotoFragment;
import com.twormobile.mytravelasia.philippines.ui.EditCommentDialogFragment;
import com.twormobile.mytravelasia.philippines.util.AppConstants;
import com.twormobile.mytravelasia.philippines.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity that manages the feed and map fragments. If the device has a smallest width of >= 530dp, it will show
 * the feed list fragment beside the map fragment. Otherwise, they will be shown sequentially, with the feed list
 * fragment taking precedence.
 *
 * @author avendael
 */
public class MainActivity extends BaseMtaFragmentActivity
        implements PoiListFragment.Callbacks, PoiDetailsFragment.Callbacks, CarouselPhotoFragment.Callbacks,
                   PoiCommentsFragment.Callbacks, EditCommentDialogFragment.Callbacks {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_FEED_LIST_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiListFragment";
    private static final String TAG_COMMENTS_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiCommentsFragment";
    private static final String TAG_DETAILS_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiDetailsFragment";
    private static final String TAG_MAP_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiMapFragment";
    private static final String TAG_EDIT_COMMENT = "com.twormobile.mytravelasia.philippines.comment.EditCommentDialogFragment";

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
    private BroadcastReceiver mLikeBroadcastReceiver;
    private BroadcastReceiver mCreateCommentBroadcastReceiver;
    private BroadcastReceiver mDeleteCommentBroadcastReceiver;
    private BroadcastReceiver mEditCommentBroadcastReceiver;

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
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        localBroadcastManager.registerReceiver(mFeedListBroadcastReceiver,
                new IntentFilter(FeedListIntentService.BROADCAST_GET_FEED_LIST));
        localBroadcastManager.registerReceiver(mFeedDetailBroadcastReceiver,
                new IntentFilter(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL));
        localBroadcastManager.registerReceiver(mLikeBroadcastReceiver,
                new IntentFilter(LikeIntentService.BROADCAST_LIKE_POI));
        localBroadcastManager.registerReceiver(mCreateCommentBroadcastReceiver,
                new IntentFilter(CreateCommentIntentService.BROADCAST_CREATE_COMMENT));
        localBroadcastManager.registerReceiver(mDeleteCommentBroadcastReceiver,
                new IntentFilter(DeleteCommentIntentService.BROADCAST_DELETE_COMMENT));
        localBroadcastManager.registerReceiver(mEditCommentBroadcastReceiver,
                new IntentFilter(EditCommentIntentService.BROADCAST_EDIT_COMMENT));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        onNextPage(1L);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        localBroadcastManager.unregisterReceiver(mFeedListBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(mFeedDetailBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(mLikeBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(mCreateCommentBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(mDeleteCommentBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(mEditCommentBroadcastReceiver);

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

    @Override
    public void onLikeClicked(long poiId, boolean isLiked) {
        Intent likeIntent = new Intent(MainActivity.this, LikeIntentService.class);

        likeIntent.putExtra(LikeIntentService.EXTRAS_POI_ID, poiId);
        likeIntent.putExtra(LikeIntentService.EXTRAS_PROFILE_ID, mProfileId);
        likeIntent.putExtra(LikeIntentService.EXTRAS_IS_LIKE, isLiked);
        startService(likeIntent);
    }

    @Override
    public void onCommentClicked(long poiId, List<CommentEntry> commentEntries) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        PoiCommentsFragment poiCommentsFragment = new PoiCommentsFragment();
        Bundle args = new Bundle();

        args.putLong(PoiCommentsFragment.ARGS_POI_ID, poiId);
        args.putParcelableArrayList(PoiCommentsFragment.ARGS_POI_COMMENTS,
                (ArrayList<? extends Parcelable>) commentEntries);
        args.putString(PoiCommentsFragment.ARGS_PROFILE_ID, mProfileId);

        poiCommentsFragment.setArguments(args);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fl_list_container, poiCommentsFragment, TAG_COMMENTS_FRAGMENT)
                .commit();
    }

    @Override
    public void onPostClicked(long poiId, String comment) {
        if (null == mProfileId || "".equals(mProfileId)) {
            Toast.makeText(MainActivity.this, R.string.msg_must_login, Toast.LENGTH_LONG).show();

            return;
        }
        Intent createCommentIntent = new Intent(MainActivity.this, CreateCommentIntentService.class);

        createCommentIntent.putExtra(CreateCommentIntentService.EXTRAS_POI_ID, poiId);
        createCommentIntent.putExtra(CreateCommentIntentService.EXTRAS_COMMENT_CONTENT, comment);
        createCommentIntent.putExtra(CreateCommentIntentService.EXTRAS_PROFILE_ID, mProfileId);

        startService(createCommentIntent);
    }

    @Override
    public void onPostEdit(long poiId, long commentId, String content) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        EditCommentDialogFragment editCommentDialogFragment = new EditCommentDialogFragment();
        Bundle args = new Bundle();

        args.putLong(EditCommentDialogFragment.EXTRAS_POI_ID, poiId);
        args.putLong(EditCommentDialogFragment.EXTRAS_COMMENT_ID, commentId);
        args.putString(EditCommentDialogFragment.EXTRAS_COMMENT_CONTENT, content);
        editCommentDialogFragment.setArguments(args);
        editCommentDialogFragment.show(fragmentManager, TAG_EDIT_COMMENT);
    }

    @Override
    public void onPostEdited(long poiId, long commentId, String content) {
        Intent editCommentIntent = new Intent(MainActivity.this, EditCommentIntentService.class);

        editCommentIntent.putExtra(EditCommentIntentService.EXTRAS_POI_ID, poiId);
        editCommentIntent.putExtra(EditCommentIntentService.EXTRAS_COMMENT_ID, commentId);
        editCommentIntent.putExtra(EditCommentIntentService.EXTRAS_PROFILE_ID, mProfileId);
        editCommentIntent.putExtra(EditCommentIntentService.EXTRAS_COMMENT_CONTENT, content);

        startService(editCommentIntent);
    }

    @Override
    public void onPostDelete(long poiId, long commentId) {
        Intent deleteCommentIntent = new Intent(MainActivity.this, DeleteCommentIntentService.class);

        deleteCommentIntent.putExtra(DeleteCommentIntentService.EXTRAS_POI_ID, poiId);
        deleteCommentIntent.putExtra(DeleteCommentIntentService.EXTRAS_COMMENT_ID, commentId);
        deleteCommentIntent.putExtra(DeleteCommentIntentService.EXTRAS_PROFILE_ID, mProfileId);

        startService(deleteCommentIntent);
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
                    PoiCommentsFragment poiCommentsFragment = (PoiCommentsFragment) getSupportFragmentManager()
                            .findFragmentByTag(TAG_COMMENTS_FRAGMENT);
                    PoiDetails poiDetails = intent.getParcelableExtra(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL_SUCCESS);

                    if (null == poiDetails) return;

                    // If the active fragment is the comments fragment, update the comment list.
                    if (null != poiCommentsFragment) {
                        poiCommentsFragment.updateCommentList((ArrayList<CommentEntry>) poiDetails.getCommentEntries());
                    } else { // Otherwise, show the detail fragment
                        Log.d(TAG, "displaying detail fragment");
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment poiDetailsFragment = new PoiDetailsFragment();
                        Bundle args = new Bundle();

                        Log.d(TAG, "poi detail name " + poiDetails);
                        args.putParcelable(PoiDetailsFragment.ARG_POI_DETAILS,
                                poiDetails);
                        poiDetailsFragment.setArguments(args);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.fl_list_container, poiDetailsFragment, TAG_DETAILS_FRAGMENT)
                                .commit();
                    }
                }
            }
        };

        mLikeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(LikeIntentService.BROADCAST_LIKE_FAILED)) {
                    Toast.makeText(MainActivity.this, "Failed to like POI. Please try again.", Toast.LENGTH_LONG)
                            .show();
                } else if (intent.hasExtra(LikeIntentService.BROADCAST_LIKE_SUCCESS)) {
                    PoiDetailsFragment poiDetailsFragment = (PoiDetailsFragment) getSupportFragmentManager()
                            .findFragmentByTag(TAG_DETAILS_FRAGMENT);

                    poiDetailsFragment.likePoi(intent.getBooleanExtra(LikeIntentService.BROADCAST_LIKE_SUCCESS, false));
                }
            }
        };

        mCreateCommentBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(CreateCommentIntentService.BROADCAST_CREATE_COMMENT_FAILED)) {
                    String message = intent.getStringExtra(CreateCommentIntentService.BROADCAST_CREATE_COMMENT_FAILED);

                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                } else if (intent.hasExtra(CreateCommentIntentService.BROADCAST_CREATE_COMMENT_SUCCESS)) {
                    Intent getDetailsIntent = new Intent(MainActivity.this, FeedDetailIntentService.class);

                    getDetailsIntent.putExtra(FeedDetailIntentService.EXTRAS_FEED_ID,
                            intent.getLongExtra(CreateCommentIntentService.BROADCAST_CREATE_COMMENT_SUCCESS, 0));
                    startService(getDetailsIntent);
                }
            }
        };

        mDeleteCommentBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(DeleteCommentIntentService.BROADCAST_DELETE_COMMENT_FAILED)) {
                    String message = intent.getStringExtra(DeleteCommentIntentService.BROADCAST_DELETE_COMMENT_FAILED);

                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                } else if (intent.hasExtra(DeleteCommentIntentService.BROADCAST_DELETE_COMMENT_SUCCESS)) {
                    Intent getDetailsIntent = new Intent(MainActivity.this, FeedDetailIntentService.class);

                    getDetailsIntent.putExtra(FeedDetailIntentService.EXTRAS_FEED_ID,
                            intent.getLongExtra(DeleteCommentIntentService.BROADCAST_DELETE_COMMENT_SUCCESS, 0));
                    startService(getDetailsIntent);
                }
            }
        };

        mEditCommentBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(EditCommentIntentService.BROADCAST_EDIT_COMMENT_FAILED)) {
                    String message = intent.getStringExtra(EditCommentIntentService.BROADCAST_EDIT_COMMENT_FAILED);

                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                } else if (intent.hasExtra(EditCommentIntentService.BROADCAST_EDIT_COMMENT_SUCCESS)) {
                    Intent getDetailsIntent = new Intent(MainActivity.this, FeedDetailIntentService.class);

                    getDetailsIntent.putExtra(FeedDetailIntentService.EXTRAS_FEED_ID,
                            intent.getLongExtra(EditCommentIntentService.BROADCAST_EDIT_COMMENT_SUCCESS, 0));
                    startService(getDetailsIntent);
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