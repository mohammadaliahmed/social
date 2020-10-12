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

public class RegisterActivity extends AppCompatActivity {

    RadioButton male, female;
    EditText name, email, username, password, phone;
    String gender = "Male";
    Button signup;
    RelativeLayout wholeLayout;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setElevation(0);


        login = findViewById(R.id.login);
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        signup = findViewById(R.id.signup);
        wholeLayout = findViewById(R.id.wholeLayout);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });


        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = "Male";
                }
            }
        });
        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = "Female";
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() == 0) {
                    name.setError("Enter name");
                } else if (username.getText().length() == 0) {
                    username.setError("Enter username");
                } else if (email.getText().length() == 0) {
                    email.setError("Enter email");
                } else if (!email.getText().toString().contains("@")) {
                    email.setError("Enter valid email");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else if (password.getText().length() < 5) {
                    password.setError("Enter atleast 5 characters");
                } else if (phone.getText().length() == 0) {
                    phone.setError("Enter phone");
                } else if (phone.getText().length() < 8) {
                    phone.setError("Enter valid phone");
                } else {
                    singupNow();
                }

            }
        });
    }

    private void singupNow() {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<SignupResponse> call = getResponse.register(
                AppConfig.API_USERNAME, AppConfig.API_PASSOWRD,
                name.getText().toString(),
                username.getText().toString(),
                email.getText().toString(),
                password.getText().toString(),
                phone.getText().toString(),
                gender
        );
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.body().getCode() == 403) {
                    CommonUtils.showToast(response.body().getMessage());
                } else if (response.body().getCode() == 302) {
                    CommonUtils.showToast(response.body().getMessage());
                } else if (response.body().getCode() == 404) {
                    CommonUtils.showToast(response.body().getMessage());
                } else if (response.body().getCode() == 200) {
                    SignupResponse object = response.body();
                    if (object != null && object.getUser() != null) {
                        UserModel user = object.getUser();
                        CommonUtils.showToast("Registration successful");
                        SharedPrefs.setUserModel(user);
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                wholeLayout.setVisibility(View.GONE);
                CommonUtils.showToast(t.getMessage());
            }
        });

    }
}
