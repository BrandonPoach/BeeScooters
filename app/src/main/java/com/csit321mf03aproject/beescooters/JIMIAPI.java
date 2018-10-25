package com.csit321mf03aproject.beescooters;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

//class that is part of GPS integration, work in progress for Tradeshow
public interface JIMIAPI {

    @FormUrlEncoded
    @POST("route/rest")
    Call<AccessResult> gpsResult(@Field("method") String method, @Field("timestamp") String timestamp,
                           @Field("app_key") String app_key, @Field("sign") String sign,
                           @Field("sign_method") String  sign_method, @Field("v") String v,
                           @Field("format") String format, @Field("user_id") String user_id,
                           @Field("user_pwd_md5") String user_pwd_md5,@Field("expires_in") String expires_in);

    @FormUrlEncoded
    @POST("route/rest")
    Call<imeiLocation> coordinateResult(@Field("method") String method, @Field("timestamp") String timestamp,
                                 @Field("app_key") String app_key, @Field("sign") String sign,
                                 @Field("sign_method") String  sign_method, @Field("v") String v,
                                 @Field("format") String format, @Field("imeis") String imeis, @Field("access_token")
                                 String access_token);
}
