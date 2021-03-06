package com.appsinventiv.social.NetworkResponses;

import com.appsinventiv.social.Models.NotificationModel;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.Models.UserModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserProfileResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("friendCount")
    @Expose
    private int friendCount;
    @SerializedName("user")
    @Expose
    private UserModel userModel;
    @SerializedName("my_user")
    @Expose
    private UserModel my_user;


    public UserModel getMy_user() {
        return my_user;
    }

    public void setMy_user(UserModel my_user) {
        this.my_user = my_user;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public int getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(int friendCount) {
        this.friendCount = friendCount;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
