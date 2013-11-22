package com.twormobile.mytravelasia.feed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.twormobile.mytravelasia.R;
import com.twormobile.mytravelasia.model.PoiDetails;

/**
 * A fragment which displays a Poi's details.
 *
 * @author avendael
 */
public class PoiDetailsFragment extends Fragment {
    public static final String TAG = PoiDetailsFragment.class.getSimpleName();
    public static final String ARG_POI_DETAILS = "com.twormobile.mytravelasia.arg_poi_details";

    private PoiDetails mPoiDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.poi_details_fragment, container, false);
        Bundle args = savedInstanceState != null ? savedInstanceState : getArguments();

        if (args != null) {
            mPoiDetails = args.getParcelable(ARG_POI_DETAILS);
        }

        return view;
    }
}
