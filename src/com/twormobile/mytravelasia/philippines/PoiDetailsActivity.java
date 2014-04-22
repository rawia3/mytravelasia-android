package com.twormobile.mytravelasia.philippines;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.twormobile.mytravelasia.philippines.feed.PoiDetailsFragment;
import com.twormobile.mytravelasia.philippines.http.FeedDetailIntentService;
import com.twormobile.mytravelasia.philippines.model.CommentEntry;
import com.twormobile.mytravelasia.philippines.util.Log;

import java.util.List;

public class PoiDetailsActivity extends FragmentActivity implements PoiDetailsFragment.Callbacks{

    private static final String TAG = PoiDetailsActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_details_activity);

        FragmentManager fm = getSupportFragmentManager();
        Fragment poiDetailsFragment = new PoiDetailsFragment();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment_poi_details, poiDetailsFragment);
        fragmentTransaction.commit();

        Intent intent = getIntent();
        final long feedId = intent.getLongExtra(FeedDetailIntentService.EXTRAS_FEED_ID, -1);
        initFeed(feedId);
    }

    private void initFeed(long feedId) {
        Log.d(TAG, "feedId: " + feedId);
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
}
