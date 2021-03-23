package com.appsinventiv.social.Activities.OtherUser;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.social.Activities.Chat.ChattingScreen;
import com.appsinventiv.social.Activities.HisListOfFriends;
import com.appsinventiv.social.Activities.HomeManagement.HomeFragment;
import com.appsinventiv.social.Activities.HomeManagement.UserPostsActivity;
import com.appsinventiv.social.Interfaces.PictureClickCallbacks;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.Models.Room;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.NetworkResponses.AllPostsResponse;
import com.appsinventiv.social.NetworkResponses.CreateRoomResponse;
import com.appsinventiv.social.NetworkResponses.UserProfileResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.ApplicationClass;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.Constants;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.bumptech.glide.Glide;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProfile extends AppCompatActivity {
    CircleImageView profilePic;
    TextView name;
    RecyclerView recyclerview;
    UserProfilePostsAdapter adapter;
    private List<PostModel> itemList = new ArrayList<>();
    int userId;
    TextView postCount, friendsCount;
    TextView button;
    int whatToDo = 0;
    TextView message;
    private UserModel user;
    LinearLayout frndssss;
    String accept_request;
    private UserModel my_user;
    RelativeLayout wholeLayout;
    AdView mAdView;
    LinearLayout privateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("");
        userId = getIntent().getIntExtra("userId", 0);

        mAdView = findViewById(R.id.mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        wholeLayout = findViewById(R.id.wholeLayout);
        privateLayout = findViewById(R.id.privateLayout);
        message = findViewById(R.id.message);
        profilePic = findViewById(R.id.profilePic);
        recyclerview = findViewById(R.id.recyclerview);
        friendsCount = findViewById(R.id.friendsCount);
        postCount = findViewById(R.id.postCount);
        frndssss = findViewById(R.id.frndssss);
        name = findViewById(R.id.name);
        button = findViewById(R.id.button);
        adapter = new UserProfilePostsAdapter(this, itemList, new PictureClickCallbacks() {
            @Override
            public void onPictureClicked(int position) {
                Constants.PICTURE_POSITION = position;
                startActivity(new Intent(ViewProfile.this, UserPostsActivity.class));
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRoomInDb(user);

            }
        });

        recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerview.setAdapter(adapter);
        getDataFromServer();
        frndssss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewProfile.this, HisListOfFriends.class);
                i.putExtra("userId", userId);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whatToDo == 0) {
                    sendFriendRequest();
                } else if (whatToDo == 1) {
                    showAlert();
                } else if (whatToDo == 2) {
                    showAlert();
                } else if (whatToDo == 3) {
                    acceptRequest();
                }
            }
        });


    }

    private void createRoomInDb(final UserModel model) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("userIds", SharedPrefs.getUserModel().getId() + "," + model.getId());

        Call<CreateRoomResponse> call = getResponse.createRoom(map);
        call.enqueue(new Callback<CreateRoomResponse>() {
            @Override
            public void onResponse(Call<CreateRoomResponse> call, Response<CreateRoomResponse> response) {
                if (response.code() == 200) {
                    CreateRoomResponse object = response.body();
                    if (object != null) {
                        if (object.getMessage().equalsIgnoreCase("Room Already Exists")) {
                            Room room = response.body().getRoom();
                            Intent i = new Intent(ViewProfile.this, ChattingScreen.class);
                            i.putExtra("roomId", room.getId());
                            i.putExtra("name", model.getName());
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                            startActivity(i);
                        } else {
                            Room room = object.getRoom();
                            Intent i = new Intent(ViewProfile.this, ChattingScreen.class);
                            i.putExtra("roomId", room.getId());
                            i.putExtra("name", model.getName());
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(i);
                        }
                    } else {
                        CommonUtils.showToast("Room Null");
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<CreateRoomResponse> call, Throwable t) {

            }
        });
    }


    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Remove " + user.getName() + " as friend? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removeAsFriend();

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeAsFriend() {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("his_id", userId);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ResponseBody> call = getResponse.removeAsFriend(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    CommonUtils.showToast("Removed as friend");
                    whatToDo = 0;
                    button.setBackground(getResources().getDrawable(R.drawable.curved_corners_colored));
                    button.setTextColor(getResources().getColor(R.color.white));
                    button.setText("Add As Friend");
                    itemList.clear();
                    adapter.setItemList(itemList);

                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());

            }
        });

    }

    private void sendFriendRequest() {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("his_id", userId);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ResponseBody> call = getResponse.sendFriendRequest(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    CommonUtils.showToast("Request Sent");
                    whatToDo = 2;
                    button.setBackground(getResources().getDrawable(R.drawable.grey_corners));
                    button.setTextColor(getResources().getColor(R.color.black));
                    button.setText("Request Sent");
                    if (user.getType() == 1) {
                        getDataFromServer();
                    }

                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());

            }
        });


    }

    private void acceptRequest() {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("his_id", userId);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ResponseBody> call = getResponse.acceptRequest(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    CommonUtils.showToast("Request Accepted");
                    whatToDo = 1;
                    button.setBackground(getResources().getDrawable(R.drawable.grey_corners));
                    button.setTextColor(getResources().getColor(R.color.black));
                    button.setText("Friend");
                    getPostsDataFromServer();
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());

            }
        });


    }


    private void getDataFromServer() {
        wholeLayout.setVisibility(View.VISIBLE);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("his_id", userId);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<UserProfileResponse> call = getResponse.userProfile(map);
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        user = response.body().getUserModel();
                        my_user = response.body().getMy_user();
                        SharedPrefs.setUserModel(my_user);
                        setupUi(user, response.body().getFriendCount());
                    } else {
                        CommonUtils.showToast("Error");
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });


    }

    private void getPostsDataFromServer() {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", userId);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AllPostsResponse> call = getResponse.getUserPosts(map);
        call.enqueue(new Callback<AllPostsResponse>() {
            @Override
            public void onResponse(Call<AllPostsResponse> call, Response<AllPostsResponse> response) {
                if (response.code() == 200) {
                    List<PostModel> data = response.body().getPosts();
                    HomeFragment.likesList = response.body().getLikesList() == null ? new ArrayList<>() : response.body().getLikesList();

                    if (data != null && data.size() > 0) {
                        itemList = data;
                        adapter.setItemList(itemList);
                        setupCache();
                        SharedPrefs.setPosts(itemList);

                    }
                    postCount.setText(itemList.size() + "");


                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<AllPostsResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });


    }

    private void setupCache() {

        for (int i = 0; i < itemList.size(); i++) {

        }
    }


    private void setupUi(UserModel userModel, int friendCount) {
        Glide.with(this).load(AppConfig.BASE_URL_Image + userModel.getThumbnailUrl()).placeholder(R.drawable.ic_profile_plc).into(profilePic);
        name.setText(userModel.getName());
        friendsCount.setText("" + friendCount);
        this.setTitle(userModel.getName());


        if (SharedPrefs.getUserModel().getFriendsList() != null) {
            if (SharedPrefs.getUserModel().getFriendsList().contains(userId)) {
                whatToDo = 1;//friend
                getPostsDataFromServer();
            } else if (SharedPrefs.getUserModel().getRequestSentList().contains(userId)) {
                whatToDo = 2;//request sent
                if (userModel.getType() == 1) {
                    getPostsDataFromServer();
                } else {
                    privateLayout.setVisibility(View.VISIBLE);
                }
            } else if (SharedPrefs.getUserModel().getRequestsReceivedList().contains(userId)) {
                whatToDo = 3;//accept request
                if (userModel.getType() == 1) {
                    getPostsDataFromServer();
                }
            } else {
                whatToDo = 0;//add as friend
                if (userModel.getType() == 1) {
                    getPostsDataFromServer();
                } else {
                    privateLayout.setVisibility(View.VISIBLE);
                }
            }


        }
        if (getIntent().getStringExtra("accept_request") != null) {

            if (getIntent().getStringExtra("accept_request").equalsIgnoreCase("accept_request")) {
                whatToDo = 3;
            }
        }


        if (whatToDo == 0) {
            button.setBackground(getResources().getDrawable(R.drawable.curved_corners_colored));
            button.setTextColor(getResources().getColor(R.color.white));
            button.setText("Add As Friend");

        } else if (whatToDo == 1) {
            button.setBackground(getResources().getDrawable(R.drawable.grey_corners));
            button.setTextColor(getResources().getColor(R.color.black));
            button.setText("Friend");
        } else if (whatToDo == 2) {
            button.setBackground(getResources().getDrawable(R.drawable.grey_corners));
            button.setTextColor(getResources().getColor(R.color.black));
            button.setText("Request Sent");
        } else if (whatToDo == 3) {
            button.setBackground(getResources().getDrawable(R.drawable.curved_corners_green));
            button.setTextColor(getResources().getColor(R.color.white));
            button.setText("Accept Request");
        }
        wholeLayout.setVisibility(View.GONE);


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
