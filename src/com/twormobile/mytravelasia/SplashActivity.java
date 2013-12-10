package com.twormobile.mytravelasia;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.twormobile.mytravelasia.util.AppConstants;
import com.twormobile.mytravelasia.util.Log;

/**
 * An activity which displays a splash screen.
 */
public class SplashActivity extends BaseMtaActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int MAX_DURATION = 1000;

    private boolean isActive = true;
    private boolean isExit = false;
    private Thread mTimerThread;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        setOrientationLock();

        if (AppConstants.DEBUG) showScreenMetrics();

        mTimerThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (isActive && (waited < MAX_DURATION)) {
                        sleep(10);
                        if (isActive) {
                            waited += 10;
                        }
                    }
                } catch (InterruptedException e) {
                    finish();
                } finally {
                    if (!isExit) {
                        Intent startUpIntent = new Intent(SplashActivity.this, StartUpActivity.class);

                        startActivity(startUpIntent);
                    }
                    finish();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        switch (result) {
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
                GooglePlayServicesUtil.getErrorDialog(result, this, 0);
            default:
                mTimerThread.start();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isActive = false;
            return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * This will simply print out the screen's density, dpHeight, dpWidth, etc. Useful for testing with different
     * devices.
     */
    private void showScreenMetrics() {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float density = resources.getDisplayMetrics().densityDpi;
        float dpHeight = metrics.heightPixels / (density / 160f);
        float dpWidth = metrics.widthPixels / (density / 160f);

        Log.d(TAG, "::showScreenMetrics() -- density " + density);
        Log.d(TAG, "::showScreenMetrics() -- dpHeight " + dpHeight);
        Log.d(TAG, "::showScreenMetrics() -- dpWidth " + dpWidth);
        Log.d(TAG, "::showScreenMetrics() -- xPpi " + metrics.xdpi);
        Log.d(TAG, "::showScreenMetrics() -- yPpi " + metrics.ydpi);
    }
}
