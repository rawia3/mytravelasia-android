package com.twormobile.mytravelasia.philippines.feed;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.loopj.android.image.SmartImageView;
import com.twormobile.mytravelasia.philippines.R;
import com.twormobile.mytravelasia.philippines.model.CommentEntry;

import java.util.List;

/**
 * Bridge between an array of comments and the view.
 *
 * @author avendael
 */
public class CommentsArrayAdapter extends ArrayAdapter<CommentEntry> {
    static class ViewHolder {
        TextView tvContent;
        SmartImageView ivProfilePic;
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
            viewHolder.ivProfilePic = (SmartImageView) view.findViewById(R.id.iv_profile_pic);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        CommentEntry commentEntry = getItem(position);
        String content = commentEntry.getContent();
        String photoUrl = "http://graph.facebook.com/" + commentEntry.getFbUserProfileId() + "/picture";

        viewHolder.tvContent.setText(content);
        viewHolder.ivProfilePic.setImageUrl(photoUrl);

        return view;
    }
}
