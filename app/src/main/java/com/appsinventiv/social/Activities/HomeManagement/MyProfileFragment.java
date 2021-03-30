package com.appsinventiv.social.Activities.HomeManagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.appsinventiv.social.Activities.AccountSettings;
import com.appsinventiv.social.Activities.ListOfNotifications;
import com.appsinventiv.social.Adapters.MyProfilePostsAdapter;
import com.appsinventiv.social.Activities.MyListOfFriends;
import com.appsinventiv.social.Interfaces.PictureClickCallbacks;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.NetworkResponses.AllPostsResponse;
import com.appsinventiv.social.NetworkResponses.ApiResponse;
import com.appsinventiv.social.NetworkResponses.LoginResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.CompressImage;
import com.appsinventiv.social.Utils.CompressImageToThumbnail;
import com.appsinventiv.social.Utils.Constants;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.bumptech.glide.Glide;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonObject;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfileFragment extends Fragment {
    private static final int REQUEST_CODE_CHOOSE = 24;
    View rootView;
    Context context;

    CircleImageView profilePic;
    TextView name, personName;
    RecyclerView recyclerview;
    MyProfilePostsAdapter adapter;
    private List<PostModel> itemList = new ArrayList<>();
    ImageView picPicture;
    private ArrayList<String> mSelected = new ArrayList<>();
    private String liveUrl;
    ProgressBar progressPic;
    private String thumbnailUrl;
    ImageView menu;
    TextView postCount, friendsCount;
    LinearLayout postss, friendss;
    TextView noPosts;
    TextView notifications;
    AdView mAdView;

    Switch profileType;

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.my_profile_fragment, container, false);

        notifications = rootView.findViewById(R.id.notifications);
        profileType = rootView.findViewById(R.id.profileType);
        profilePic = rootView.findViewById(R.id.profilePic);
        mAdView = rootView.findViewById(R.id.mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        recyclerview = rootView.findViewById(R.id.recyclerview);
        name = rootView.findViewById(R.id.name);
        postss = rootView.findViewById(R.id.postss);
        friendss = rootView.findViewById(R.id.friendss);
        personName = rootView.findViewById(R.id.personName);
        noPosts = rootView.findViewById(R.id.noPosts);
        progressPic = rootView.findViewById(R.id.progressPic);
        friendsCount = rootView.findViewById(R.id.friendsCount);
        postCount = rootView.findViewById(R.id.postCount);
        menu = rootView.findViewById(R.id.menu);
        picPicture = rootView.findViewById(R.id.picPicture);
        if (SharedPrefs.getUserModel().getPicUrl() != null) {
            Glide.with(context).load(AppConfig.BASE_URL_Image + SharedPrefs.getUserModel().getThumbnailUrl()).placeholder(R.drawable.ic_profile_plc).into(profilePic);
        }

        if (SharedPrefs.getUserModel().getType() == 1) {
            profileType.setChecked(true);
        } else {
            profileType.setChecked(false);
        }
        profileType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Make profile " + (isChecked ? "public" : "private"));
                    String msg;
                    if (isChecked) {
                        msg = "Making account public will allow everyone will be able to see your posts and stories";
                    } else {
                        msg = "Making account private will only allow friends to see your posts and stories";

                    }
                    builder.setMessage(msg);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            updateDataToServer(isChecked);

                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });

        name.setText(SharedPrefs.getUserModel().getName());
        friendsCount.setText("" + SharedPrefs.getUserModel().getFriendsCount());
        personName.setText(SharedPrefs.getUserModel().getName());
        adapter = new MyProfilePostsAdapter(context, itemList, new PictureClickCallbacks() {
            @Override
            public void onPictureClicked(int position) {
                Constants.PICTURE_POSITION = position;
                startActivity(new Intent(context, UserPostsActivity.class));

            }
        });
        recyclerview.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerview.setAdapter(adapter);
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ListOfNotifications.class));
            }
        });

        picPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMatisse();
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        friendss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MyListOfFriends.class));
            }
        });


        getDataFromServer();
        return rootView;
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
                        CommonUtils.showToast("Done");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_my_profile, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.accountSettings:
//                        SharedPrefs.logout();
                        Intent i = new Intent(context, AccountSettings.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
//                        MainActivity.activity.finish();
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void initMatisse() {
        mSelected.clear();

        Options options = Options.init()
                .setRequestCode(REQUEST_CODE_CHOOSE)                                           //Request code for activity results
                .setCount(5)                                                   //Number of images to restict selection count
                .setExcludeVideos(true)                                       //Option to exclude videos
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                ;                                       //Custom Path For media Storage

        Pix.start(this, options);
    }

    private void getDataFromServer() {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AllPostsResponse> call = getResponse.myPosts(map);
        call.enqueue(new Callback<AllPostsResponse>() {
            @Override
            public void onResponse(Call<AllPostsResponse> call, Response<AllPostsResponse> response) {
                if (response.code() == 200) {
                    List<PostModel> data = response.body().getPosts();
                    HomeFragment.likesList = response.body().getLikesList() == null ? new ArrayList<>() : response.body().getLikesList();
                    if (data != null && data.size() > 0) {
                        noPosts.setVisibility(View.GONE);
                        itemList = data;
                        adapter.setItemList(itemList);

                        setupCache();
                        SharedPrefs.setPosts(itemList);
                    } else {
                        noPosts.setVisibility(View.VISIBLE);
                    }
                    postCount.setText("" + itemList.size());
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

    private void updateProfilePicture() {

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("picUrl", liveUrl);
        map.addProperty("thumbnailUrl", thumbnailUrl);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<LoginResponse> call = getResponse.updateProfilePicture(map);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressPic.setVisibility(View.GONE);
                if (response.code() == 200) {
                    LoginResponse object = response.body();
                    if (object != null) {
                        if (object.getUser() != null) {
                            SharedPrefs.setUserModel(object.getUser());
                            CommonUtils.showToast("Picture Uploaded");
                            Glide.with(context).load(AppConfig.BASE_URL_Image + SharedPrefs.getUserModel().getThumbnailUrl()).into(profilePic);
                        }
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());

            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 24 && resultCode == Activity.RESULT_OK) {
            mSelected = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            CompressImage compressImage = new CompressImage(context);
            CompressImageToThumbnail thumbnail = new CompressImageToThumbnail(context);
            compressImage.compressImage("" + mSelected.get(0));
            progressPic.setVisibility(View.VISIBLE);

            uploadThumbnailFile(thumbnail.compressImage("" + mSelected.get(0)));
            uploadFile(compressImage.compressImage("" + mSelected.get(0)));
//            PhotoRedirectActivity.activity.finish();
        }
    }

    private void uploadFile(String abc) {
        // create upload service client
        File file = new File(abc);

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
                        if (thumbnailUrl != null && liveUrl != null) {
                            updateProfilePicture();
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

    private void uploadThumbnailFile(String abc) {
        // create upload service client
        File file = new File(abc);

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
                        thumbnailUrl = response.body().string();
                        if (thumbnailUrl != null && liveUrl != null) {
                            updateProfilePicture();
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


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
