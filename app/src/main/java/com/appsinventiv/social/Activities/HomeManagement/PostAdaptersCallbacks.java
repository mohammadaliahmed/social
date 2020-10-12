package com.appsinventiv.social.Activities.HomeManagement;

import com.appsinventiv.social.Models.PostModel;

public interface PostAdaptersCallbacks {
    public void takeUserToMyUserProfile(int userId);

    public void takeUserToOtherUserProfile(int userId);

    public void takeUserToLikesScreen(int postId);

    public void onLikedPost(PostModel model);

    public void onUnlikedPost(PostModel model);

    public void onFileDownload(String filename);

    public void onDelete(PostModel model, int position);

    public void onMutePost(PostModel model);

    public void onUnMutePost(PostModel model);

    public void onSharePostWithFriends(PostModel model);

    public void onRePost(PostModel model);

    public void onShowDownloadMenu(PostModel model);
}
