package com.appsinventiv.social.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.appsinventiv.social.Adapters.UserListAdapter;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.NetworkResponses.HisFriendsListResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HisListOfFriends extends AppCompatActivity {


    RecyclerView recyclerview;
    UserListAdapter adapter;
    private List<UserModel> itemList = new ArrayList<>();
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_friends);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Friends");
        userId = getIntent().getIntExtra("userId", 0);

        recyclerview = findViewById(R.id.recyclerview);
//        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerview.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new UserListAdapter(this, itemList);
        recyclerview.setAdapter(adapter);

        getDataFromServer();

    }

    private void getDataFromServer() {

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("his_id", userId);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<HisFriendsListResponse> call = getResponse.getHisFriends(map);
        call.enqueue(new Callback<HisFriendsListResponse>() {
            @Override
            public void onResponse(Call<HisFriendsListResponse> call, Response<HisFriendsListResponse> response) {
                if (response.code() == 200) {
                    itemList = response.body().getHisFriends();

                    if (itemList != null && itemList.size() > 0) {
                        adapter.setItemList(itemList);

                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<HisFriendsListResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());

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
