package com.example.viewpagerdemo;

import com.google.gson.JsonArray;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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
    @POST("contact/{userID}/")
    Call<Contact> postUserContact(
            @Field("id") String id,
            @Field("phone_number") String phone_number,
            @Field("name") String name,
            @Path("userID")String userID
    );
    @Multipart
    @POST("image/")
    Call<ResponseBody> upload(
            @Field("id") String id,
            @Field("file") File file
    );
}
