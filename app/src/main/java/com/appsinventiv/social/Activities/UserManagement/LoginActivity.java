package com.appsinventiv.social.Activities.UserManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.social.Activities.MainActivity;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.NetworkResponses.ApiResponse;
import com.appsinventiv.social.NetworkResponses.LoginResponse;
import com.appsinventiv.social.NetworkResponses.SignupResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    GoogleApiClient apiClient;
    GoogleSignInAccount account;
    RelativeLayout google;
    ProgressBar wholeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        google=findViewById(R.id.google);
        wholeLayout=findViewById(R.id.wholeLayout);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        apiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                startActivityForResult(i, 100);
            }
        });


//        username = findViewById(R.id.username);
//        password = findViewById(R.id.password);
//        register = findViewById(R.id.register);
//        login = findViewById(R.id.login);
//
//        wholeLayout = findViewById(R.id.wholeLayout);
//        register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
//            }
//        });
//
//
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (username.getText().length() == 0) {
//                    username.setError("Enter username");
//                } else if (password.getText().length() == 0) {
//                    password.setError("Enter password");
//                } else {
//                    loginNow();
//                }
//
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(googleSignInResult);
        }

    }

    private void handleResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()) {
            account = googleSignInResult.getSignInAccount();
            loginNow();

            Auth.GoogleSignInApi.signOut(apiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {

                }
            });


        } else {
            CommonUtils.showToast("Error");
        }
    }


    private void loginNow() {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map=new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("name",account.getDisplayName());
        map.addProperty("email",account.getEmail());
        map.addProperty("password",account.getEmail().replace("-","").replace("@",""));
        map.addProperty("picUrl",""+account.getPhotoUrl());
        Call<ApiResponse> call = getResponse.loginUser(
                map


        );
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                        UserModel user = response.body().getUser();
                        CommonUtils.showToast("Login Successful");
                        SharedPrefs.setUserModel(user);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }else{
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
}
