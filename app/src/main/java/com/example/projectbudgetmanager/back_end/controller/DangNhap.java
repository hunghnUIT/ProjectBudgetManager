package com.example.projectbudgetmanager.back_end.controller;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DangNhap extends AsyncTask<String ,Void,Response> {
    protected Response doInBackground(String... strings) {
        OkHttpClient client=new OkHttpClient() ;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody=RequestBody.create(strings[1], JSON);
        Request request=new Request.Builder().url(strings[0])
                .post(requestBody)
                .build();
        Response response=null;
        try {
            response=client.newCall(request).execute();

        } catch (IOException e) {
            Log.e("aaa",e.toString());
        }
        return response;
    }
}