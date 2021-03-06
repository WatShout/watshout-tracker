package com.watshout.mobile;

import com.watshout.mobile.pojo.CreateRoadMap;
import com.watshout.mobile.pojo.FriendRequestList;
import com.watshout.mobile.pojo.FriendRequestResponse;
import com.watshout.mobile.pojo.FriendsList;
import com.watshout.mobile.pojo.NewsFeedList;
import com.watshout.mobile.pojo.SuccessCheck;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitInterface {

    @GET("/api/friends/{uid}/")
    Call<FriendsList> getFriendsList(
            @Path("uid") String token
    );

    @GET("/api/friendrequests/{uid}/")
    Call<FriendRequestList> getFriendRequestList(
            @Path("uid") String token
    );

    @GET("/api/newsfeed/{uid}/")
    Call<NewsFeedList> getNewsFeed(
            @Path("uid") String token
    );

    @GET("/api/history/{uid}/")
    Call<NewsFeedList> getHistory(
            @Path("uid") String token
    );

    @FormUrlEncoded
    @POST("/api/sendfriendnotification/")
    Call<FriendRequestResponse> sendFriendNotification(
            @Field("my_uid") String myUID,
            @Field("their_uid") String theirUID
    );

    @FormUrlEncoded
    @POST("/api/authorized/")
    Call<SuccessCheck> getEmailAuthorized(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("/api/activitystartnotification/")
    Call<FriendRequestResponse> sendActivityNotification(
            @Field("my_uid") String myUID
    );

    @FormUrlEncoded
    @POST("/api/createroadsnap/")
    Call<CreateRoadMap> createRoadSnap(
            @Field("coordinates") String coordinates
    );


}
