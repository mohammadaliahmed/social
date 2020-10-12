package com.appsinventiv.social.NetworkResponses;

import com.appsinventiv.social.Models.CommentsModel;
import com.appsinventiv.social.Models.UserModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddCommentResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("comment")
    @Expose
    private CommentsModel comment = null;
    @SerializedName("user")
    @Expose
    private UserModel user = null;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
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

    public CommentsModel getComment() {
        return comment;
    }

    public void setComment(CommentsModel comment) {
        this.comment = comment;
    }
}
