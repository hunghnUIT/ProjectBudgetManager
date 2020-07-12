package com.example.projectbudgetmanager.back_end.controller;

import android.os.AsyncTask;
import android.util.Log;

import com.example.projectbudgetmanager.back_end.BaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostTrans extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
        OkHttpClient client = new OkHttpClient(); //Default
        MediaType JSON = MediaType.parse("application/json; charset=utf-8"); //Default
        RequestBody requestBody = RequestBody.create(strings[1], JSON); //Create a request body from Json,

        Log.e("check", "doInBackground: String in request body" + strings[1]);
        Request request = new Request.Builder().url(strings[0])
                .post(requestBody)
                .addHeader("x-access-token", BaseUser.base)
                .build();
        Response response = null;
        String redata = "";
        try {
            Log.e("check", "doInBackground: try to execute");
            response = client.newCall(request).execute();
            if (response.code() == 200) {
                redata = "OK";
                Log.e("Check result post", "doInBackground: check response "+ response.toString());
                Log.e("check result", "doInBackground: Ok");
            }
        } catch (IOException e) {
            Log.e("aaa", e.toString());
        }
        return redata;
    }
}
