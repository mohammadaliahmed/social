package com.appsinventiv.social.Activities;

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
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.appsinventiv.social.Activities.UserManagement.LoginActivity;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.NetworkResponses.ApiResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountSettings extends AppCompatActivity {

    RelativeLayout logout;
    Switch profileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("");

//        deleteAccount = findViewById(R.id.deleteAccount);
        profileType = findViewById(R.id.profileType);
        logout = findViewById(R.id.logout);
        this.setTitle("Account Settings");

        if(SharedPrefs.getUserModel().getType()==1){
            profileType.setChecked(true);
        }else{
            profileType.setChecked(false);
        }

        profileType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    updateDataToServer(isChecked);
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefs.logout();
                Intent i = new Intent(AccountSettings.this, Splash.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();

            }
        });
    }

    private void updateDataToServer(boolean isChecked) {
        CommonUtils.showToast("Profile Updated");
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("type", isChecked ? 1 : 0);

        Call<ApiResponse> call = getResponse.updateProfileType(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    UserModel user = response.body().getUser();
                    if (user != null) {
                        SharedPrefs.setUserModel(user);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

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
