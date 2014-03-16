package com.twormobile.mytravelasia.philippines.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Parser for comment response.
 *
 * @author avendael
 */
public class CommentResponse {
    @Expose
    @SerializedName("valid")
    private boolean isValid;

    @Expose
    private String message;

    @Expose
    @SerializedName("total")
    private int totalComments;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }
}
