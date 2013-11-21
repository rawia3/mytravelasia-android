package com.twormobile.mytravelasia.feed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.twormobile.mytravelasia.R;

/**
 * A fragment which displays a Poi's details.
 *
 * @author avendael
 */
public class PoiDetailsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.poi_details_fragment, container, false);

        return view;
    }
}
