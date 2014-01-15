package com.twormobile.mytravelasia.philippines.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.twormobile.mytravelasia.philippines.model.Poi;

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

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("FeedResponse: \n");
        for (Poi poi : feeds) {
            buffer.append(poi.getContent() + "\n");
        }

        return buffer.toString();
    }
}
