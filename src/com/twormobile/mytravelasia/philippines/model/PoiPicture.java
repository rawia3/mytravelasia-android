package com.twormobile.mytravelasia.philippines.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model class for a PoiDetails' picture.
 *
 * @author avendael
 */
public class PoiPicture implements Parcelable {
    public static final Creator<PoiPicture> CREATOR = new Creator<PoiPicture>() {
        @Override
        public PoiPicture createFromParcel(Parcel parcel) {
            return new PoiPicture(parcel);
        }

        @Override
        public PoiPicture[] newArray(int size) {
            return new PoiPicture[size];
        }
    };

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

    public PoiPicture() {}

    public PoiPicture(Parcel parcel) {
        long[] longData = new long[2];
        String[] stringData = new String[3];

        parcel.readLongArray(longData);
        parcel.readStringArray(stringData);

        resourceId = longData[0];
        poiId = longData[1];
        caption = stringData[0];
        thumbnailUrl = stringData[1];
        fullImageUrl = stringData[2];
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLongArray(new long[] {resourceId, poiId});
        parcel.writeStringArray(new String[] {caption, thumbnailUrl, fullImageUrl});
    }

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

    @Override
    public int describeContents() {
        return 0;
    }
}
