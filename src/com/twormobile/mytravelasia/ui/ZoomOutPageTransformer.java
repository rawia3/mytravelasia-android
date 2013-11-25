package com.twormobile.mytravelasia.ui;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ViewPager zoom out animation.
 *
 * @author avendael
 */
public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View view, float position) {
        float minScale = 0.85f;
        float minAlpha = 0.5f;
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) {
            view.setAlpha(0);
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(minScale, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;

            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between minScale and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(minAlpha
                    + (scaleFactor - minScale)
                    / (1 - minScale)
                    * (1 - minAlpha));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }
}
