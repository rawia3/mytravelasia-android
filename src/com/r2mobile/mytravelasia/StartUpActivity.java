package com.r2mobile.mytravelasia;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * An activity which serves as the startup entry point of the app. It shows an overview about Philippines, and some ads at
 * the bottom.
 *
 * @author avendael
 */
public class StartUpActivity extends Activity {
    private static final String TAG = StartUpActivity.class.getSimpleName();
    private WebView mWvAds;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_activity);

        mWvAds = (WebView) findViewById(R.id.wv_ads);

        initAds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWvAds.loadUrl("http://www.mytravel-asia.com/mobile/adsense");
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