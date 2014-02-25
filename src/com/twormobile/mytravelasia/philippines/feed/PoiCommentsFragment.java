package com.twormobile.mytravelasia.philippines.feed;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
    public static final String ARGS_POI_COMMENTS = "com.twormobile.mytravelasia.poi_comments";

    private long mPoiId;
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

        return view;
    }

    /**
     * This must be called after a successful comment event to update the comments list.
     *
     * @param commentEntries The new set of comments.
     */
    public void updateCommentList(ArrayList<CommentEntry> commentEntries) {
        Log.d(TAG, "updating comment list");
        mComments = commentEntries;

        mAdapter = new CommentsArrayAdapter(getActivity(), R.layout.comment_list_item, mComments);

        mListView.setAdapter(mAdapter);
    }
}
