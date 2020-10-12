package com.appsinventiv.social.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;

public class FriendsRequestModel {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("user_one")
    @Expose
    private Integer userOne;
    @SerializedName("user_two")
    @Expose
    private Integer userTwo;
    @SerializedName("type")
    @Expose
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserOne() {
        return userOne;
    }

    public void setUserOne(Integer userOne) {
        this.userOne = userOne;
    }

    public Integer getUserTwo() {
        return userTwo;
    }

    public void setUserTwo(Integer userTwo) {
        this.userTwo = userTwo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
