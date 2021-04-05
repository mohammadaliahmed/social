package com.appsinventiv.social.NetworkResponses;

import com.appsinventiv.social.Models.CommentsModel;
import com.appsinventiv.social.Models.NotificationModel;
import com.appsinventiv.social.Models.PostModel;
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
    @SerializedName("posts")
    @Expose
    private List<PostModel> posts = null;
    @SerializedName("post")
    @Expose
    private PostModel post = null;

    @SerializedName("likes")
    @Expose
    private List<Integer> likesList = null;

    public List<Integer> getLikesList() {
        return likesList;
    }

    public void setLikesList(List<Integer> likesList) {
        this.likesList = likesList;
    }

    public PostModel getPost() {
        return post;
    }

    public void setPost(PostModel post) {
        this.post = post;
    }

    public List<PostModel> getPosts() {
        return posts;
    }

    public void setPosts(List<PostModel> posts) {
        this.posts = posts;
    }

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
