package com.twormobile.mytravelasia.philippines;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
import com.twormobile.mytravelasia.philippines.feed.PoiCommentsFragment;
import com.twormobile.mytravelasia.philippines.feed.PoiDetailsFragment;
import com.twormobile.mytravelasia.philippines.feed.PoiMapFragment;
import com.twormobile.mytravelasia.philippines.feed.PoiPhotoActivity;
import com.twormobile.mytravelasia.philippines.http.FeedDetailIntentService;
import com.twormobile.mytravelasia.philippines.http.LikeIntentService;
import com.twormobile.mytravelasia.philippines.model.CommentEntry;
import com.twormobile.mytravelasia.philippines.model.PoiDetails;
import com.twormobile.mytravelasia.philippines.ui.CarouselPhotoFragment;
import com.twormobile.mytravelasia.philippines.ui.EditCommentDialogFragment;
import com.twormobile.mytravelasia.philippines.util.AppConstants;
import com.twormobile.mytravelasia.philippines.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PoiDetailsActivity extends FragmentActivity
        implements PoiDetailsFragment.Callbacks,
        CarouselPhotoFragment.Callbacks,
        PoiCommentsFragment.Callbacks,
        EditCommentDialogFragment.Callbacks {

    private static final String TAG = PoiDetailsActivity.class.getSimpleName();

    private static final String TAG_DETAILS_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiDetailsFragment";
    private static final String TAG_MAP_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiMapFragment";
    private static final String TAG_COMMENTS_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiCommentsFragment";

    private String mProfileId;

    private BroadcastReceiver mFeedDetailBroadcastReceiver;
    private BroadcastReceiver mLikeBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_details_activity);

        Intent intent = getIntent();
        final long feedId = intent.getLongExtra(FeedDetailIntentService.EXTRAS_FEED_ID, -1);
        initFeed(feedId);

        initProfileId(savedInstanceState, intent);

        initBroadcastReceivers();
    }

    private void initProfileId(Bundle savedInstanceState, Intent intent) {
        mProfileId = null != savedInstanceState
                ? savedInstanceState.getString(AppConstants.ARG_FB_PROFILE_ID)
                : intent.getStringExtra(AppConstants.ARG_FB_PROFILE_ID);

        Log.d(TAG, "profile id " + mProfileId);
    }

    private void initBroadcastReceivers() {

        mFeedDetailBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL_FAILED)
                        || intent.hasExtra(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL_FAILED)) {

                    Toast.makeText(PoiDetailsActivity.this, "Failed to retrieve details", Toast.LENGTH_LONG).show();

                } else if (intent.hasExtra(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL_SUCCESS)) {

                    PoiDetails poiDetails = intent.getParcelableExtra(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL_SUCCESS);

                    if (null == poiDetails) return;

                    Log.d(TAG, "displaying detail fragment");
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment poiDetailsFragment = new PoiDetailsFragment();
                    Bundle args = new Bundle();

                    Log.d(TAG, "poi detail name " + poiDetails);
                    args.putParcelable(PoiDetailsFragment.ARG_POI_DETAILS, poiDetails);
                    poiDetailsFragment.setArguments(args);
                    fragmentTransaction.replace(R.id.fragment_poi_details, poiDetailsFragment, TAG_DETAILS_FRAGMENT);

                    PoiCommentsFragment poiCommentsFragment = new PoiCommentsFragment();

                    args.putLong(PoiCommentsFragment.ARGS_POI_ID, poiDetails.getResourceId());
                    args.putParcelableArrayList(PoiCommentsFragment.ARGS_POI_COMMENTS,
                            (ArrayList<? extends Parcelable>) poiDetails.getCommentEntries());
                    args.putString(PoiCommentsFragment.ARGS_PROFILE_ID, mProfileId);

                    poiCommentsFragment.setArguments(args);
                    fragmentTransaction.replace(R.id.fragment_poi_comments, poiCommentsFragment, TAG_COMMENTS_FRAGMENT);
                    fragmentTransaction.commit();
                }
            }
        };

        mLikeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(LikeIntentService.BROADCAST_LIKE_FAILED)) {
                    Toast.makeText(PoiDetailsActivity.this, "Failed to like POI. Please try again.", Toast.LENGTH_LONG)
                            .show();
                } else if (intent.hasExtra(LikeIntentService.BROADCAST_LIKE_SUCCESS)) {
                    PoiDetailsFragment poiDetailsFragment = (PoiDetailsFragment) getSupportFragmentManager()
                            .findFragmentByTag(TAG_DETAILS_FRAGMENT);

                    poiDetailsFragment.likePoi(intent.getBooleanExtra(LikeIntentService.BROADCAST_LIKE_SUCCESS, false));
                }
            }
        };



    }

    private void initFeed(long feedId) {
        Log.d(TAG, "feedId: " + feedId);
        Intent getDetailsIntent = new Intent(PoiDetailsActivity.this, FeedDetailIntentService.class);
        getDetailsIntent.putExtra(FeedDetailIntentService.EXTRAS_FEED_ID, feedId);
        startService(getDetailsIntent);
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
        fragmentTransaction.replace(R.id.fragment_poi_details, poiMapFragment, TAG_MAP_FRAGMENT)
                .commit();
    }

    @Override
    public void onLikeClicked(long poiId, boolean isLiked) {
        Intent likeIntent = new Intent(PoiDetailsActivity.this, LikeIntentService.class);

        likeIntent.putExtra(LikeIntentService.EXTRAS_POI_ID, poiId);
        likeIntent.putExtra(LikeIntentService.EXTRAS_PROFILE_ID, mProfileId);
        likeIntent.putExtra(LikeIntentService.EXTRAS_IS_LIKE, isLiked);
        startService(likeIntent);
    }

    @Override
    public void onCommentClicked(long poiId, List<CommentEntry> commentEntries) {
        //TODO: Scroll to comments in listview
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        localBroadcastManager.registerReceiver(mFeedDetailBroadcastReceiver,
                new IntentFilter(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL));
        localBroadcastManager.registerReceiver(mLikeBroadcastReceiver,
                new IntentFilter(LikeIntentService.BROADCAST_LIKE_POI));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        localBroadcastManager.unregisterReceiver(mFeedDetailBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(mLikeBroadcastReceiver);

        super.onPause();
    }


    @Override
    public void onPhotoClicked(String url) {
        Intent intent = new Intent(PoiDetailsActivity.this, PoiPhotoActivity.class);

        intent.putExtra(AppConstants.ARG_PHOTO_URL, url);
        startActivity(intent);
    }

    @Override
    public void onPostClicked(long poiId, String comment) {

    }

    @Override
    public void onPostEdit(long poiId, long commentId, String content) {

    }

    @Override
    public void onPostDelete(long poiId, long commentId) {

    }

    @Override
    public void onPostEdited(long poiId, long commentId, String content) {

    }
}
