package com.example.zhaoh.retrofit2_text;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by zhaoh on 2016/4/11.
 */
public interface ApiService {

    @FormUrlEncoded
    @POST("api/customers")
    Observable<RegisterResponse> register(@Field("request_content") String request_content,
                                          @Field("mobile") String mobile,
                                          @Field("password") String password);

    @GET("api/customers?request_content=login")
    Observable<LoginResponse> login(@Query("mobile") String mobile,
                                    @Query("password") String password);
}
