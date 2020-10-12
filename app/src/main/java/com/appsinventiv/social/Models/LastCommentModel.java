package com.appsinventiv.social.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastCommentModel {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("text")
    @Expose
    private String text;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
