package com.wirelessiths.test;

import java.io.IOException;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class GetExample {

    //OkHttpClient client = new OkHttpClient();
    String token = "eyJraWQiOiJ5Y3RoWnIra2RqTGo0MkhYZGhJeEFpa294cUJrNXBqTXRjNGxlYWJRWkd3PSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJiNDUxZDU2Zi04OWU2LTRmZDQtYWJjNC1kNjVjN2UwZWVmMjMiLCJldmVudF9pZCI6ImUwNDgyOTY4LWU1M2YtNDE2Zi1iNjZiLWEzYzg4NWE4Nzc4ZiIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoib3BlbmlkIGVtYWlsIiwiYXV0aF90aW1lIjoxNTY4NDU5NTY1LCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9DRURISHN1WUYiLCJleHAiOjE1Njg0NjMxNjUsImlhdCI6MTU2ODQ1OTU2NSwidmVyc2lvbiI6MiwianRpIjoiODVlYTc1ZmMtZDllNy00MTA2LTk2OWMtMTVmN2Q5ODllNzFlIiwiY2xpZW50X2lkIjoiN245Mjlta2tzbmpsM2Z0anRlcDJsZTZzcTQiLCJ1c2VybmFtZSI6ImNhcmwwOTEwIn0.ZAnXax00ZDrsSKuMz2felY9s1MnEVYdpPN3MhVfykFi-skM2lzl7P6bmynua9y8wgy3QNhdKHyhMt3o52VaASIFhlizFmmndzfqgdk9ghE1KQgmopbxk9EvFWqdCBTSHG1Pin5fnU6n26nqjUG6He8dy1w3Ln_fBeS_WIyfsNTRXkSbGJ115w7YZCmc9mcxIu-3oCC2kWv31mgHkH9Tj3hehVhRYZgLkWMllCp-Bejhh8gdul4oOCDaaiYPEjbe3WzzfsdOekvCYrATHO-8gJkZ7xWaMbdUyXoAIy7U3P7HTlWtVcg7ai2WG3r88DuHHoHt0qP7z6qSCqHp47imb3g";

    String run(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return response.body().string();
    }

//    public static void main(String[] args) throws IOException {
//        GetExample example = new GetExample();
//        String response = example.run("https://raw.github.com/square/okhttp/master/README.md");
//        System.out.println(response);
//    }

}
