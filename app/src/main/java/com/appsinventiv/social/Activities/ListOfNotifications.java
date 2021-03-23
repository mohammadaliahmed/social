package com.appsinventiv.social.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.social.Adapters.NotificationsListAdapter;
import com.appsinventiv.social.Models.NotificationModel;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.NetworkResponses.ApiResponse;
import com.appsinventiv.social.NetworkResponses.UserProfileResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListOfNotifications extends AppCompatActivity {

    RecyclerView recycler;
    NotificationsListAdapter adapter;
    private List<NotificationModel> itemList = new ArrayList<>();

    TextView noNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_notifications);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        noNoti = findViewById(R.id.noNoti);
        recycler = findViewById(R.id.recycler);
        this.setTitle("Notifications");
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new NotificationsListAdapter(this, itemList);
        recycler.setAdapter(adapter);
        getDataFromServer();
    }

    private void getDataFromServer() {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ApiResponse> call = getResponse.getMyNotifications(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    itemList = response.body().getNotificationList();
                    if (itemList != null && itemList.size() > 0) {
                        Collections.reverse(itemList);
                        adapter.setItemList(itemList);
                        noNoti.setVisibility(View.GONE);
                    } else {
                        noNoti.setVisibility(View.VISIBLE);
                    }

                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                noNoti.setVisibility(View.VISIBLE);

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
