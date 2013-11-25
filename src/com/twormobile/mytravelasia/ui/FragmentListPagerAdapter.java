package com.twormobile.mytravelasia.ui;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * An adapter for a ViewPager. This adapter is preferred for larger lists of fragments.
 *
 * @author avendael
 */
public class FragmentListPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = FragmentStatePagerAdapter.class.getSimpleName();
    protected List<Fragment> mFragments;

    public FragmentListPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
        super(fragmentManager);
        mFragments = fragments;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    public List<Fragment> getFragments() {
        return mFragments;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
