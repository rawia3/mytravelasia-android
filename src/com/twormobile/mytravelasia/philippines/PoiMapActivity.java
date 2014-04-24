package com.twormobile.mytravelasia.philippines;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.twormobile.mytravelasia.philippines.feed.PoiMapFragment;

import static com.twormobile.mytravelasia.philippines.feed.PoiMapFragment.*;

public class PoiMapActivity extends FragmentActivity {

    private static final String TAG = PoiMapActivity.class.getSimpleName();
    private static final String TAG_MAP_FRAGMENT = "com.twormobile.mytravelasia.philippines.feed.PoiMapFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_map_activity);

        Intent intent = getIntent();
        double lat = intent.getDoubleExtra(ARGS_LAT, 0);
        double lon = intent.getDoubleExtra(ARGS_LNG, 0);
        String name = intent.getStringExtra(ARGS_NAME);
        Bundle args = new Bundle();

        args.putDouble(PoiMapFragment.ARGS_LAT, lat);
        args.putDouble(PoiMapFragment.ARGS_LNG, lon);
        args.putString(PoiMapFragment.ARGS_NAME, name);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        PoiMapFragment poiMapFragment = new PoiMapFragment();

        poiMapFragment.setArguments(args);
        fragmentTransaction.replace(R.id.map, poiMapFragment, TAG_MAP_FRAGMENT)
                .commit();

    }

}
