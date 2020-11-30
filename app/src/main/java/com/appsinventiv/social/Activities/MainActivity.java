package com.appsinventiv.social.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.appsinventiv.social.Activities.Camera.CameraActivity;
import com.appsinventiv.social.Activities.Camera.PhotoRedirectActivity;
import com.appsinventiv.social.Activities.Camera.TextStatusActivity;
import com.appsinventiv.social.Activities.HomeManagement.ChatsFragment;
import com.appsinventiv.social.Activities.HomeManagement.HomeFragment;
import com.appsinventiv.social.Activities.HomeManagement.MyProfileFragment;
import com.appsinventiv.social.Activities.HomeManagement.SearchFragment;
import com.appsinventiv.social.Models.StoriesPickedModel;
import com.appsinventiv.social.Models.StoryModel;
import com.appsinventiv.social.Models.StoryViewsModel;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.NetworkResponses.AllStoriesResponse;
import com.appsinventiv.social.NetworkResponses.UpdateFcmKeyResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.Constants;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;

import com.droidninja.imageeditengine.ImageEditor;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Fragment fragment;
    private static final int REQUEST_CODE_CHOOSE = 23;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    public static ArrayList<ArrayList<StoryModel>> arrayLists = new ArrayList<>();
    public static List<StoryModel> myArrayLists = new ArrayList<>();
    public static HashMap<Integer, ArrayList<StoryModel>> storiesHasMap = new HashMap<>();
    public static HashMap<Integer, ArrayList<StoryViewsModel>> storyViewsHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constants.IS_HOME_LOADED = false;
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getPermissions();
        updateFcmKey();
        fragment = new HomeFragment(MainActivity.this);
        loadFragment(fragment);
//        getStoriesFromServer();
    }

    private void downloadFile(final String url) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File mediaStorageDir = new File(mediaStorageDir = Environment.getExternalStorageDirectory(), "/Downlo/");

                    if (mediaStorageDir.exists()) {

                    } else {
                        mediaStorageDir.mkdirs();
                    }
                    String abc = CommonUtils.getNameFromUrl(url);

                    String uploadFilepathtemp = Environment.getExternalStorageDirectory()
                            + "/Downlo/" + abc
                            + ".jpg";
                    File carmeraFile = new File(uploadFilepathtemp);
                    if (carmeraFile.exists()) {
                        return;
                    }
                    URL u = new URL(url);
                    URLConnection conn = u.openConnection();
                    int contentLength = conn.getContentLength();

                    DataInputStream stream = new DataInputStream(u.openStream());

                    byte[] buffer = new byte[contentLength];
                    stream.readFully(buffer);
                    stream.close();

                    DataOutputStream fos = new DataOutputStream(new FileOutputStream(carmeraFile));
                    fos.write(buffer);
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    CommonUtils.showToast(e.getMessage());

                }
            }
        });
        thread.start();
    }


    private void getStoriesFromServer() {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("friends", SharedPrefs.getUserModel().getCommaFriends());
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AllStoriesResponse> call = getResponse.allStories(map);
        call.enqueue(new Callback<AllStoriesResponse>() {
            @Override
            public void onResponse(Call<AllStoriesResponse> call, Response<AllStoriesResponse> response) {
                if (response.code() == 200) {
                    List<StoryModel> data = response.body().getPosts();
                    if (data != null && data.size() > 0) {
                        myArrayLists.clear();
                        storiesHasMap.clear();
                        storyViewsHashMap.clear();
                        for (StoryModel model : data) {
                            if (model.getUserId().equals(SharedPrefs.getUserModel().getId())) {
                                myArrayLists.add(model);
                            } else {
                                if (storiesHasMap.containsKey(model.getUserId())) {
                                    ArrayList<StoryModel> list = storiesHasMap.get(model.getUserId());
                                    list.add(model);
                                    storiesHasMap.put(model.getUserId(), list);
                                } else {
                                    ArrayList<StoryModel> list = new ArrayList<>();
                                    list.add(model);
                                    storiesHasMap.put(model.getUserId(), list);
                                }
                            }
                        }
                        if (storiesHasMap.size() > 0) {
                            arrayLists.clear();
                            arrayLists.addAll(storiesHasMap.values());
                            SharedPrefs.setHomeStories(arrayLists);
                        } else {
                            arrayLists = new ArrayList<>();

                            SharedPrefs.setHomeStories(arrayLists);

                        }
                    } else {
                        arrayLists = new ArrayList<>();

                        SharedPrefs.setHomeStories(arrayLists);
                    }
                    List<StoryViewsModel> storyViewsList = response.body().getStoryViews();
                    if (storyViewsList != null && storyViewsList.size() > 0) {
                        for (StoryViewsModel storyView : storyViewsList) {
                            if (storyViewsHashMap.containsKey(storyView.getStoryId())) {
                                ArrayList<StoryViewsModel> list = storyViewsHashMap.get(storyView.getStoryId());
                                list.add(storyView);
                                storyViewsHashMap.put(storyView.getStoryId(), list);
                            } else {
                                ArrayList<StoryViewsModel> list = new ArrayList<>();
                                list.add(storyView);
                                storyViewsHashMap.put(storyView.getStoryId(), list);
                            }
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<AllStoriesResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());

            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    toolbar.setTitle("Shop");


                    fragment = new HomeFragment(MainActivity.this);
                    loadFragment(fragment);

                    return true;
                case R.id.navigation_chats:
                    fragment = new ChatsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_post:

//                    toolbar.setTitle("Cart");
//                    showBottomSheet();
                    initMatisse();
                    return true;
                case R.id.navigation_search:
                    fragment = new SearchFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_profile:
                    fragment = new MyProfileFragment();
                    loadFragment(fragment);
                    return true;

            }
            return false;
        }
    };

    private void showBottomSheet() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.post_options_sheet, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        dialog.setContentView(customView);
        ImageView cancel = customView.findViewById(R.id.cancel);
        LinearLayout camera = customView.findViewById(R.id.camera);
        LinearLayout upload = customView.findViewById(R.id.upload);
        LinearLayout text = customView.findViewById(R.id.text);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, TextStatusActivity.class));

            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                startActivity(new Intent(MainActivity.this, CameraActivity.class));

            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                initMatisse();

//                initMatisse();
//                showUploadDialog();

            }
        });
