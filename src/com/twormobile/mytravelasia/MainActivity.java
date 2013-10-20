package com.twormobile.mytravelasia;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * An activity that manages the feed and map fragments. If the device has a smallest width of >= 530dp, it will show
 * the feed list fragment beside the map fragment. Otherwise, they will be shown sequentially, with the feed list
 * fragment taking precedence.
 *
 * @author avendael
 */
public class MainActivity extends FragmentActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}