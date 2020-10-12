package com.appsinventiv.social.Activities.Stories;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.appsinventiv.social.Activities.Camera.MultiStoriesPickedAdapter;
import com.appsinventiv.social.Activities.Camera.PhotoRedirectActivity;
import com.appsinventiv.social.Activities.Camera.PickedStoriesSliderAdapter;
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
import com.bumptech.glide.Glide;
import com.droidninja.imageeditengine.ImageEditor;
import com.google.gson.JsonObject;


import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryRedirectActivity extends AppCompatActivity {


    public static String liveUrls;
    public static ArrayList<String> imagess = new ArrayList<>();
    ImageView addStory;
    RecyclerView recyclerview;
    CustomViewPager viewPager;

    MultiStoriesPickedAdapter adapter;
    //    List<String> itemList = new ArrayList<>();
    ArrayList<StoriesPickedModel> itemList = new ArrayList<>();
    public static ArrayList<StoriesPickedModel> finalList = new ArrayList<>();
    ArrayList<StoriesPickedModel> listToUpload = new ArrayList<>();


    PickedStoriesSliderAdapter sliderAdapter;
    int posi = 0;
    ImageView delete, back, edit;
    int count = 0;
    RelativeLayout wholeLayout;
    CircleImageView userPic;
    int countt = 0;
    int fnf = 0;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            stopService(new Intent(getApplicationContext(), UploadStoryService.class));

            uploadPost();

//            manager.removeUpdates(locationListener);


        }
    };

    private void uploadPost() {

        stopService(new Intent(getApplicationContext(), UploadStoryService.class));
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("urls", liveUrls);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AddPostResponse> call = getResponse.addStory(map);
        call.enqueue(new Callback<AddPostResponse>() {
            @Override
            public void onResponse(Call<AddPostResponse> call, Response<AddPostResponse> response) {
                stopService(new Intent(getApplicationContext(), UploadStoryService.class));
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
                stopService(new Intent(getApplicationContext(), UploadStoryService.class));
                CommonUtils.showToast(t.getMessage());

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("storyUploaded"));
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_redirect_activity);

//        itemList = SharedPrefs.getMultiImgs();
        itemList = SharedPrefs.getPickedList();
        finalList = itemList;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }

        wholeLayout = findViewById(R.id.wholeLayout);
        edit = findViewById(R.id.edit);
        back = findViewById(R.id.back);
        delete = findViewById(R.id.delete);
        viewPager = findViewById(R.id.viewPager);
        addStory = findViewById(R.id.addStory);
        userPic = findViewById(R.id.userPic);
        recyclerview = findViewById(R.id.recyclerview);

        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        adapter = new MultiStoriesPickedAdapter(this, itemList, new MultiStoriesPickedAdapter.AdapterCallbacks() {
            @Override
            public void onSelected(int position) {
                viewPager.setCurrentItem(position);
            }
        });
        recyclerview.setAdapter(adapter);
        Glide.with(this).load(AppConfig.BASE_URL_Image + SharedPrefs.getUserModel().getThumbnailUrl()).into(userPic);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                if (itemList.get(position).getType().equalsIgnoreCase("video")) {
                    edit.setVisibility(View.GONE);
                } else {
                    edit.setVisibility(View.VISIBLE);
                }
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
                finalList = itemList;
                if (itemList.size() == 0) {
                    finish();
                }

            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    new ImageEditor.Builder(StoryRedirectActivity.this, CommonUtils.getRealPathFromURI(Uri.parse(itemList.get(posi).getUri())))
                            .setStickerAssets("stickers")
                            .open();
                } catch (Exception e) {

                } finally {
                    new ImageEditor.Builder(StoryRedirectActivity.this, itemList.get(posi).getUri())
                            .setStickerAssets("stickers")
                            .open();
                }

            }
        });

        addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonUtils.showToast("Adding Story");
                if (itemList.size() > 1) {
                    imagess = new ArrayList<>();
                    for (StoriesPickedModel model : itemList) {
                        CompressImage compressImage = new CompressImage(StoryRedirectActivity.this);
                        String abc = compressImage.compressImage(model.getUri());
                        imagess.add(abc);
                    }
                    startServiceNow();
//                    sendMultiImages();
                } else {
                    imagess = new ArrayList<>();
                    CompressImage compressImage = new CompressImage(StoryRedirectActivity.this);
                    String abc = compressImage.compressImage(itemList.get(0).getUri());
//                    sendImage(abc);
                    imagess.add(abc);
                    startServiceNow();
                }


            }
        });


    }


    private void startServiceNow() {
        stopServiceNow();
        boolean isClipboardServiceRunning = isMyServiceRunning(UploadStoryService.class);

        if (!isClipboardServiceRunning) {
            Intent svc = new Intent(StoryRedirectActivity.this, UploadStoryService.class);

            startService(svc);

            finish();
        } else {
//            CommonUtils.showToast("Service running");

        }
    }

    private void stopServiceNow() {
        boolean isClipboardServiceRunning = isMyServiceRunning(UploadStoryService.class);

        if (isClipboardServiceRunning) {
            stopService(new Intent(getApplicationContext(), UploadStoryService.class));

        } else {
//            CommonUtils.showToast("Service not running running");
        }


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


    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        stopService(new Intent(getApplicationContext(), UploadStoryService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 52) {

            String imagePath = data.getStringExtra(ImageEditor.EXTRA_EDITED_PATH);
            StoriesPickedModel abc = itemList.get(posi);
            abc.setUri(imagePath);
            itemList.set(posi, abc);
            CompressImage compressImage = new CompressImage(this);
            String aaa = compressImage.compressImage(imagePath);
            StoriesPickedModel ali = finalList.get(posi);
            ali.setUri(aaa);
            finalList.set(posi, ali);
            adapter.notifyDataSetChanged();
            sliderAdapter.setPicturesList(itemList);
            viewPager.setAdapter(sliderAdapter);
            viewPager.setCurrentItem(posi);
            sliderAdapter.notifyDataSetChanged();
            finalList = itemList;
        }
    }


}
