/*
 * TestBotResponse is the response object for the lambda request.
 */

package com.wirelessiths;

import com.google.gson.Gson;

public class TestBotResponse {
    private String message;

    public String toString() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;

    }
}
