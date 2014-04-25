package com.twormobile.mytravelasia.philippines.feed;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.facebook.widget.ProfilePictureView;

import com.twormobile.mytravelasia.philippines.R;
import com.twormobile.mytravelasia.philippines.model.CommentEntry;
import com.twormobile.mytravelasia.philippines.util.Log;

import java.util.List;

/**
 * Bridge between an array of comments and the view.
 *
 * @author avendael
 */
public class CommentsArrayAdapter extends ArrayAdapter<CommentEntry> {
    private static final String TAG = CommentsArrayAdapter.class.getSimpleName();

    static class ViewHolder {
        TextView tvContent;
        TextView tvProfileName;
        ProfilePictureView fbProfilePic;
    }

    public CommentsArrayAdapter(Context context, int resource, List<CommentEntry> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;

        if (null == view) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            view = inflater.inflate(R.layout.comment_list_item, parent, false);
            viewHolder.tvContent = (TextView) view.findViewById(R.id.tv_content);
            viewHolder.tvProfileName = (TextView) view.findViewById(R.id.tv_profile_name);
            viewHolder.fbProfilePic = (ProfilePictureView) view.findViewById(R.id.iv_profile_pic);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        CommentEntry commentEntry = getItem(position);
        String content = commentEntry.getContent();
        String profileName = commentEntry.getFbUserProfileName();
        String profileId = String.valueOf(commentEntry.getFbUserProfileId());

        viewHolder.tvContent.setText(content);
        viewHolder.tvProfileName.setText(profileName);
        viewHolder.fbProfilePic.setProfileId(profileId);

        return view;
    }
}
