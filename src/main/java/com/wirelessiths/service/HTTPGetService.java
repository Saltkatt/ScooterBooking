package com.wirelessiths.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class HTTPGetService {

    public String run(String url, String auth) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", auth)
                .build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }
}
