package com.twormobile.mytravelasia.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.twormobile.mytravelasia.model.Poi;

import java.util.List;

/**
 * JSON parser for the get feed request.
 *
 * @author avendael
 */
public class FeedResponse {
    @Expose
    @SerializedName("data")
    private List<Poi> feeds;

    @Expose
    private long totalPages;

    public List<Poi> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Poi> feeds) {
        this.feeds = feeds;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }
}
