package com.twormobile.mytravelasia.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.loopj.android.image.SmartImageView;
import com.twormobile.mytravelasia.R;

/**
 * Displays a photo in a ViewPager carousel.
 *
 * @author avendael
 */
public class CarouselPhotoFragment extends Fragment {
    private static final String TAG = CarouselPhotoFragment.class.getSimpleName();

    public static final String ARG_PHOTO_URL = "com.twormobile.mytravelasia.ui.photo_url";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.carousel_photo_fragment, container, false);
        SmartImageView ivPhoto = (SmartImageView) view.findViewById(R.id.iv_carousel_photo);
        Bundle args = getArguments();

        if (args == null) {
            args = savedInstanceState; // TODO: write a proper saveInstanceState
        }

        String imageUrl = args.getString(ARG_PHOTO_URL);

        if (imageUrl != null) ivPhoto.setImageUrl(imageUrl);
        else ivPhoto.setImageResource(R.drawable.city);

        return view;
    }
}
