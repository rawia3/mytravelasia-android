package com.twormobile.mytravelasia.philippines.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twormobile.mytravelasia.philippines.util.Log;

/**
 * A fragment which displays a POI's location in a map.
 *
 * @author avendael
 */
public class PoiMapFragment extends SupportMapFragment {
    private static final String TAG = PoiMapFragment.class.getSimpleName();
    private static final int CAMERA_ZOOM = 15;
    private static final int CAMERA_ZOOM_DURATION_MS = 10;

    public static final String ARGS_LAT = "com.twormobile.mytravelasia.lat";
    public static final String ARGS_LNG = "com.twormobile.mytravelasia.lng";
    public static final String ARGS_NAME = "com.twormobile.mytravelasia.name";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Bundle args = getArguments();

        if (null == args) {
            args = savedInstanceState;
        }

        double lat = args.getDouble(ARGS_LAT);
        double lng = args.getDouble(ARGS_LNG);
        LatLng latLng = new LatLng(lat, lng);
        GoogleMap map = getMap();

        Log.d(TAG, "lat lng" + lat + " " + lng);
        if (null != map) {
            map.addMarker(new MarkerOptions().position(latLng).title(args.getString(ARGS_NAME)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM));
            map.animateCamera(CameraUpdateFactory.zoomTo(10), CAMERA_ZOOM_DURATION_MS, null);
        }

        return view;
    }
}
