package com.twormobile.mytravelasia.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.twormobile.mytravelasia.model.PoiDetails;
import com.twormobile.mytravelasia.model.PoiPicture;

import java.util.List;

/**
 * Parser for a feed detail response.
 *
 * @author avendael
 */
public class FeedDetailResponse {
    @Expose
    @SerializedName("poi")
    private PoiDetailResponse poiDetail;

    @Expose
    @SerializedName("pictures")
    private List<PoiPicture> poiPictures;

    public PoiDetails getPoiDetail() {
        PoiDetails detail = poiDetail.getPoiDetails();

        if (detail != null
                && (detail.getPictures() == null || detail.getPictures().size() == 0)) {
            detail.setPictures(poiPictures);
        }

        return detail;
    }

    public void setPoiDetail(PoiDetails poiDetails) {
        this.poiDetail.setPoiDetails(poiDetails);
    }

    public List<PoiPicture> getPoiPictures() {
        return poiPictures;
    }

    public void setPoiPictures(List<PoiPicture> poiPictures) {
        this.poiPictures = poiPictures;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        PoiDetails poiDetails = getPoiDetail();

        buffer.append(poiDetails.getName());
        buffer.append(": [");

        for (PoiPicture picture : poiDetails.getPictures()) {
            buffer.append(picture.getThumbnailUrl());
            buffer.append(", ");
        }

        buffer.append("]");

        return buffer.toString();
    }
}
