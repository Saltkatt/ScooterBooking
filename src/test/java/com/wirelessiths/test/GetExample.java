package com.wirelessiths.test;

import java.io.IOException;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class GetExample {

    //OkHttpClient client = new OkHttpClient();

    String run(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer eyJraWQiOiJ5Y3RoWnIra2RqTGo0MkhYZGhJeEFpa294cUJrNXBqTXRjNGxlYWJRWkd3PSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJiNDUxZDU2Zi04OWU2LTRmZDQtYWJjNC1kNjVjN2UwZWVmMjMiLCJldmVudF9pZCI6ImE3MmY5NGQ2LWFlYjEtNDJmZS1hNmJkLTY2ZGViODRiNmNlOSIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoib3BlbmlkIGVtYWlsIiwiYXV0aF90aW1lIjoxNTY4NDU1NTY5LCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9DRURISHN1WUYiLCJleHAiOjE1Njg0NTkxNjksImlhdCI6MTU2ODQ1NTU2OSwidmVyc2lvbiI6MiwianRpIjoiNDQwNTYxODctY2YzOS00YmFjLWEwZTQtMjViYTgxZjJmZjNlIiwiY2xpZW50X2lkIjoiN245Mjlta2tzbmpsM2Z0anRlcDJsZTZzcTQiLCJ1c2VybmFtZSI6ImNhcmwwOTEwIn0.P_qsGisJ3sFXZvPVUA0GoASsY0HB_zX12agtGM6Vi1jBquXSoPP3oy41QOZ9F9qPWVS9gIcmjchoA8VLCeOa0DNII0tQBAen9ylS8ARnnUADARcNCEgBkVmXIGk9R9vAmNvCFZ1MWQi2H78WaXkdz-JfvPWpvkyd6akIWlGOilGg3a9uujlVHpSuv3OT07xeXIF4S-HMNHeDuiBjjsQYSyA2gNpMQ2v6pLQEyX5f-8wSqk29cniP8GbhpyiBkKfkZf98ivQ9SaZCnc6omnBjqRWcBaBIfYGTMWhYSqte3GnU77n7VAJGu8v6WtP1G5TMTqkpZ5R49wLkGu47It863Q")
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
