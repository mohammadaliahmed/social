package com.appsinventiv.social.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.appsinventiv.social.Adapters.MyFriendsListAdapter;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.NetworkResponses.AllFriendsResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
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

public class MyListOfFriends extends AppCompatActivity {


    RecyclerView recyclerview;
    MyFriendsListAdapter adapter;
    private List<UserModel> itemList = new ArrayList<>();

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

        recyclerview = findViewById(R.id.recyclerview);
//        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerview.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new MyFriendsListAdapter(this, itemList);
        recyclerview.setAdapter(adapter);

        getDataFromServer();

    }

    private void getDataFromServer() {

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AllFriendsResponse> call = getResponse.getMyFriends(map);
        call.enqueue(new Callback<AllFriendsResponse>() {
            @Override
            public void onResponse(Call<AllFriendsResponse> call, Response<AllFriendsResponse> response) {
                if (response.code() == 200) {
                    itemList = response.body().getFriends();
                    if (itemList != null && itemList.size() > 0) {
                        adapter.setItemList(itemList);
                    }
                }
            }

            @Override
            public void onFailure(Call<AllFriendsResponse> call, Throwable t) {

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
