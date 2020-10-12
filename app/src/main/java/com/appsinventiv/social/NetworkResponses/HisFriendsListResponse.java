package com.appsinventiv.social.NetworkResponses;

import com.appsinventiv.social.Models.FriendsRequestModel;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.Models.UserModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HisFriendsListResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("hisFriends")
    @Expose
    private List<UserModel> hisFriends = null;

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


    public List<UserModel> getHisFriends() {
        return hisFriends;
    }

    public void setHisFriends(List<UserModel> hisFriends) {
        this.hisFriends = hisFriends;
    }
}
