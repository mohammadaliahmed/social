package com.appsinventiv.social;

import com.appsinventiv.social.Activities.Comments.CommentsActivity;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.ApplicationClass;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.Constants;
import com.appsinventiv.social.Utils.NotificationAsync;
import com.appsinventiv.social.Utils.NotificationObserver;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonFunctions {
    public static void likePost(PostModel model) {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("postId", model.getId());
        map.addProperty("userId", SharedPrefs.getUserModel().getId());
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ResponseBody> call = getResponse.likeUnlikePost(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    sendNotification(model.getUserModel().getFcmKey(), "New Like", SharedPrefs.getUserModel().getName() + " liked your post", model.getId());
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());

            }
        });
    }

    public static void sendNotification(String to, String title, String message, Integer id) {
        NotificationAsync notificationAsync = new NotificationAsync(ApplicationClass.getInstance().getApplicationContext(), new NotificationObserver() {
            @Override
            public void onSuccess(String chatId) {

            }

            @Override
            public void onFailure() {

            }
        });

        notificationAsync.execute(
                "ali",
                to,
                title,
                message,
                Constants.NOTIFICATION_LIKE,
                "" + id
        );
    }
}
