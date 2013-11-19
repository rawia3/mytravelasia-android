package com.twormobile.mytravelasia.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.twormobile.mytravelasia.model.PoiDetail;

/**
 * Parser for a poi detail response.
 *
 * @author avendael
 */
public class PoiDetailResponse {
    @Expose
    @SerializedName("poi")
    private PoiDetail poiDetail;

    public PoiDetail getPoiDetail() {
        return poiDetail;
    }

    public void setPoiDetail(PoiDetail poiDetail) {
        this.poiDetail = poiDetail;
    }

    @Override
    public String toString() {
        return poiDetail.getName();
    }
}
