package com.example.viewpagerdemo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {
    @FormUrlEncoded
    @POST("account/")
    Call<ResponseBody> useraccount(
            @Field("account") String account
    );
}
