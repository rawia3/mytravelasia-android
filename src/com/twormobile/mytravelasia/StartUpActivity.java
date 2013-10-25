package com.twormobile.mytravelasia;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.twormobile.mytravelasia.db.MtaPhProvider;
import com.twormobile.mytravelasia.http.FeedHttpClient;
import com.twormobile.mytravelasia.http.FeedResponse;
import com.twormobile.mytravelasia.model.Poi;
import com.twormobile.mytravelasia.util.Log;

/**
 * An activity which serves as the startup entry point of the app. It shows an overview about Philippines, and some ads at
 * the bottom.
 *
 * @author avendael
 */
public class StartUpActivity extends Activity {
    private static final String TAG = StartUpActivity.class.getSimpleName();
    private static final String ADS_URL = "http://www.mytravel-asia.com/mobile/adsense";

    private WebView mWvAds;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_activity);

        mWvAds = (WebView) findViewById(R.id.wv_ads);
        Button enterButton = (Button) findViewById(R.id.btn_enter);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StartUpActivity.this, MainActivity.class);

                startActivity(mainIntent);
            }
        });

        initAds();
        getContentResolver().delete(MtaPhProvider.POI_URI, null, null);

        FeedHttpClient.getFeeds(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String content) {
                Log.d(TAG, "response is " + content);
                // TODO: Move the getFeed call to a Service or IntentService
                FeedResponse feedResponse = FeedHttpClient.getFeedGsonParser().fromJson(content, FeedResponse.class);

                for (Poi poi : feedResponse.getFeeds()) {
                    Log.d(TAG, "poi name: " + poi.getName());
                    ContentValues values = new ContentValues();

                    values.put(Poi.RESOURCE_ID, poi.getResourceId());
                    values.put(Poi.NAME, poi.getName());
                    values.put(Poi.ADDRESS, poi.getAddress());
                    values.put(Poi.CONTENT, poi.getContent());
                    values.put(Poi.FB_USER_PROFILE_ID, poi.getFbUserProfileId());
                    values.put(Poi.FB_USER_PROFILE_NAME, poi.getFbUserProfileName());
                    values.put(Poi.CREATED_AT, poi.getCreatedAt());
                    values.put(Poi.LONGITUDE, poi.getLongitude());
                    values.put(Poi.LATITUDE, poi.getLatitude());
                    values.put(Poi.FEED_TYPE, poi.getFeedType());
                    values.put(Poi.POI_TYPE, poi.getPoiType());
                    values.put(Poi.IMAGE_THUMB_URL, poi.getImageThumbUrl());
                    values.put(Poi.ANNOTATION_TYPE, poi.getAnnotationType());
                    values.put(Poi.TOTAL_COMMENTS, poi.getTotalComments());
                    values.put(Poi.TOTAL_LIKES, poi.getTotalLikes());

                    getContentResolver().insert(MtaPhProvider.POI_URI, values);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWvAds.loadUrl(ADS_URL);
    }

    private void initAds() {
        WebSettings settings = mWvAds.getSettings();
        mWvAds.setSaveEnabled(true);
        mWvAds.setBackgroundColor(Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWvAds.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }

        settings.setLoadsImagesAutomatically(true);
        settings.setBlockNetworkLoads(false);
        settings.setBlockNetworkImage(false);
        settings.setJavaScriptEnabled(true);
    }
}