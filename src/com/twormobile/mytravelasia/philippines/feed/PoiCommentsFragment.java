package com.twormobile.mytravelasia.philippines.feed;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.twormobile.mytravelasia.philippines.R;
import com.twormobile.mytravelasia.philippines.model.CommentEntry;
import com.twormobile.mytravelasia.philippines.util.AppConstants;
import com.twormobile.mytravelasia.philippines.util.Log;

import java.util.ArrayList;

/**
 * A fragment to list and make comments.
 *
 * @author avendael
 */
public class PoiCommentsFragment extends Fragment {
    private static final String TAG = PoiCommentsFragment.class.getSimpleName();

    public static final String ARGS_POI_ID = "com.twormobile.mytravelasia.poi_id";
    public static final String ARGS_PROFILE_ID = "com.twormobile.mytravelasia.profile_id";
    public static final String ARGS_POI_COMMENTS = "com.twormobile.mytravelasia.poi_comments";

    private static final int MENU_DELETE = 0;
    private static final int MENU_EDIT = 1;

    private long mPoiId;
    private String mProfileId;
    private Callbacks mCallbacks;
    private CommentsArrayAdapter mAdapter;
    private ArrayList<CommentEntry> mComments;
    private EditText mEtComment;
    private ListView mListView;

    public interface Callbacks {
        /**
         * The action to take when the post button is clicked.
         *
         * @param comment The comment content.
         * @param poiId The poi id.
         */
        public void onPostClicked(long poiId, String comment);

        /**
         * The action to take when the user selects edit on the comment's context menu.
         *
         * @param poiId The poi id where the comment belongs.
         * @param commentId The comment's id.
         * @param content The comment's original content.
         */
        public void onPostEdit(long poiId, long commentId, String content);

        /**
         * The action to take when the user selects delete on the comment's context menu.
         *
         * @param poiId The poi id where the comment belongs.
         * @param commentId The comment's id.
         */
        public void onPostDelete(long poiId, long commentId);
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
        View view = inflater.inflate(R.layout.poi_comments_fragment, container, false);
        mListView = (ListView) view.findViewById(R.id.lv_comments_list);
        Bundle args = savedInstanceState != null ? savedInstanceState : getArguments();
        Button btnPost = (Button) view.findViewById(R.id.btn_post);
        mEtComment = (EditText) view.findViewById(R.id.et_comment);
        mPoiId = args.getLong(ARGS_POI_ID);
        mProfileId = args.getString(ARGS_PROFILE_ID);
        mComments = args.getParcelableArrayList(ARGS_POI_COMMENTS);
        mAdapter = new CommentsArrayAdapter(getActivity(), R.layout.comment_list_item, mComments);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable text = mEtComment.getText();

                if (null == text) return;

                mCallbacks.onPostClicked(mPoiId, text.toString());
            }
        });

        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);
        setListViewHeightBasedOnChildren(mListView);

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        switch (view.getId()) {
            case R.id.lv_comments_list:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                CommentEntry commentEntry = (CommentEntry) mListView.getItemAtPosition(info.position);

                Log.d(TAG, "comment profile id " + commentEntry.getFbUserProfileId());
                Log.d(TAG, "user profile id " + mProfileId);

                if (null != mProfileId && mProfileId.equals(Long.toString(commentEntry.getFbUserProfileId()))) {
                    menu.add(Menu.NONE, MENU_EDIT, 0, "Edit");
                    menu.add(Menu.NONE, MENU_DELETE, 1, "Delete");
                }

                break;
            default:
                super.onCreateContextMenu(menu, view, menuInfo);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int index = item.getItemId();
        CommentEntry commentEntry = (CommentEntry) mListView.getItemAtPosition(info.position);

        if (null == commentEntry) return false;

        long commentId = commentEntry.getResourceId();

        switch (index) {
            case MENU_EDIT:
                mCallbacks.onPostEdit(mPoiId, commentId, commentEntry.getContent());

                return true;
            case MENU_DELETE:
                mCallbacks.onPostDelete(mPoiId, commentId);

                return true;
            default:
                return false;
        }
    }

    /**
     * This must be called after a successful create or delete comment event to update the comments list.
     *
     * @param commentEntries The new set of comments.
     */
    public void updateCommentList(ArrayList<CommentEntry> commentEntries) {
        Log.d(TAG, "updating comment list");
        mComments = commentEntries;

        mAdapter = new CommentsArrayAdapter(getActivity(), R.layout.comment_list_item, mComments);

        mListView.setAdapter(mAdapter);
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        CommentsArrayAdapter listAdapter = (CommentsArrayAdapter) listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
