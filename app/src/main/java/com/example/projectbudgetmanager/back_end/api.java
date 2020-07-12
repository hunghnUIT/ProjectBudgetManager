package com.example.projectbudgetmanager.back_end;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface api {

    @FormUrlEncoded
    @POST("posttrans")
    Call<ResponseBody> postTrans(
            @Field("kind") String kind,
            @Field("amount") int amount,
            @Field("category") String category,
            @Field("note") String note
    );
}
