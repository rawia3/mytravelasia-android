package com.twormobile.mytravelasia.philippines.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.twormobile.mytravelasia.philippines.R;
import com.twormobile.mytravelasia.philippines.util.AppConstants;

/**
 * A dialog which allows the user to edit a comment.
 *
 * @author avendael
 */
public class EditCommentDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {
    private static final String TAG = EditCommentDialogFragment.class.getSimpleName();

    public static final String EXTRAS_POI_ID = "poi_id";
    public static final String EXTRAS_COMMENT_ID = "comment_id";
    public static final String EXTRAS_COMMENT_CONTENT = "comment_content";

    private long mPoiId;
    private long mCommentId;
    private EditText mCommentContent;
    private Callbacks mCallbacks;

    public interface Callbacks {
        /**
         * The action to take when the user is done editing.
         *
         * @param poiId The comment's poi id.
         * @param commentId The comment's id
         * @param content The comment's edited content.
         */
        public void onPostEdited(long poiId, long commentId, String content);
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
        View view = inflater.inflate(R.layout.comment_edit_dialog, container);
        mCommentContent = (EditText) view.findViewById(R.id.et_comment);
        Bundle args = savedInstanceState != null ? savedInstanceState : getArguments();
        mPoiId = args.getLong(EXTRAS_POI_ID);
        mCommentId = args.getLong(EXTRAS_COMMENT_ID);

        getDialog().setTitle("Edit Comment");
        mCommentContent.setText(args.getString(EXTRAS_COMMENT_CONTENT));
        mCommentContent.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mCommentContent.setOnEditorActionListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCallbacks = null;
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
                mCallbacks.onPostEdited(mPoiId, mCommentId, mCommentContent.getText().toString());
                dismiss();

                return true;
            default:
                return false;
        }
    }
}
