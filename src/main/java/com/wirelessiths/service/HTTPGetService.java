package com.wirelessiths.service;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class HTTPGetService {



    public String run(String url, String auth) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", auth)
                .build();
        Response response = null;

            response = client.newCall(request).execute();

        return response.body().string();
    }
}
