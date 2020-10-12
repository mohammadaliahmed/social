package com.appsinventiv.social.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.social.Adapters.LikesListAdapter;
import com.appsinventiv.social.Adapters.UserListAdapter;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.NetworkResponses.AllPostsResponse;
import com.appsinventiv.social.NetworkResponses.AllUsersResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikesList extends AppCompatActivity {


    int postId;
    RecyclerView recyclerview;
    LikesListAdapter adapter;
    private List<UserModel> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Likes");

        postId = getIntent().getIntExtra("postId", 0);

        recyclerview = findViewById(R.id.recyclerview);

        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new LikesListAdapter(this, itemList);

        recyclerview.setAdapter(adapter);

        getDataFromServer();

    }

    private void getDataFromServer() {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", postId);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AllUsersResponse> call = getResponse.getUsersByPostLikes(map);
        call.enqueue(new Callback<AllUsersResponse>() {
            @Override
            public void onResponse(Call<AllUsersResponse> call, Response<AllUsersResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getUsers() != null) {
                        adapter.setItemList(response.body().getUsers());
                    }
                } else {

                }

            }

            @Override
            public void onFailure(Call<AllUsersResponse> call, Throwable t) {

            }
        });

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