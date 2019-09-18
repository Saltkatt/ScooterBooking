package com.wirelessiths.test;

import java.io.IOException;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class GetExample {


    String run(String url, String auth) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                //.header("Authorization", "Bearer " + auth)
                .header("Authorization", auth)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return response.body().string();
    }

}
