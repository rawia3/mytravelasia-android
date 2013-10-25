package com.twormobile.mytravelasia;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.twormobile.mytravelasia.feed.PoiListFragment;

/**
 * An activity that manages the feed and map fragments. If the device has a smallest width of >= 530dp, it will show
 * the feed list fragment beside the map fragment. Otherwise, they will be shown sequentially, with the feed list
 * fragment taking precedence.
 *
 * @author avendael
 */
public class MainActivity extends FragmentActivity implements PoiListFragment.Callbacks {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_FEED_LIST = "com.twormobile.mytravelasia.feed.PoiListFragment";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_list_container, new PoiListFragment(), TAG_FEED_LIST)
                .commit();
    }

    @Override
    public void onPoiSelected(int position) {
        // TODO
    }
}