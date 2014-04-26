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
import com.twormobile.mytravelasia.philippines.http.*;
import com.twormobile.mytravelasia.philippines.model.CommentEntry;
import com.twormobile.mytravelasia.philippines.model.PoiDetails;
import com.twormobile.mytravelasia.philippines.ui.CarouselPhotoFragment;
import com.twormobile.mytravelasia.philippines.ui.EditCommentDialogFragment;
import com.twormobile.mytravelasia.philippines.util.AppConstants;
import com.twormobile.mytravelasia.philippines.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PoiDetailsActivity extends BaseMtaFragmentActivity
        implements PoiDetailsFragment.Callbacks,
        CarouselPhotoFragment.Callbacks,
        PoiCommentsFragment.Callbacks,
        EditCommentDialogFragment.Callbacks {

    private static final String TAG = PoiDetailsActivity.class.getSimpleName();

    private static final String TAG_DETAILS_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiDetailsFragment";
    private static final String TAG_COMMENTS_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiCommentsFragment";
    private static final String TAG_EDIT_COMMENT = "com.twormobile.mytravelasia.philippines.comment.EditCommentDialogFragment";

    private String mProfileId;

    private BroadcastReceiver mFeedDetailBroadcastReceiver;
    private BroadcastReceiver mLikeBroadcastReceiver;
    private BroadcastReceiver mCreateCommentBroadcastReceiver;
    private BroadcastReceiver mDeleteCommentBroadcastReceiver;
    private BroadcastReceiver mEditCommentBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOrientationLock();
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

        mCreateCommentBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(CreateCommentIntentService.BROADCAST_CREATE_COMMENT_FAILED)) {
                    String message = intent.getStringExtra(CreateCommentIntentService.BROADCAST_CREATE_COMMENT_FAILED);

                    Toast.makeText(PoiDetailsActivity.this, message, Toast.LENGTH_LONG).show();
                } else if (intent.hasExtra(CreateCommentIntentService.BROADCAST_CREATE_COMMENT_SUCCESS)) {
                    Intent getDetailsIntent = new Intent(PoiDetailsActivity.this, FeedDetailIntentService.class);

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

                    Toast.makeText(PoiDetailsActivity.this, message, Toast.LENGTH_LONG).show();
                } else if (intent.hasExtra(DeleteCommentIntentService.BROADCAST_DELETE_COMMENT_SUCCESS)) {
                    Intent getDetailsIntent = new Intent(PoiDetailsActivity.this, FeedDetailIntentService.class);

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

                    Toast.makeText(PoiDetailsActivity.this, message, Toast.LENGTH_LONG).show();
                } else if (intent.hasExtra(EditCommentIntentService.BROADCAST_EDIT_COMMENT_SUCCESS)) {
                    Intent getDetailsIntent = new Intent(PoiDetailsActivity.this, FeedDetailIntentService.class);

                    getDetailsIntent.putExtra(FeedDetailIntentService.EXTRAS_FEED_ID,
                            intent.getLongExtra(EditCommentIntentService.BROADCAST_EDIT_COMMENT_SUCCESS, 0));
                    startService(getDetailsIntent);
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
        Intent mapIntent = new Intent(this, PoiMapActivity.class);
        mapIntent.putExtra(PoiMapFragment.ARGS_LAT, latitude);
        mapIntent.putExtra(PoiMapFragment.ARGS_LNG, longitude);
        mapIntent.putExtra(PoiMapFragment.ARGS_NAME, name);
        startActivity(mapIntent);
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        PoiCommentsFragment poiCommentsFragment = new PoiCommentsFragment();
        Bundle args = new Bundle();

        args.putLong(PoiCommentsFragment.ARGS_POI_ID, poiId);
        args.putParcelableArrayList(PoiCommentsFragment.ARGS_POI_COMMENTS,
                (ArrayList<? extends Parcelable>) commentEntries);
        args.putString(PoiCommentsFragment.ARGS_PROFILE_ID, mProfileId);

        poiCommentsFragment.setArguments(args);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_poi_details, poiCommentsFragment, TAG_COMMENTS_FRAGMENT);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

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
    protected void onPause() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        localBroadcastManager.unregisterReceiver(mFeedDetailBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(mLikeBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(mCreateCommentBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(mDeleteCommentBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(mEditCommentBroadcastReceiver);

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
        if (null == mProfileId || "".equals(mProfileId)) {
            Toast.makeText(PoiDetailsActivity.this, R.string.msg_must_login, Toast.LENGTH_LONG).show();

            return;
        }
        Intent createCommentIntent = new Intent(PoiDetailsActivity.this, CreateCommentIntentService.class);

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
        Intent editCommentIntent = new Intent(PoiDetailsActivity.this, EditCommentIntentService.class);

        editCommentIntent.putExtra(EditCommentIntentService.EXTRAS_POI_ID, poiId);
        editCommentIntent.putExtra(EditCommentIntentService.EXTRAS_COMMENT_ID, commentId);
        editCommentIntent.putExtra(EditCommentIntentService.EXTRAS_PROFILE_ID, mProfileId);
        editCommentIntent.putExtra(EditCommentIntentService.EXTRAS_COMMENT_CONTENT, content);

        startService(editCommentIntent);
    }

    @Override
    public void onPostDelete(long poiId, long commentId) {
        Intent deleteCommentIntent = new Intent(PoiDetailsActivity.this, DeleteCommentIntentService.class);

        deleteCommentIntent.putExtra(DeleteCommentIntentService.EXTRAS_POI_ID, poiId);
        deleteCommentIntent.putExtra(DeleteCommentIntentService.EXTRAS_COMMENT_ID, commentId);
        deleteCommentIntent.putExtra(DeleteCommentIntentService.EXTRAS_PROFILE_ID, mProfileId);

        startService(deleteCommentIntent);
    }


}
