package com.wafflestudio.snutt;

import android.telecom.Call;

import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.model.Table;
import com.wafflestudio.snutt.model.TagList;
import com.wafflestudio.snutt.model.Token;
import com.wafflestudio.snutt.model.User;

import java.net.ResponseCache;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by makesource on 2016. 1. 16..
 */
public interface SNUTTRestApi {

    // API Basics and Auth

    @POST("/auth/register_local")
    public void postSignUp(@Body Map query, Callback<Response> callback);

    @POST("/auth/login_local")
    public void postSignIn(@Body Map query, Callback<Token> callback);

    @POST("/search_query")
    public void postSearchQuery(@Body Map query, Callback<List<Lecture>> callback);

    // API Timetable

    @GET("/tables")
    public void getTableList(@Header("x-access-token") String token, Callback<List<Table>> callback);

    @POST("/tables")
    public void postTable(@Header("x-access-token") String token, @Body Map query, Callback<List<Table>> callback);

    @GET("/tables/{id}")
    public void getTableById(@Header("x-access-token") String token, @Path("id") String id, Callback<Table> callback);

    @GET("/tables/recent")
    public void getRecentTable(@Header("x-access-token") String token, Callback<Table> callback);

    @POST("/tables/{id}/lecture")
    public void postLecture(@Header("x-access-token") String token, @Path("id") String id, @Body Lecture lecture, Callback<Table> callback);

    @DELETE("/tables/{id}/lecture/{lecture_id}")
    public void deleteLecture(@Header("x-access-token") String token, @Path("id") String id, @Path("lecture_id") String lecture_id, Callback<Table> callback);

    @PUT("/tables/{id}/lecture/{lecture_id}")
    public void putLecture(@Header("x-access-token") String token, @Path("id") String id, @Path("lecture_id") String lecture_id, @Body Lecture lecture, Callback<Table> callback);

    @GET("/tags/{year}/{semester}")
    public void getTagList(@Path("year") int year, @Path("semester") int semester, Callback<TagList> callback);

    // API for User

    @GET("/user/info")
    public void getUserInfo(@Header("x-access-token") String token, Callback<User> callback);

    @PUT("/user/info")
    public void putUserInfo(@Header("x-access-token") String token, @Body Map query, Callback<Response> callback);

    @PUT("/user/password")
    public void putUserPassword(@Header("x-access-token") String token, @Body Map query, Callback<Token> callback);

    @POST("/user/password")
    public void postUserPassword(@Header("x-access-token") String token, @Body Map query, Callback<Response> callback);

    @POST("/user/facebook")
    public void postUserFacebook(@Header("x-access-token") String token, @Body Map query, Callback<Response> callback);

    @DELETE("/user/facebook")
    public void deleteUserFacebook(@Header("x-access-token") String token, @Body Map query, Callback<Response> callback);

    @GET("/user/facebook")
    public void getUserFacebook(@Header("x-access-token") String token, @Body Map query, Callback<Response> callback);

    @POST("/user/device")
    public void registerToken(@Header("x-access-token") String token, @Body String fToken, Callback<Response> callback);

    @DELETE("/user/device")
    public void deleteToken(@Header("x-access-token") String token, @Body String fToken, Callback<Response> callback);

    @DELETE("/user/account")
    public void deleteUserAccount(@Header("x-access-token") String token, Callback<Response> callback);

    // API for Notification

    @GET("/notification")
    public void getNotification(@Header("x-access-token") String token, @Body Map query, Callback<Response> callback);

    @GET("/notification/count")
    public void getNotificationCount(@Header("x-access-token") String token, Callback<Response> callback);

}
