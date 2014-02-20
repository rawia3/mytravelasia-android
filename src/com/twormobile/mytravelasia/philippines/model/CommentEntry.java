package com.twormobile.mytravelasia.philippines.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Model class for a user's comment in a Poi.
 *
 * @author avendael
 */
public class CommentEntry implements Parcelable {
    public CommentEntry(Parcel parcel) {
        fbUserProfileId = parcel.readLong();
        resourceId = parcel.readLong();
        fbUserProfileName = parcel.readString();
        content = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(fbUserProfileId);
        parcel.writeLong(resourceId);
        parcel.writeString(fbUserProfileName);
        parcel.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CommentEntry> CREATOR = new Creator<CommentEntry>() {
        @Override
        public CommentEntry createFromParcel(Parcel parcel) {
            return new CommentEntry(parcel);
        }

        @Override
        public CommentEntry[] newArray(int i) {
            return new CommentEntry[i];
        }
    };
    /**
     * ID of the comment entry from the remote resource. This is different from the row id stored in the local database.
     */
    @Expose

    @SerializedName("comment_id")
    private long resourceId;

    /**
     * Facebook profile ID of the user who made the comment.
     */
    @Expose
    @SerializedName("profile_id")
    private long fbUserProfileId;

    /**
     * Facebook profile name of the user who made the comment.
     */
    @Expose
    @SerializedName("name")
    private String fbUserProfileName;

    /**
     * Comment content.
     */
    @Expose
    private String content;

    /**
     * Age of the comment since creation date.
     */
    @Expose
    private String age;

    /**
     * Date when the comment was created.
     */
    @Expose
    private Date createdAt;

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public long getFbUserProfileId() {
        return fbUserProfileId;
    }

    public void setFbUserProfileId(long fbUserProfileId) {
        this.fbUserProfileId = fbUserProfileId;
    }

    public String getFbUserProfileName() {
        return fbUserProfileName;
    }

    public void setFbUserProfileName(String fbUserProfileName) {
        this.fbUserProfileName = fbUserProfileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
