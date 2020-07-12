package com.example.projectbudgetmanager.back_end.controller;

import android.os.AsyncTask;
import android.util.Log;

import com.example.projectbudgetmanager.back_end.BaseUser;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetTrans extends AsyncTask<String ,Void , String> {
    @Override
    protected String doInBackground(String... strings) {
        OkHttpClient client=new OkHttpClient() ;
        Response response=null;
        String re="";
        Request request=new Request.Builder().url(strings[0])
                .get()
                .addHeader("x-access-token", BaseUser.base)
                .build();
        try {
            response=client.newCall(request).execute();
            re=response.body().string();
        } catch (IOException e) {
            Log.e("aaa",e.toString());
        }
        return re;
    }
}
