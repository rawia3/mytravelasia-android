package com.twormobile.mytravelasia.philippines;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.twormobile.mytravelasia.philippines.util.AppConstants;
import com.twormobile.mytravelasia.philippines.util.Log;

/**
 * An activity which serves as the startup entry point of the app. It shows an overview about Philippines, and some ads at
 * the bottom.
 *
 * @author avendael
 */
public class StartUpActivity extends BaseMtaActivity {
    private static final String TAG = StartUpActivity.class.getSimpleName();
    private static final String ADS_URL = "http://www.mytravel-asia.com/mobile/adsense";

    private double[] mCoords;
    private WebView mWvAds;
    private UiLifecycleHelper mUiLifecycleHelper;
    private Session.StatusCallback mSessionCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_activity);
        setOrientationLock();

        mWvAds = (WebView) findViewById(R.id.wv_ads);
        mCoords = getIntent().getDoubleArrayExtra(AppConstants.ARG_CURRENT_LOCATION);
        mUiLifecycleHelper = new UiLifecycleHelper(this, mSessionCallback);
        Button enterButton = (Button) findViewById(R.id.btn_enter);

        mUiLifecycleHelper.onCreate(savedInstanceState);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StartUpActivity.this, MainActivity.class);

                mainIntent.putExtra(AppConstants.ARG_CURRENT_LOCATION, mCoords);
                startActivity(mainIntent);
            }
        });

        initAds();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        mUiLifecycleHelper.onResume();
        mWvAds.loadUrl(ADS_URL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUiLifecycleHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUiLifecycleHelper.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUiLifecycleHelper.onSaveInstanceState(outState);
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

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) Log.d(TAG, "facebook logged in");
        else Log.d(TAG, "facebook logged out");
    }
}