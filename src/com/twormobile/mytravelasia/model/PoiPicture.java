package com.twormobile.mytravelasia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model class for a PoiDetail's picture.
 *
 * @author avendael
 */
public class PoiPicture {
    @Expose
    @SerializedName("id")
    private long resourceId;

    @Expose
    private long poiId;

    @Expose
    private String caption;

    @Expose
    @SerializedName("thumb")
    private String thumbnailUrl;

    @Expose
    @SerializedName("full")
    private String fullImageUrl;

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public long getPoiId() {
        return poiId;
    }

    public void setPoiId(long poiId) {
        this.poiId = poiId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getFullImageUrl() {
        return fullImageUrl;
    }

    public void setFullImageUrl(String fullImageUrl) {
        this.fullImageUrl = fullImageUrl;
    }
}
