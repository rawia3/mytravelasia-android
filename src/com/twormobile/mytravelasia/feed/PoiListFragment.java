package com.twormobile.mytravelasia.feed;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import com.twormobile.mytravelasia.R;
import com.twormobile.mytravelasia.db.MtaPhProvider;
import com.twormobile.mytravelasia.model.Poi;
import com.twormobile.mytravelasia.util.AppConstants;
import com.twormobile.mytravelasia.util.Log;

/**
 * A fragment which shows a list of POIs (points of interest).
 *
 * @author avendael
 */
public class PoiListFragment extends ListFragment {
    private static final String TAG = PoiListFragment.class.getSimpleName();

    private PoiCursorAdapter mAdapter;
    private LoaderCallbacks<Cursor> mLoader;
    private long mCurrentPage;
    private long mTotalPages;
    private boolean isLoadingNextPage;

    private Callbacks mCallbacks = sDummyCallbacks;
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onPoiSelected(long feedId) {
        }

        @Override
        public void onNextPage(long page) {
        }
    };


    public interface Callbacks {
        /**
         * The action that the activity will execute when a POI is clicked. @param position The clicked POI's position
         * in the list.
         */
        public void onPoiSelected(long feedId);

        /**
         * The action that the activity should perform when the list has reached the last item of the current page.
         */
        public void onNextPage(long page);
    }

    /**
     * Loads POI items from the DB into the adapter asynchronously.
     */
    private class PoiListLoader implements LoaderCallbacks<Cursor> {
        String[] projection = {
                Poi._ID, Poi.NAME, Poi.ADDRESS, Poi.TOTAL_COMMENTS, Poi.TOTAL_LIKES,
                Poi.LATITUDE, Poi.LONGITUDE, Poi.IMAGE_THUMB_URL, Poi.RESOURCE_ID
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.d(TAG, "created loader");
            return new CursorLoader(getActivity(), MtaPhProvider.POI_URI, projection, null, null,
                    Poi.CREATED_AT + " DESC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.d(TAG, "load finished");
            mAdapter.swapCursor(data);
            if (data != null && data.getCount() > 0) {
                setListShown(true);
            } else {
                // Otherwise, show a view for an empty dataset.
                // TODO: empty dataset is better handled by the activity showing a different fragment instead of this one.
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }
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
        Log.d(TAG, "onCreateView() -- START");
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mAdapter = new PoiCursorAdapter(getActivity(), null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mLoader = new PoiListLoader();

        Log.d(TAG, "onCreateView() -- END");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getListView();
        View listViewFooter = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.poi_list_footer, null, false);

        setListShown(false);
        listView.addFooterView(listViewFooter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItemInScreen = firstVisibleItem + visibleItemCount;

                Log.d(TAG, "last item " + lastItemInScreen + " first " + firstVisibleItem
                        + " visible " + visibleItemCount + " total " + totalItemCount);
                Log.d(TAG, "current page " + mCurrentPage + " total pages " + mTotalPages
                        + " loading " + isLoadingNextPage);
                Log.d(TAG, "" + (mCurrentPage < mTotalPages) + " "
                        + (lastItemInScreen == totalItemCount) + " " + !isLoadingNextPage);
                if (mCurrentPage < mTotalPages && (lastItemInScreen == totalItemCount)) { // && !isLoadingNextPage) {
                    Log.d(TAG, "attempt to load the next page " + mCurrentPage + " " + mTotalPages);
                    isLoadingNextPage = true;

                    mCallbacks.onNextPage(mCurrentPage + 1L);
                }
            }
        });
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, mLoader);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        mCallbacks.onPoiSelected(mAdapter.getItemId(position));
    }

    public long getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(long currentPage) {
        Log.d(TAG, "setCurrentPage() -- " + mCurrentPage + " " + currentPage);
        if (currentPage == mCurrentPage + 1) isLoadingNextPage = false;

        mCurrentPage = currentPage;
    }

    public long getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(long totalPages) {
        mTotalPages = totalPages;
    }
}
