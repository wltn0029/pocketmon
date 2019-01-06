package com.example.viewpagerdemo;

import com.google.gson.JsonArray;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {
    @FormUrlEncoded
    @POST("account/")
    Call<ResponseBody> useraccount(
            @Field("account") String account
    );

    @GET("contact/{userID}/")
    Call<List<Contact>> getUserContact(
            @Path("userID") String userID
    );

    @FormUrlEncoded
    @POST("contact/")
    Call<Contact> postUserContact(
            @Field("id") String userID,
            @Field("phone_number") String phone_number,
            @Field("name") String name
    );
}
