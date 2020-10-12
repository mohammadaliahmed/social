package com.appsinventiv.social.Activities.Camera;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.appsinventiv.social.Activities.MainActivity;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.Models.StoriesPickedModel;
import com.appsinventiv.social.NetworkResponses.AddPostResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.ApplicationClass;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.CompressImage;
import com.appsinventiv.social.Utils.CustomViewPager;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.droidninja.imageeditengine.ImageEditActivity;
import com.droidninja.imageeditengine.ImageEditor;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sotsys016-2 on 13/8/16 in com.cnc3camera.
 */
public class PhotoRedirectActivity extends AppCompatActivity {


    ImageView delete, back, edit;
    CustomViewPager viewPager;
    MultiStoriesPickedAdapter adapter;
    RecyclerView recyclerview;
    ArrayList<StoriesPickedModel> itemList = new ArrayList<>();
    public static ArrayList<String> imagess = new ArrayList<>();
    PickedStoriesSliderAdapter sliderAdapter;
    private int posi;
    public static String liveUrls;

    ImageView addStory;
    private PostModel finalPostModel;
    private String postType;
    public static PhotoRedirectActivity activity;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            stopServiceNow();
            uploadPost();

//            manager.removeUpdates(locationListener);


        }
    };

    private void uploadPost() {

        stopService(new Intent(getApplicationContext(), UploadPostService.class));
        String string = Long.toHexString(Double.doubleToLongBits(Math.random()));

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("images_url", liveUrls);
        map.addProperty("post_type", postType);
        map.addProperty("random_id", string);
        map.addProperty("time", System.currentTimeMillis());
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AddPostResponse> call = getResponse.addPost(map);
        call.enqueue(new Callback<AddPostResponse>() {
            @Override
            public void onResponse(Call<AddPostResponse> call, Response<AddPostResponse> response) {
                stopService(new Intent(getApplicationContext(), UploadPostService.class));
                if (response.code() == 200) {
                    liveUrls = null;
                    imagess.clear();
                    CommonUtils.showToast(response.body().getMessage());

                    finish();
                } else {
                    CommonUtils.showToast(response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AddPostResponse> call, Throwable t) {
                stopService(new Intent(getApplicationContext(), UploadPostService.class));
                CommonUtils.showToast(t.getMessage());

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("uploaded"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_redirect);
        activity = this;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("New post");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }
        edit = findViewById(R.id.edit);
        back = findViewById(R.id.back);
        delete = findViewById(R.id.delete);
        addStory = findViewById(R.id.addStory);
        viewPager = findViewById(R.id.viewPager);
        recyclerview = findViewById(R.id.recyclerview);
        itemList = SharedPrefs.getPickedList();
        if (itemList.size() > 1) {
            postType = "multi";
        } else {
            postType = "image";
        }


        addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showToast("Adding Post");
                if (itemList.size() > 1) {
                    imagess = new ArrayList<>();
                    for (StoriesPickedModel model : itemList) {
                        CompressImage compressImage = new CompressImage(PhotoRedirectActivity.this);
                        String abc = compressImage.compressImage(model.getUri());
                        imagess.add(abc);
                    }
                    startServiceNow();
//                    sendMultiImages();
                } else {
                    imagess = new ArrayList<>();
                    CompressImage compressImage = new CompressImage(PhotoRedirectActivity.this);
                    String abc = compressImage.compressImage(itemList.get(0).getUri());
//                    sendImage(abc);
                    imagess.add(abc);
                    startServiceNow();
                }
            }
        });

        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        adapter = new MultiStoriesPickedAdapter(this, itemList, new MultiStoriesPickedAdapter.AdapterCallbacks() {
            @Override
            public void onSelected(int position) {
                viewPager.setCurrentItem(position);
            }
        });
        recyclerview.setAdapter(adapter);
        sliderAdapter = new PickedStoriesSliderAdapter(this, itemList);
        viewPager.setAdapter(sliderAdapter);
        viewPager.setOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                posi = position;
                adapter.setPosition(position);
                if (position == 2) {
                    recyclerview.scrollToPosition(0);

                } else if (position == 5) {
                    recyclerview.scrollToPosition(itemList.size() - 1);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.remove(posi);
                if (posi > 0) {
                    posi = posi - 1;
                }
                adapter.setPosition(posi);
                sliderAdapter.setPicturesList(itemList);
                viewPager.setAdapter(sliderAdapter);
                viewPager.setCurrentItem(posi);
                if (itemList.size() == 0) {
                    finish();
                }

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    new ImageEditor.Builder(PhotoRedirectActivity.this, itemList.get(posi).getUri())
                            .setStickerAssets("stickers")
                            .open();
                } catch (Exception e) {
                    new ImageEditor.Builder(PhotoRedirectActivity.this, itemList.get(posi).getUri())
                            .setStickerAssets("stickers")
                            .open();
                }

            }
        });

    }

    private void startServiceNow() {
        stopServiceNow();
        boolean isClipboardServiceRunning = isMyServiceRunning(UploadPostService.class);

        if (!isClipboardServiceRunning) {
            Intent svc = new Intent(PhotoRedirectActivity.this, UploadPostService.class);

            startService(svc);
        } else {
//            CommonUtils.showToast("Service running");

        }
    }

    private void stopServiceNow() {
        boolean isClipboardServiceRunning = isMyServiceRunning(UploadPostService.class);

        if (isClipboardServiceRunning) {
            stopService(new Intent(getApplicationContext(), UploadPostService.class));

        } else {
//            CommonUtils.showToast("Service not running running");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 52) {

            String imagePath = data.getStringExtra(ImageEditor.EXTRA_EDITED_PATH);
            StoriesPickedModel abc = itemList.get(posi);
            abc.setUri(imagePath);
            itemList.set(posi, abc);
            adapter.notifyDataSetChanged();
            sliderAdapter.setPicturesList(itemList);
            viewPager.setAdapter(sliderAdapter);
            viewPager.setCurrentItem(posi);
            sliderAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onBackPressed() {
        stopService(new Intent(getApplicationContext(), UploadPostService.class));
        if (ImageEditActivity.activity != null) {
            ImageEditActivity.activity.finish();
        }
        Intent i = new Intent(PhotoRedirectActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photoredirect_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }
        if (item.getItemId() == R.id.action_share) {
//            if (postType.equalsIgnoreCase("image")) {
//                sendImage();
//            }
//            if (postType.equalsIgnoreCase("Multi")) {
//                sendMultiImages();
//            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        super.onStop();
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ApplicationClass.getInstance().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
