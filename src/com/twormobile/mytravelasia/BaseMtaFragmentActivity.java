package com.twormobile.mytravelasia;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import com.twormobile.mytravelasia.util.AppConstants;

/**
 * @author avendael
 */
public abstract class BaseMtaFragmentActivity extends FragmentActivity {
    /**
     * Set the orientation lock for phones and tablets. Phones will use portrait orientation, while tablets will use
     * landscape orientation.
     */
    protected void setOrientationLock() {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float density = resources.getDisplayMetrics().densityDpi;
        float dpWidth = metrics.widthPixels / (density / 160f);

        if (dpWidth >= AppConstants.TABLET_MIN_SW) setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
