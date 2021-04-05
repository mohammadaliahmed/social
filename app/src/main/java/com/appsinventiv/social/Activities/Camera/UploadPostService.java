package com.appsinventiv.social.Activities.Camera;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.appsinventiv.social.Activities.MainActivity;
import com.appsinventiv.social.NetworkResponses.AddPostResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadPostService extends Service {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    int uploadCount = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent resultIntent = null;
        uploadCount = 0;
        resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Uploading Post..")
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.enableVibration(true);
//            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
//        mNotificationManager.notify(num /* Request Code */, mBuilder.build());
        Notification notification = mBuilder.build();
        startForeground(101, notification);

        uploadFile(PhotoRedirectActivity.imagess.get(uploadCount));

        return Service.START_STICKY;

    }

    private void uploadFile(String abc) {
        // create upload service client
        File file = new File(abc);

        UserClient service = AppConfig.getRetrofit().create(UserClient.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody token = RequestBody.create(
                MediaType.parse("text/plain"), "token_string");

        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
        RequestBody username = RequestBody.create(MediaType.parse("text/plain"), SharedPrefs.getUserModel().getUsername());
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("token", token);
        map.put("photo", requestBody);
        map.put("username", username);
        // finally, execute the request
        Call<ResponseBody> call = service.uploadFile1(fileToUpload, map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        String liveUrl = response.body().string();
                        if (PhotoRedirectActivity.liveUrls == null) {
                            PhotoRedirectActivity.liveUrls = liveUrl;
                        } else {
                            PhotoRedirectActivity.liveUrls = PhotoRedirectActivity.liveUrls + "," + liveUrl;
                        }
                        uploadCount++;
                        if (uploadCount < PhotoRedirectActivity.imagess.size()) {

                            uploadFile(PhotoRedirectActivity.imagess.get(uploadCount));
                        } else {
                            sendMessage();
//                            CommonUtils.showToast("All Uploaded");
                        }


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

    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("uploaded");
        // You can also include some extra data.
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        super.onTaskRemoved(rootIntent);
    }

}
