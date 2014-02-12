package com.twormobile.mytravelasia.philippines.feed;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.TextView;
import com.twormobile.mytravelasia.philippines.R;
import com.twormobile.mytravelasia.philippines.model.PoiDetails;
import com.twormobile.mytravelasia.philippines.model.PoiPicture;
import com.twormobile.mytravelasia.philippines.ui.CarouselPhotoFragment;
import com.twormobile.mytravelasia.philippines.ui.FragmentListPagerAdapter;
import com.twormobile.mytravelasia.philippines.ui.ZoomOutPageTransformer;
import com.twormobile.mytravelasia.philippines.util.AppConstants;
import com.twormobile.mytravelasia.philippines.util.Log;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment which displays a Poi's details.
 *
* @author avendael
 */
public class PoiDetailsFragment extends Fragment {
    public static final String TAG = PoiDetailsFragment.class.getSimpleName();
    public static final String ARG_POI_DETAILS = "com.twormobile.mytravelasia.arg_poi_details";

    private Callbacks mCallbacks;
    private PoiDetails mPoiDetails;
    private ViewPager mViewPager;
    private FragmentListPagerAdapter mAdapter;
    private TextView mTvLikes;
    private TextView mTvComments;

    public interface Callbacks {
        /**
         * The action to take when the map action of a POI is clicked.
         *
         * @param latitude The POI's latitude.
         * @param longitude The POI's longitude.
         * @param name The POI's name.
         */
        public void onViewMap(double latitude, double longitude, String name);

        /**
         * The action to take when the like button of a POI is clicked.
         *
         * @param poiId Resource ID of the POI.
         * @param isLiked Whether the POI is already liked by the same user.
         */
        public void onLikeClicked(long poiId, boolean isLiked);
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
        View view = inflater.inflate(R.layout.poi_details_fragment, container, false);
        Bundle args = savedInstanceState != null ? savedInstanceState : getArguments();
        TextView tvPoiName = (TextView) view.findViewById(R.id.tv_poi_name);
        TextView tvPoiAddress = (TextView) view.findViewById(R.id.tv_poi_address);
        TextView tvTelephone = (TextView) view.findViewById(R.id.tv_telephone);
        TextView tvWebsite = (TextView) view.findViewById(R.id.tv_website);
        TextView tvEmail = (TextView) view.findViewById(R.id.tv_email);
        TextView tvDescription = (TextView) view.findViewById(R.id.tv_description);
        mTvComments = (TextView) view.findViewById(R.id.tv_comments);
        mTvLikes = (TextView) view.findViewById(R.id.tv_likes);

        mTvLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "poi id liked " + mPoiDetails.getResourceId());
                mCallbacks.onLikeClicked(mPoiDetails.getResourceId(), mPoiDetails.isLiked());
            }
        });

        if (args != null) {
            mPoiDetails = args.getParcelable(ARG_POI_DETAILS);

            tvPoiName.setText(mPoiDetails.getName());
            tvPoiAddress.setText(mPoiDetails.getAddress());
            mTvComments.setText(Long.toString(mPoiDetails.getTotalComments()));
            mTvLikes.setText(Long.toString(mPoiDetails.getTotalLikes()));
            tvTelephone.setText(mPoiDetails.getTelNo());
            tvWebsite.setText(mPoiDetails.getWebUrl());
            tvEmail.setText(mPoiDetails.getEmail());
            tvDescription.setText(mPoiDetails.getDescription());
        } else {
            mPoiDetails = new PoiDetails();
        }

        List<PoiPicture> pictures = mPoiDetails.getPictures();

        Log.d(TAG, "TOTAL LIKES " + mPoiDetails.getTotalLikes());
        if (pictures != null && pictures.size() > 0) {
            view.findViewById(R.id.fl_photo_container).setVisibility(View.VISIBLE);
            initCarousel(view);
        }

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.poi_details_menu_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map:
                mCallbacks.onViewMap(mPoiDetails.getLatitude(), mPoiDetails.getLongitude(), mPoiDetails.getName());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This must be called after a successful like event to notify this fragment that the fragment is already liked (or
     * not).
     *
     * @param isLiked Boolean response from the server.
     */
    public void likePoi(boolean isLiked) {
        long likes = isLiked ? mPoiDetails.getTotalLikes() + 1 : mPoiDetails.getTotalLikes() - 1;

        mPoiDetails.setLiked(isLiked);
        mPoiDetails.setTotalLikes(likes);
        mTvLikes.setText(Long.toString(likes));
    }

    /**
     * This must be called after a succesful comment event to update the total number of comments in the POI.
     *
     * @param totalComments The new total number of comments.
     */
    public void updateTotalComments(int totalComments) {
        mPoiDetails.setTotalComments(totalComments);

        mTvComments.setText(Integer.toString(totalComments));
    }

    private void initCarousel(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.vp_carousel);
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) view.findViewById(R.id.vi_indicator);

        // Prevent the ScrollView from intercepting a ViewPager flip
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        mAdapter = new FragmentListPagerAdapter(getActivity().getSupportFragmentManager(), fragments);

        List<PoiPicture> pictures = mPoiDetails.getPictures();

        for (PoiPicture picture : pictures) {
            CarouselPhotoFragment carouselPhotoFragment = new CarouselPhotoFragment();
            Bundle args = new Bundle();

            args.putString(AppConstants.ARG_PHOTO_URL, picture.getFullImageUrl());
            carouselPhotoFragment.setArguments(args);

            fragments.add(carouselPhotoFragment);
        }

        mAdapter.notifyDataSetChanged();
        mViewPager.setAdapter(mAdapter);

        if (pictures.size() <= 1) {
            circlePageIndicator.setVisibility(View.GONE);
        } else {
            circlePageIndicator.setViewPager(mViewPager);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mViewPager.setPageTransformer(false, new ZoomOutPageTransformer());
        }
    }
}
