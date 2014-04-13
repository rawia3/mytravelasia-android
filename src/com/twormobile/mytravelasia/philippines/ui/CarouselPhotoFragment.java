package com.twormobile.mytravelasia.philippines.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.loopj.android.image.SmartImageView;
import com.twormobile.mytravelasia.philippines.R;
import com.twormobile.mytravelasia.philippines.util.AppConstants;

/**
 * Displays a photo in a ViewPager carousel.
 *
 * @author avendael
 */
public class CarouselPhotoFragment extends Fragment {
    private static final String TAG = CarouselPhotoFragment.class.getSimpleName();

    private Callbacks mCallbacks;

    public interface Callbacks {
        public void onPhotoClicked(String url);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(AppConstants.EXC_ACTIVITY_CALLBACK);
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.carousel_photo_fragment, container, false);
        SmartImageView ivPhoto = (SmartImageView) view.findViewById(R.id.iv_carousel_photo);
        Bundle args = getArguments();

        if (args == null) {
            args = savedInstanceState; // TODO: write a proper saveInstanceState
        }

        final String imageUrl = args.getString(AppConstants.ARG_PHOTO_URL);

        if (imageUrl != null) ivPhoto.setImageUrl(imageUrl);
        else ivPhoto.setImageResource(R.drawable.loading);

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onPhotoClicked(imageUrl);
            }
        });

        return view;
    }
}
