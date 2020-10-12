package com.appsinventiv.social.Activities.UserManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.social.Activities.MainActivity;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.NetworkResponses.LoginResponse;
import com.appsinventiv.social.NetworkResponses.SignupResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    RelativeLayout wholeLayout;
    TextView register;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setElevation(0);


        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);

        wholeLayout = findViewById(R.id.wholeLayout);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().length() == 0) {
                    username.setError("Enter username");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else {
                    loginNow();
                }

            }
        });
    }

    private void loginNow() {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<LoginResponse> call = getResponse.loginUser(
                AppConfig.API_USERNAME, AppConfig.API_PASSOWRD,
                username.getText().toString(),
                password.getText().toString()

        );
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.body().getCode() == 403) {
                    CommonUtils.showToast(response.body().getMessage());
                } else if (response.body().getCode() == 302) {
                    CommonUtils.showToast(response.body().getMessage());
                } else if (response.body().getCode() == 404) {
                    CommonUtils.showToast(response.body().getMessage());
                } else if (response.body().getCode() == 200) {
                    LoginResponse object = response.body();
                    if (object != null && object.getUser() != null) {
                        UserModel user = object.getUser();
                        CommonUtils.showToast("Registration successful");
                        SharedPrefs.setUserModel(user);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                wholeLayout.setVisibility(View.GONE);
                CommonUtils.showToast(t.getMessage());
            }
        });

    }
}
