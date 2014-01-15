package com.twormobile.mytravelasia.philippines.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.twormobile.mytravelasia.philippines.model.PoiDetails;

/**
 * Parser for a poi detail response.
 *
 * @author avendael
 */
public class PoiDetailResponse {
    @Expose
    @SerializedName("poi")
    private PoiDetails poiDetails;

    public PoiDetails getPoiDetails() {
        return poiDetails;
    }

    public void setPoiDetails(PoiDetails poiDetails) {
        this.poiDetails = poiDetails;
    }

    @Override
    public String toString() {
        return poiDetails.getName();
    }
}