//        startActivity(new Intent(MainActivity.this, CameraActivity.class));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showUploadDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_dialog_upload, null);

        dialog.setContentView(layout);

        LinearLayout video = layout.findViewById(R.id.video);
        LinearLayout picture = layout.findViewById(R.id.picture);


        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                initMatisse();
            }
        });


        dialog.show();

    }

    private void initMatisse() {


        Options options = Options.init()
                .setRequestCode(REQUEST_CODE_CHOOSE)                                           //Request code for activity results
                .setCount(5)                                                   //Number of images to restict selection count
                .setExcludeVideos(true)                                       //Option to exclude videos
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                ;                                       //Custom Path For media Storage

        Pix.start(MainActivity.this, options);


    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


//        if (requestCode == 23) {
//
//            if (fragment != null) {
//                fragment.onActivityResult(requestCode, resultCode, data);
//            }
//        }
        if (requestCode == 25) {

            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        if (requestCode == 25) {

            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        if (requestCode == ImageEditor.RC_IMAGE_EDITOR) {

            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                checkPermission();
            } else {
                // Do as per your logic
            }
        }
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {


//          List<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);


//            List<Uri> mSelected = Matisse.obtainResult(data);

//            mSelected = Matisse.obtainResult(data);
            ArrayList<String> mSelected = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            ArrayList<StoriesPickedModel> list = new ArrayList<>();
            int count = 0;
            for (String path : mSelected) {
                Uri uri = Uri.parse(path);
                StoriesPickedModel model = new StoriesPickedModel(
                        "" + uri, "" + uri, "", "image", count
                );
                list.add(model);
                count++;

            }
            if (list.size() > 0) {
                Collections.sort(list, new Comparator<StoriesPickedModel>() {
                    @Override
                    public int compare(StoriesPickedModel listData, StoriesPickedModel t1) {
                        Long ob1 = listData.getTime();
                        Long ob2 = t1.getTime();
                        return ob1.compareTo(ob2);

                    }
                });
                SharedPrefs.setPickedList(list);
                startActivity(new Intent(this, PhotoRedirectActivity.class));

            }

        }


    }


    public void checkPermission() {
        System.out.println("CHECK PERMISSIONS:");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,


        };

        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
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

    private void updateFcmKey() {
        if (SharedPrefs.getUserModel() != null) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String deviceToken = instanceIdResult.getToken();
                    UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
                    Call<UpdateFcmKeyResponse> call = getResponse.updateFcmKey(
                            AppConfig.API_USERNAME, AppConfig.API_PASSOWRD,
                            "" + SharedPrefs.getUserModel().getId(),
                            deviceToken
                    );
                    call.enqueue(new Callback<UpdateFcmKeyResponse>() {
                        @Override
                        public void onResponse(Call<UpdateFcmKeyResponse> call, Response<UpdateFcmKeyResponse> response) {
                            if (response.code() == 200) {
                                UserModel user = response.body().getUserModel();
                                if (user != null) {
                                    SharedPrefs.setUserModel(user);
                                }
                            } else {
                                CommonUtils.showToast(response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateFcmKeyResponse> call, Throwable t) {
                            CommonUtils.showToast(t.getMessage());
                        }
                    });
                    // Do whatever you want with your token now
                    // i.e. store it on SharedPreferences or DB
                    // or directly send it to server
                }

            });
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
