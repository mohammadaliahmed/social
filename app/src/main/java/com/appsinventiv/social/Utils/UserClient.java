package com.appsinventiv.social.Utils;


import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.NetworkResponses.AddCommentResponse;
import com.appsinventiv.social.NetworkResponses.AddPostResponse;
import com.appsinventiv.social.NetworkResponses.AllCommentsResponse;
import com.appsinventiv.social.NetworkResponses.AllFriendsResponse;
import com.appsinventiv.social.NetworkResponses.AllPostsResponse;
import com.appsinventiv.social.NetworkResponses.AllRoomMessagesResponse;
import com.appsinventiv.social.NetworkResponses.AllStoriesResponse;
import com.appsinventiv.social.NetworkResponses.AllUsersResponse;
import com.appsinventiv.social.NetworkResponses.ApiResponse;
import com.appsinventiv.social.NetworkResponses.CreateRoomResponse;
import com.appsinventiv.social.NetworkResponses.HisFriendsListResponse;
import com.appsinventiv.social.NetworkResponses.LoginResponse;
import com.appsinventiv.social.NetworkResponses.NewMessageResponse;
import com.appsinventiv.social.NetworkResponses.PostResponse;
import com.appsinventiv.social.NetworkResponses.SearchedUsersResponse;
import com.appsinventiv.social.NetworkResponses.SignupResponse;
import com.appsinventiv.social.NetworkResponses.UpdateFcmKeyResponse;
import com.appsinventiv.social.NetworkResponses.UploadFileResponse;
import com.appsinventiv.social.NetworkResponses.UploadProfilePictureResponse;
import com.appsinventiv.social.NetworkResponses.UserMessagesResponse;
import com.appsinventiv.social.NetworkResponses.UserProfileResponse;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface UserClient {


    @Headers("Content-Type: application/json")
    @POST("api/post/allPosts")
    Call<AllPostsResponse> allPosts(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/post/viewPost")
    Call<AllPostsResponse> viewPost(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/post/myPosts")
    Call<AllPostsResponse> myPosts(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/post/likeUnlikePost")
    Call<ResponseBody> likeUnlikePost(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/post/deletePost")
    Call<ResponseBody> deletePost(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/post/getUserPosts")
    Call<AllPostsResponse> getUserPosts(
            @Body JsonObject jsonObject

    );


    @Headers("Content-Type: application/json")
    @POST("api/post/getPost")
    Call<PostResponse> getPost(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/friends/acceptRequest")
    Call<ResponseBody> acceptRequest(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/friends/sendFriendRequest")
    Call<ResponseBody> sendFriendRequest(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/friends/removeAsFriend")
    Call<ResponseBody> removeAsFriend(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/notification/getMyNotifications")
    Call<ApiResponse> getMyNotifications(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/user/userProfile")
    Call<UserProfileResponse> userProfile(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/room/getOtherUserFromRoomId")
    Call<UserProfileResponse> getOtherUserFromRoomId(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/story/allStories")
    Call<AllStoriesResponse> allStories(
            @Body JsonObject jsonObject

    );


    @Headers("Content-Type: application/json")
    @POST("api/comments/getAllComments")
    Call<AllCommentsResponse> getAllComments(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/comments/addComment")
    Call<ApiResponse> addComment(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/story/deleteStory")
    Call<ResponseBody> deleteStory(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/views/addStoryView")
    Call<ResponseBody> addStoryView(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/friends/getMyFriends")
    Call<AllFriendsResponse> getMyFriends(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/post/getUsersByPostLikes")
    Call<AllUsersResponse> getUsersByPostLikes(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/friends/getHisFriends")
    Call<HisFriendsListResponse> getHisFriends(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/post/addPost")
    Call<AddPostResponse> addPost(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/user/updateProfilePicture")
    Call<LoginResponse> updateProfilePicture(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/story/addStory")
    Call<AddPostResponse> addStory(
            @Body JsonObject jsonObject

    );


    @Headers("Content-Type: application/json")
    @POST("api/user/updateProfileType")
    Call<ApiResponse> updateProfileType(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/user/login")
    Call<ApiResponse> loginUser(
            @Body JsonObject jsonObject

    );


    @POST("api/user/updateFcmKey")
    @FormUrlEncoded
    Call<UpdateFcmKeyResponse> updateFcmKey(
            @Field("api_username") String api_username,
            @Field("api_password") String api_password,
            @Field("id") String email,
            @Field("fcmKey") String password

    );

    @POST("api/user/register")
    @FormUrlEncoded
    Call<SignupResponse> register(
            @Field("api_username") String api_username,
            @Field("api_password") String api_password,
            @Field("name") String name,
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("gender") String gender

    );

    @POST("api/uploadFile")
    @Multipart
    Call<ResponseBody> uploadFile(
            @Part MultipartBody.Part file, @Part("photo") RequestBody name

    );

    @POST("api/uploadFile")
    @Multipart
    Call<ResponseBody> uploadVideoFile(
            @Part MultipartBody.Part file, @Part("video") RequestBody name

    );


    @Headers("Content-Type: application/json")
    @POST("api/room/createRoom")
    Call<CreateRoomResponse> createRoom(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/message/createMessage")
    Call<NewMessageResponse> createMessage(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/message/sendStoryMessage")
    Call<ApiResponse> sendStoryMessage(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/message/allRoomMessages")
    Call<AllRoomMessagesResponse> allRoomMessages(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/user/searchUsers")
    Call<SearchedUsersResponse> searchUsers(
            @Body JsonObject jsonObject

    );

    @Headers("Content-Type: application/json")
    @POST("api/message/userMessages")
    Call<UserMessagesResponse> userMessages(
            @Body JsonObject jsonObject

    );


}
