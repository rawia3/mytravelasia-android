package com.twormobile.mytravelasia.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.twormobile.mytravelasia.model.PoiDetail;
import com.twormobile.mytravelasia.model.PoiPicture;

import java.util.List;

/**
 * Parser for a feed detail resonse.
 *
 * @author avendael
 */
public class FeedDetailResponse {
    @Expose
    @SerializedName("poi")
    private PoiDetailResponse poiDetail;

    @Expose
    private List<PoiPicture> poiPictures;

    public PoiDetail getPoiDetail() {
        PoiDetail detail = poiDetail.getPoiDetail();

        if (detail != null
                && (detail.getPictures() == null || detail.getPictures().size() == 0)) {
            detail.setPictures(poiPictures);
        }

        return detail;
    }

    public void setPoiDetail(PoiDetail poiDetail) {
        this.poiDetail.setPoiDetail(poiDetail);
    }

    public List<PoiPicture> getPoiPictures() {
        return poiPictures;
    }

    public void setPoiPictures(List<PoiPicture> poiPictures) {
        this.poiPictures = poiPictures;
    }
}
