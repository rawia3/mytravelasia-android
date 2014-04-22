package com.twormobile.mytravelasia.philippines;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
import com.twormobile.mytravelasia.philippines.feed.PoiDetailsFragment;
import com.twormobile.mytravelasia.philippines.feed.PoiPhotoActivity;
import com.twormobile.mytravelasia.philippines.http.FeedDetailIntentService;
import com.twormobile.mytravelasia.philippines.model.CommentEntry;
import com.twormobile.mytravelasia.philippines.model.PoiDetails;
import com.twormobile.mytravelasia.philippines.ui.CarouselPhotoFragment;
import com.twormobile.mytravelasia.philippines.util.AppConstants;
import com.twormobile.mytravelasia.philippines.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PoiDetailsActivity extends FragmentActivity implements PoiDetailsFragment.Callbacks, CarouselPhotoFragment.Callbacks{

    private static final String TAG = PoiDetailsActivity.class.getSimpleName();

    private static final String TAG_DETAILS_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiDetailsFragment";

    private BroadcastReceiver mFeedDetailBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_details_activity);

//        FragmentManager fm = getSupportFragmentManager();
//        Fragment poiDetailsFragment = new PoiDetailsFragment();
//        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//        fragmentTransaction.add(R.id.fragment_poi_details, poiDetailsFragment);
//        fragmentTransaction.commit();

        Intent intent = getIntent();
        final long feedId = intent.getLongExtra(FeedDetailIntentService.EXTRAS_FEED_ID, -1);
        initFeed(feedId);

        initBroadcastReceivers();
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

                    // If the active fragment is the comments fragment, update the comment list.
                    Log.d(TAG, "displaying detail fragment");
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment poiDetailsFragment = new PoiDetailsFragment();
                    Bundle args = new Bundle();

                    Log.d(TAG, "poi detail name " + poiDetails);
                    args.putParcelable(PoiDetailsFragment.ARG_POI_DETAILS, poiDetails);
                    poiDetailsFragment.setArguments(args);
//                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.fragment_poi_details, poiDetailsFragment, TAG_DETAILS_FRAGMENT)
                            .commit();

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

    }

    @Override
    public void onLikeClicked(long poiId, boolean isLiked) {

    }

    @Override
    public void onCommentClicked(long poiId, List<CommentEntry> commentEntries) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        localBroadcastManager.registerReceiver(mFeedDetailBroadcastReceiver,
                new IntentFilter(FeedDetailIntentService.BROADCAST_GET_FEED_DETAIL));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        localBroadcastManager.unregisterReceiver(mFeedDetailBroadcastReceiver);

        super.onPause();
    }


    @Override
    public void onPhotoClicked(String url) {
        Intent intent = new Intent(PoiDetailsActivity.this, PoiPhotoActivity.class);

        intent.putExtra(AppConstants.ARG_PHOTO_URL, url);
        startActivity(intent);
    }
}
