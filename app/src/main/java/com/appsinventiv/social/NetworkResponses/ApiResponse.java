package com.appsinventiv.social.NetworkResponses;

import com.appsinventiv.social.Models.CommentsModel;
import com.appsinventiv.social.Models.NotificationModel;
import com.appsinventiv.social.Models.UserModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user")
    @Expose
    private UserModel user;

    @SerializedName("notifications")
    @Expose
    private List<NotificationModel> notificationList;

    @SerializedName("comment")
    @Expose
    private CommentsModel comment = null;

    public CommentsModel getComment() {
        return comment;
    }

    public void setComment(CommentsModel comment) {
        this.comment = comment;
    }

    public List<NotificationModel> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<NotificationModel> notificationList) {
        this.notificationList = notificationList;
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

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
