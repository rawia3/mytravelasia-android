package com.twormobile.mytravelasia.philippines.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * JSON parser for the register response.
 *
 * @author avendael
 */
public class RegisterResponse {
    @Expose
    @SerializedName("valid")
    private boolean isValid;

    @Expose
    private String message;

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

    @Override
    public String toString() {
        return "Register: " + isValid + " - " + message;
    }
}
