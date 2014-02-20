package com.twormobile.mytravelasia.philippines.feed;

import android.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.twormobile.mytravelasia.philippines.R;
import com.twormobile.mytravelasia.philippines.model.CommentEntry;
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
    public static final String ARGS_POI_COMMENTS = "com.twormobile.mytravelasia.poi_comments";

    private PullToRefreshLayout mPullToRefreshLayout;
    private CommentsArrayAdapter mAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> mLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.poi_comments_fragment, container, false);
        ListView listView = (ListView) view.findViewById(R.id.lv_comments_list);
        Bundle args = savedInstanceState != null ? savedInstanceState : getArguments();
        ArrayList<CommentEntry> comments = args.getParcelableArrayList(ARGS_POI_COMMENTS);
        mAdapter = new CommentsArrayAdapter(getActivity(), R.layout.comment_list_item, comments);

        listView.setAdapter(mAdapter);

        return view;
    }
}
