package com.twormobile.mytravelasia.feed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twormobile.mytravelasia.R;
import com.twormobile.mytravelasia.util.Log;

/**
 * A fragment which displays a POI's location in a map.
 *
 * @author avendael
 */
public class PoiMapFragment extends Fragment {
    private static final String TAG = PoiMapFragment.class.getSimpleName();
    private static final int CAMERA_ZOOM = 15;
    private static final int CAMERA_ZOOM_DURATION_MS = 10;

    public static final String ARGS_LAT = "com.twormobile.mytravelasia.lat";
    public static final String ARGS_LNG = "com.twormobile.mytravelasia.lng";
    public static final String ARGS_NAME = "com.twormobile.mytravelasia.name";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.poi_map_fragment, container, false);
        Bundle args = getArguments();

        if (null == args) {
            args = savedInstanceState;
        }

        double lat = args.getDouble(ARGS_LAT);
        double lng = args.getDouble(ARGS_LNG);
        LatLng latLng = new LatLng(lat, lng);
        GoogleMap map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        Log.d(TAG, "lat lng" + lat + " " + lng);
        if (null != map) {
            map.addMarker(new MarkerOptions().position(latLng).title(args.getString(ARGS_NAME)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM));
            map.animateCamera(CameraUpdateFactory.zoomTo(10), CAMERA_ZOOM_DURATION_MS, null);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // We need to do this. Otherwise, MainActivity will crash because of a dupe SupportMapFragment id.
        Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }
}
