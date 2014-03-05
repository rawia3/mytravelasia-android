package com.twormobile.mytravelasia.philippines.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Parser for delete comment response.
 *
 * @author avendael
 */
public class DeleteCommentResponse {
    @Expose
    @SerializedName("valid")
    private boolean isValid;

    @Expose
    private String message;

    @Expose
    private int total;

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
