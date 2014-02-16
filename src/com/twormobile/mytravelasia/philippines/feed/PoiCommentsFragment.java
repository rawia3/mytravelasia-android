package com.twormobile.mytravelasia.philippines.feed;

import android.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.twormobile.mytravelasia.philippines.R;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import java.util.ArrayList;

/**
 * A fragment to list and make comments.
 *
 * @author avendael
 */
public class PoiCommentsFragment extends Fragment {
    private static final String TAG = PoiCommentsFragment.class.getSimpleName();

    public static final String ARGS_POI_ID = "com.twormobile.mytravelasia.poi_id";

    private PullToRefreshLayout mPullToRefreshLayout;
    private LoaderManager.LoaderCallbacks<Cursor> mLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.poi_comments_fragment, container, false);
        ListView listView = (ListView) view.findViewById(R.id.lv_comments_list);
        ArrayList<String> items = new ArrayList<String>();

        for (int i = 0; i < 20; i++) {
            items.add("item " + i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        return view;
    }
}
