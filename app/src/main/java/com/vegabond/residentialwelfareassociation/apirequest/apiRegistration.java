package com.vegabond.residentialwelfareassociation.apirequest;

import android.app.Activity;
import android.util.Log;

import com.vegabond.residentialwelfareassociation.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class apiRegistration {


    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public void userdetailsget(String emailID){
        String url = "https://residenceassociation.herokuapp.com/UserProfiles/"+emailID;
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    Log.d("Check5","4 "+myResponse);
                    variablesValues.userData = myResponse;
                }
            }
        });
    }

    public String userdetailspost(String url, String json) throws IOException {
        final String[] result = new String[1];

        Log.d("Check5","1");
        RequestBody body = RequestBody.create(json, JSON);
        Log.d("Check5","2");
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Log.d("Check5","3");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result[0] = response.body().string();
                Log.d("Check5","4 "+result[0]);

            }
        });
        return result[0];



//        try (Response response = client.newCall(request).execute()) {
//            Log.d("Check5","4");
//            return response.body().string();
//        }
    }

}
