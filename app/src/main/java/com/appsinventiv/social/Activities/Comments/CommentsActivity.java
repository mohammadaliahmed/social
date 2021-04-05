package com.appsinventiv.social.Activities.Comments;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.appsinventiv.social.Activities.UserManagement.LoginActivity;
import com.appsinventiv.social.Models.CommentsModel;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.NetworkResponses.AddCommentResponse;
import com.appsinventiv.social.NetworkResponses.AllCommentsResponse;
import com.appsinventiv.social.NetworkResponses.AllStoriesResponse;
import com.appsinventiv.social.NetworkResponses.ApiResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.Constants;
import com.appsinventiv.social.Utils.NotificationAsync;
import com.appsinventiv.social.Utils.NotificationObserver;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsActivity extends AppCompatActivity implements NotificationObserver {

    CircleImageView img;
    EditText comment;
    ImageView send;
    CommentsAdapter adapter;
    RelativeLayout wholeLayout;
    RecyclerView recyclerview;
    int postId;
    private UserModel postByUser;
    private List<CommentsModel> commentsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Comments");
        img = findViewById(R.id.img);
        recyclerview = findViewById(R.id.recyclerview);
        comment = findViewById(R.id.comment);
        wholeLayout = findViewById(R.id.wholeLayout);
        img = findViewById(R.id.img);
        send = findViewById(R.id.send);

        postId = getIntent().getIntExtra("postId", 0);
        Glide.with(this).load(AppConfig.BASE_URL_Image +SharedPrefs.getUserModel().getUsername()+"/"+ SharedPrefs.getUserModel().getThumbnailUrl()).into(img);

        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new CommentsAdapter(this, commentsList);
        recyclerview.setAdapter(adapter);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment.getText().length() == 0) {
                    comment.setError("Empty");
                } else {
                    postComment(comment.getText().toString());
                }
            }
        });
        getCommentsFromDB();
    }

    private void getCommentsFromDB() {
        wholeLayout.setVisibility(View.VISIBLE);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("post_id", postId);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AllCommentsResponse> call = getResponse.getAllComments(map);
        call.enqueue(new Callback<AllCommentsResponse>() {
            @Override
            public void onResponse(Call<AllCommentsResponse> call, Response<AllCommentsResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {

                    AllCommentsResponse object = response.body();
                    if (object != null) {
                        postByUser = object.getUser();
                        commentsList = object.getComments();
                        if (commentsList != null && commentsList.size() > 0) {
                            adapter.setItemList(commentsList);

                        }
                    }
                } else {
                    wholeLayout.setVisibility(View.GONE);

                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<AllCommentsResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });
    }

    private void postComment(String comment) {
        this.comment.setText("");
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("post_id", postId);
        map.addProperty("text", comment);
        map.addProperty("time", System.currentTimeMillis());
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ApiResponse> call = getResponse.addComment(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    CommentsModel object = response.body().getComment();
                    if (object != null) {
                        commentsList.add(object);
                        adapter.setItemList(commentsList);
                        recyclerview.scrollToPosition(commentsList.size() - 1);
                        if (!SharedPrefs.getUserModel().getId().equals(postByUser.getId())) {
                            sendNotification(comment);
                        }

                    }

                } else {
                    CommonUtils.showToast(response.message());
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                wholeLayout.setVisibility(View.GONE);
                CommonUtils.showToast(t.getMessage());
            }
        });
    }

    private void sendNotification(String comment) {
        NotificationAsync notificationAsync = new NotificationAsync(CommentsActivity.this, new NotificationObserver() {
            @Override
            public void onSuccess(String chatId) {

            }

            @Override
            public void onFailure() {

            }
        });
        String NotificationTitle = "New Comment by " + SharedPrefs.getUserModel().getName();
        String NotificationMessage = "Comment: " + comment;
        notificationAsync.execute(
                "ali",
                postByUser.getFcmKey(),
                NotificationTitle,
                NotificationMessage,
                Constants.NOTIFICATION_COMMENT,
                "" + postId
        );
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

    @Override
    public void onSuccess(String chatId) {

    }

    @Override
    public void onFailure() {

    }
}
