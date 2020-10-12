package com.appsinventiv.social.NetworkResponses;

import com.appsinventiv.social.Models.StoryModel;
import com.appsinventiv.social.Models.StoryViewsModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllStoriesResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("stories")
    @Expose
    private List<StoryModel> posts = null;
    @SerializedName("storyViews")
    @Expose
    private List<StoryViewsModel> storyViews = null;

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

    public List<StoryModel> getPosts() {
        return posts;
    }

    public void setPosts(List<StoryModel> posts) {
        this.posts = posts;
    }

    public List<StoryViewsModel> getStoryViews() {
        return storyViews;
    }

    public void setStoryViews(List<StoryViewsModel> storyViews) {
        this.storyViews = storyViews;
    }
}
