package com.appsinventiv.social.Activities.HomeManagement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.appsinventiv.social.Activities.MainActivity;
import com.appsinventiv.social.Activities.OtherUser.ViewProfile;
import com.appsinventiv.social.Activities.Stories.HomeStoriesAdapter;
import com.appsinventiv.social.Activities.UserManagement.LoginActivity;
import com.appsinventiv.social.CommonFunctions;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.Constants;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPostsActivity extends AppCompatActivity {
    PostsAdapter adapter;

    private List<PostModel> itemList = new ArrayList<>();
    private RecyclerView recycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);
        recycler = findViewById(R.id.my_fancy_videos);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);

        }
        this.setTitle("");
        itemList = SharedPrefs.getPosts();
        adapter = new PostsAdapter(this, itemList, new PostAdaptersCallbacks() {
            @Override
            public void takeUserToMyUserProfile(int userId) {
//                MyProfileFragment fragment = new MyProfileFragment();
//                loadFragment(fragment);
            }

            @Override
            public void takeUserToOtherUserProfile(int userId) {
//                Intent i = new Intent(UserPostsActivity.this, ViewProfile.class);
//                i.putExtra("userId", userId);
//                startActivity(i);
            }

            @Override
            public void takeUserToLikesScreen(int postId) {

            }

            @Override
            public void onLikedPost(PostModel model) {
                CommonFunctions.likePost(model);
            }

            @Override
            public void onUnlikedPost(PostModel model) {
                CommonFunctions.likePost(model);
            }

            @Override
            public void onFileDownload(String filename) {

            }

            @Override
            public void onDelete(PostModel model, int position) {
                showDeleteAlert(model.getId(), position);

            }

            @Override
            public void onMutePost(PostModel model) {

            }

            @Override
            public void onUnMutePost(PostModel model) {

            }

            @Override
            public void onSharePostWithFriends(PostModel model) {

            }

            @Override
            public void onRePost(PostModel model) {

            }

            @Override
            public void onShowDownloadMenu(PostModel model) {

            }
        });
        recycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        recycler.scrollToPosition(Constants.PICTURE_POSITION);
        this.setTitle(SharedPrefs.getPosts().get(0).getUserModel().getName());


    }

    private void callDeleteApi(Integer idd, final int positionn) {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("post_id", idd);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ResponseBody> call = getResponse.deletePost(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    CommonUtils.showToast("Post Delete");
                    itemList.remove(positionn);
//                    adapter.setDataArrayList(postList);
                    if (positionn == 0) {
//                        adapter.setDataArrayList(itemList);
                    } else {
//                        adapter.notifyItemRemoved(positionn);
                    }

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

    private void showDeleteAlert(final Integer idd, final int positionn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to delete this post? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callDeleteApi(idd, positionn);

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
