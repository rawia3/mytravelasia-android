package com.twormobile.mytravelasia.philippines.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * JSON parser for the like response.
 *
 * @author avendael
 */
public class LikeResponse {
    @Expose
    private long poiId;

    @Expose
    private int likes;

    @Expose
    @SerializedName("liked")
    private boolean isLiked;

    public long getPoiId() {
        return poiId;
    }

    public void setPoiId(long poiId) {
        this.poiId = poiId;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    @Override
    public String toString() {
        return "Like " + poiId +  ": " + isLiked;
    }
}
