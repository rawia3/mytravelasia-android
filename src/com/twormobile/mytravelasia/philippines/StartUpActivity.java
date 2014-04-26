package com.twormobile.mytravelasia.philippines;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.twormobile.mytravelasia.philippines.http.RegisterIntentService;
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
    private String mProfileId;
    private WebView mWvAds;
    private UiLifecycleHelper mUiLifecycleHelper;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
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

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

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

        if (null != savedInstanceState) {
            mProfileId = savedInstanceState.getString(AppConstants.ARG_FB_PROFILE_ID);
        }

        initBroadcastReceivers();
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(RegisterIntentService.BROADCAST_REGISTER_MTA));

        mWvAds.loadUrl(ADS_URL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUiLifecycleHelper.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
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
        outState.putString(AppConstants.ARG_FB_PROFILE_ID, mProfileId);
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

    private void initBroadcastReceivers() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "received broadcast");

                boolean isFailed = intent.hasExtra(RegisterIntentService.BROADCAST_REGISTER_FAILED);
                Toast loginFailMessage = Toast.makeText(
                        StartUpActivity.this, R.string.msg_register_failed, Toast.LENGTH_LONG);

                if (isFailed) {
                    loginFailMessage.show();
                } else if (intent.hasExtra(RegisterIntentService.BROADCAST_REGISTER_SUCCESS)) {
                    String profileId = intent.getStringExtra(RegisterIntentService.BROADCAST_REGISTER_SUCCESS);

                    if (null == profileId) {
                        loginFailMessage.show();

                        return;
                    }

                    mProfileId = profileId;
                    Log.d(TAG, "successfully registered");
                    Log.d(TAG, "profile id " + profileId);

                    Intent mainIntent = new Intent(StartUpActivity.this, MainActivity.class);

                    mainIntent.putExtra(AppConstants.ARG_CURRENT_LOCATION, mCoords);
                    mainIntent.putExtra(AppConstants.ARG_FB_PROFILE_ID, profileId);
                    startActivity(mainIntent);
                }
            }
        };
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.d(TAG, "facebook logged in");
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (null != mProfileId) {
                        Log.d(TAG, "current profile id " + mProfileId);
                        return;
                    }

                    String profileId = user.getId();
                    String firstName = user.getFirstName();
                    String lastName = user.getLastName();
                    String accessToken = session.getAccessToken();
                    Intent registerIntent = new Intent(StartUpActivity.this, RegisterIntentService.class);

                    Log.d(TAG, "graph user first name " + firstName);
                    Log.d(TAG, "graph user last name " + lastName);
                    Log.d(TAG, "graph user profile id " + profileId);
                    Log.d(TAG, "graph user session " + accessToken);

                    registerIntent.putExtra(RegisterIntentService.EXTRAS_PROFILE_ID, profileId);
                    registerIntent.putExtra(RegisterIntentService.EXTRAS_FIRST_NAME, firstName);
                    registerIntent.putExtra(RegisterIntentService.EXTRAS_LAST_NAME, lastName);
                    registerIntent.putExtra(RegisterIntentService.EXTRAS_TOKEN, accessToken);

                    Log.d(TAG, "attempt to call intent service");
                    startService(registerIntent);
                }
            }).executeAsync();
        } else {
            Log.d(TAG, "facebook logged out");

            if(exception != null){
                Log.d(TAG, "facebook exception: " + exception.getMessage());
                exception.printStackTrace();
            }

            mProfileId = null;
        }
    }
}