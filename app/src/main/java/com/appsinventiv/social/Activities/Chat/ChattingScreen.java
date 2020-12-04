package com.appsinventiv.social.Activities.Chat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.social.Activities.OtherUser.ViewProfile;
import com.appsinventiv.social.Models.MessageModel;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.NetworkResponses.AllRoomMessagesResponse;
import com.appsinventiv.social.NetworkResponses.NewMessageResponse;
import com.appsinventiv.social.NetworkResponses.UserProfileResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.CompressImage;
import com.appsinventiv.social.Utils.Constants;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChattingScreen extends AppCompatActivity {


    Integer roomId;
    String name;
    EditText message;
    ImageView send;
    RecyclerView recycler;
    MessagesAdapter adapter;
    private List<MessageModel> itemList = new ArrayList<>();
    ImageView pickGallery;

    private static final int REQUEST_CODE_CHOOSE = 23;
    private List<String> mSelected = new ArrayList<>();
    private String compressedUrl;
    private String liveUrl;
    CircleImageView image;
    TextView chatterName;
    ImageView back;
    private Integer hisUserId;
    private boolean callededUserApi;
    private UserModel hisUserModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_screen);
        LocalBroadcastManager.getInstance(ChattingScreen.this).registerReceiver(mMessageReceiver,
                new IntentFilter("newMsg"));

        getPermissions();
        name = getIntent().getStringExtra("name");


        image = findViewById(R.id.image);
        chatterName = findViewById(R.id.chatterName);
        back = findViewById(R.id.back);
        pickGallery = findViewById(R.id.pickGallery);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new MessagesAdapter(this, itemList);
        recycler.setAdapter(adapter);

        chatterName.setText(name);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pickGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMatisse();
            }
        });

        roomId = getIntent().getIntExtra("roomId", 0);
//        CommonUtils.showToast("" + roomId);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChattingScreen.this, ViewProfile.class);
                i.putExtra("userId", hisUserId);
                startActivity(i);
            }
        });
        chatterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChattingScreen.this, ViewProfile.class);
                i.putExtra("userId", hisUserId);
                startActivity(i);
            }
        });

        send = findViewById(R.id.send);
        message = findViewById(R.id.message);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().length() == 0) {
                    message.setError("Cant send empty message");
                } else {
                    sendMessage(Constants.MESSAGE_TYPE_TEXT);
                }
            }
        });
        getRoomMessagesFromDB();
        getOtherUserFromRoomId(roomId);


    }

    private void initMatisse() {
        Options options = Options.init()
                .setRequestCode(REQUEST_CODE_CHOOSE)                                           //Request code for activity results
                .setCount(1)                                                   //Number of images to restict selection count
                .setExcludeVideos(true)                                       //Option to exclude videos
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                ;                                       //Custom Path For media Storage

        Pix.start(this, options);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 23) {
            if (data != null) {
               mSelected = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

                CompressImage compressImage = new CompressImage(this);
                compressedUrl = compressImage.compressImage("" + mSelected.get(0));
                uploadImageToServer();

            }

        }
    }

    private void uploadImageToServer() {
        CommonUtils.showToast("Sending image...");
        // create upload service client
        File file = new File(compressedUrl);

        UserClient service = AppConfig.getRetrofit().create(UserClient.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        // finally, execute the request
        Call<ResponseBody> call = service.uploadFile(fileToUpload, filename);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {

                    try {
                        liveUrl = response.body().string();
                        sendMessage(Constants.MESSAGE_TYPE_IMAGE);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    CommonUtils.showToast(response.body().getUrl());
                } else {
//                    CommonUtils.showToast(response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                CommonUtils.showToast(t.getMessage());
            }
        });


    }
//

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            getRoomMessagesFromDB();
        }
    };

    private void getRoomMessagesFromDB() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("roomId", roomId);
        Call<AllRoomMessagesResponse> call = getResponse.allRoomMessages(map);
        call.enqueue(new Callback<AllRoomMessagesResponse>() {
            @Override
            public void onResponse(Call<AllRoomMessagesResponse> call, Response<AllRoomMessagesResponse> response) {
                if (response.isSuccessful()) {

                    AllRoomMessagesResponse object = response.body();
                    if (object.getMessages() != null && object.getMessages().size() > 0) {
                        itemList = object.getMessages();
                        Collections.sort(itemList, new Comparator<MessageModel>() {
                            @Override
                            public int compare(MessageModel listData, MessageModel t1) {
                                Long ob1 = listData.getTime();
                                Long ob2 = t1.getTime();

                                return ob1.compareTo(ob2);

                            }
                        });


                        adapter.setItemList(itemList);
                        recycler.scrollToPosition(itemList.size() - 1);

//                        for (MessageModel messageModel : itemList) {
//                            if (messageModel.getMessageById() != SharedPrefs.getUserModel().getId()) {
//                                hisUserId = messageModel.getMessageById();
//                                if (!callededUserApi) {
//                                    callUserApi(hisUserId);
//                                }
//                                return;
//                            }
//
//                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<AllRoomMessagesResponse> call, Throwable t) {

            }
        });
    }

    private void getOtherUserFromRoomId(int roomId) {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", roomId);
        map.addProperty("myId", SharedPrefs.getUserModel().getId());
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<UserProfileResponse> call = getResponse.getOtherUserFromRoomId(map);
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        callededUserApi = true;
                        hisUserModel = response.body().getUserModel();
                        hisUserId=hisUserModel.getId();
                        chatterName.setText(hisUserModel.getName());
                        try {
                            Glide.with(ChattingScreen.this).load(AppConfig.BASE_URL_Image + hisUserModel.getThumbnailUrl()).into(image);

                        } catch (Exception e) {

                        }
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


    private void sendMessage(String messageType) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("messageText", message.getText().toString());
        map.addProperty("messageType", messageType);
        map.addProperty("imageUrl", liveUrl);
        map.addProperty("messageByName", SharedPrefs.getUserModel().getName());
        map.addProperty("messageById", SharedPrefs.getUserModel().getId());
        map.addProperty("hisUserId",hisUserId);
        map.addProperty("roomId", roomId);
        map.addProperty("time", System.currentTimeMillis());
        Call<NewMessageResponse> call = getResponse.createMessage(map);
        call.enqueue(new Callback<NewMessageResponse>() {
            @Override
            public void onResponse(Call<NewMessageResponse> call, Response<NewMessageResponse> response) {
                if (response.code() == 200) {
                    message.setText("");

                    NewMessageResponse object = response.body();
                    if (object.getMessages() != null && object.getMessages().size() > 0) {
                        itemList = object.getMessages();
                        Collections.sort(itemList, new Comparator<MessageModel>() {
                            @Override
                            public int compare(MessageModel listData, MessageModel t1) {
                                Long ob1 = listData.getTime();
                                Long ob2 = t1.getTime();

                                return ob1.compareTo(ob2);

                            }
                        });
                        adapter.setItemList(itemList);
                        recycler.scrollToPosition(itemList.size() - 1);

                    }
                }
            }

            @Override
            public void onFailure(Call<NewMessageResponse> call, Throwable t) {

                CommonUtils.showToast(t.getMessage());

            }
        });

    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        LocalBroadcastManager.getInstance(ChattingScreen.this).unregisterReceiver(mMessageReceiver);

    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,


        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
        }
    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {

                }
            }
        }
        return true;
    }

}
