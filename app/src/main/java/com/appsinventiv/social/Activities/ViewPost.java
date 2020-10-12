package com.appsinventiv.social.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.social.Activities.UserManagement.LoginActivity;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.NetworkResponses.AllStoriesResponse;
import com.appsinventiv.social.NetworkResponses.PostResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPost extends AppCompatActivity {


    int postId;
    CircleImageView postByPic;
    TextView postByName;
    ImageView mainImage;
    TextView lastComment, commentsCount;
    CircleImageView commenterImg;
    RelativeLayout commentsArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        postId = getIntent().getIntExtra("postId", 0);


        commentsArea = findViewById(R.id.commentsArea);
        postByPic = findViewById(R.id.postByPic);
        postByName = findViewById(R.id.postByName);
        mainImage = findViewById(R.id.mainImage);
        lastComment = findViewById(R.id.lastComment);
        commentsCount = findViewById(R.id.commentsCount);
        commenterImg = findViewById(R.id.commenterImg);

        Glide.with(this).load(AppConfig.BASE_URL_Image + SharedPrefs.getUserModel().getThumbnailUrl()).into(commenterImg);

//        getDataFromServer();


    }

//    private void getDataFromServer() {
//        JsonObject map = new JsonObject();
//        map.addProperty("api_username", AppConfig.API_USERNAME);
//        map.addProperty("api_password", AppConfig.API_PASSOWRD);
//        map.addProperty("post_id", postId);
//        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
//        Call<PostResponse> call = getResponse.getPost(map);
//        call.enqueue(new Callback<PostResponse>() {
//            @Override
//            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
//                if (response.code() == 200) {
//                    PostModel model = response.body().getPosts();
//                    if (model.getLastComment().size() > 0) {
//                        String sourceString = "<b>" + model.getLastComment().get(0).getName() + "</b> " + model.getLastComment().get(0).getText();
//                        lastComment.setText(Html.fromHtml(sourceString));
//                        lastComment.setVisibility(View.VISIBLE);
//
//                    } else {
//
//                        lastComment.setVisibility(View.GONE);
//
//                    }
//                    commentsCount.setText("View " + model.getCommentsCount() + " comments");
//
//                } else {
//                    CommonUtils.showToast(response.message());
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PostResponse> call, Throwable t) {
//                CommonUtils.showToast(t.getMessage());
//            }
//        });
//    }


}
