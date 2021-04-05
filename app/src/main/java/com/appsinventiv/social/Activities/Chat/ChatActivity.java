package com.appsinventiv.social.Activities.Chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.social.Activities.MainActivity;
import com.appsinventiv.social.Activities.UserManagement.LoginActivity;
import com.appsinventiv.social.Adapters.ChatListAdapter;
import com.appsinventiv.social.Models.UserMessages;
import com.appsinventiv.social.NetworkResponses.UserMessagesResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {


    RecyclerView recycler;
    ChatListAdapter adapter;
    private List<UserMessages> itemList = new ArrayList<>();
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats_fragment);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Chats");
        recycler = findViewById(R.id.recycler);
        search = findViewById(R.id.search);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ChatListAdapter(this
                , itemList);
        recycler.setAdapter(adapter);


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(s.toString());
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromServer();
    }

    private void getDataFromServer() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        Call<UserMessagesResponse> call = getResponse.userMessages(map);
        call.enqueue(new Callback<UserMessagesResponse>() {
            @Override
            public void onResponse(Call<UserMessagesResponse> call, Response<UserMessagesResponse> response) {
                if (response.isSuccessful()) {

                    UserMessagesResponse object = response.body();
                    if (object != null) {
                        itemList.clear();
                        if (object.getMessages() != null && object.getMessages().size() > 0) {
                            itemList = object.getMessages();
                        }
                        adapter.updateList(itemList);
                    }

                }
            }

            @Override
            public void onFailure(Call<UserMessagesResponse> call, Throwable t) {
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
