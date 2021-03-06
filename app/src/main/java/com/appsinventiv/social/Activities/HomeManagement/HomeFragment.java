package com.appsinventiv.social.Activities.HomeManagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.appsinventiv.social.Activities.Camera.PhotoRedirectActivity;
import com.appsinventiv.social.Activities.Chat.ChatActivity;
import com.appsinventiv.social.Activities.MainActivity;
import com.appsinventiv.social.Activities.OtherUser.ViewProfile;
import com.appsinventiv.social.Activities.Stories.AddStoryActivity;
import com.appsinventiv.social.Activities.Stories.HomeStoriesAdapter;
import com.appsinventiv.social.Activities.Stories.MyStoryActivity;
import com.appsinventiv.social.Activities.Stories.StoryActivity;
import com.appsinventiv.social.Activities.Stories.StoryRedirectActivity;
import com.appsinventiv.social.CommonFunctions;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.Models.StoriesPickedModel;
import com.appsinventiv.social.Models.StoryModel;
import com.appsinventiv.social.Models.StoryViewsModel;
import com.appsinventiv.social.NetworkResponses.AllPostsResponse;
import com.appsinventiv.social.NetworkResponses.AllStoriesResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.ApplicationClass;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.CompressImage;
import com.appsinventiv.social.Utils.CompressImageToThumbnail;
import com.appsinventiv.social.Utils.Constants;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.bumptech.glide.Glide;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final int REQUEST_CODE_CHOOSE = 25;
    Context context;
    private View rootView;
    PostsAdapter adapter;
    private List<PostModel> postList = new ArrayList<>();


    CircleImageView userPic;
    ImageView plusImg;
    RecyclerView friendsStories;

    HomeStoriesAdapter storiesAdapter;
    private RecyclerView recycler;
    private ArrayList<String> mSelected = new ArrayList<>();
    public static List<Integer> likesList = new ArrayList<>();
    ImageView circleImg, chat, camera;
    CardView noPosts;
    Button createPost;


    public HomeFragment(Context context) {
        this.context = context;
    }


    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_fragment, container, false);
        recycler = rootView.findViewById(R.id.my_fancy_videos);
        circleImg = rootView.findViewById(R.id.circleImg);
        userPic = rootView.findViewById(R.id.userPic);
        createPost = rootView.findViewById(R.id.createPost);
        chat = rootView.findViewById(R.id.chat);
        camera = rootView.findViewById(R.id.camera);
        plusImg = rootView.findViewById(R.id.plusImg);
        noPosts = rootView.findViewById(R.id.noPosts);
        friendsStories = rootView.findViewById(R.id.friendsStories);

        friendsStories.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));


        plusImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChatActivity.class));
            }
        });

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options options = Options.init()
                        .setRequestCode(23)                                           //Request code for activity results
                        .setCount(5)                                                   //Number of images to restict selection count
                        .setExcludeVideos(true)                                       //Option to exclude videos
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                        ;                                       //Custom Path For media Storage

                Pix.start(getActivity(), options);
            }
        });

        Glide.with(context).load(AppConfig.BASE_URL_Image + SharedPrefs.getUserModel().getUsername() + "/" + SharedPrefs.getUserModel().getThumbnailUrl()).placeholder(R.drawable.ic_profile_plc).into(userPic);
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(context, StoriesCameraActivity.class));
                if (MainActivity.myArrayLists != null) {
                    if (MainActivity.myArrayLists.size() > 0) {
                        Intent i = new Intent(context, MyStoryActivity.class);
                        context.startActivity(i);
                    } else {
                        openGallery();
                    }
                } else {
                    openGallery();
                }
            }
        });
        adapter = new PostsAdapter(getActivity(), postList, new PostAdaptersCallbacks() {
            @Override
            public void takeUserToMyUserProfile(int userId) {
                MyProfileFragment fragment = new MyProfileFragment();
                loadFragment(fragment);
            }

            @Override
            public void takeUserToOtherUserProfile(int userId) {
                Intent i = new Intent(context, ViewProfile.class);
                i.putExtra("userId", userId);
                startActivity(i);
            }

            @Override
            public void takeUserToLikesScreen(int postId) {

            }

            @Override
            public void onLikedPost(PostModel model) {
                CommonFunctions.likePost(model);
            }

            @Override
            public void onUnlikedPost(PostModel model) {
                CommonFunctions.likePost(model);
            }

            @Override
            public void onFileDownload(String filename) {

            }

            @Override
            public void onDelete(PostModel model, int position) {
                showDeleteAlert(model.getId(), position);

            }

            @Override
            public void onMutePost(PostModel model) {

            }

            @Override
            public void onUnMutePost(PostModel model) {

            }

            @Override
            public void onSharePostWithFriends(PostModel model) {

            }

            @Override
            public void onRePost(PostModel model) {

            }

            @Override
            public void onShowDownloadMenu(PostModel model) {

            }
        });
        recycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
//        adapter.setLikeList(SharedPrefs.getLikesList());
        postList = SharedPrefs.getHomeList();
        if (postList != null) {
            adapter.setDataArrayList(postList);
            setStoriesList();
        }

        getDataFromServer();


        return rootView;

    }

    private void setStoriesList() {
        ArrayList<ArrayList<StoryModel>> list = SharedPrefs.getHomeStories();
        if (list == null) {
            list = new ArrayList<>();
        }


        storiesAdapter = new HomeStoriesAdapter(context, list, new HomeStoriesAdapter.HomeStoriesAdapterCallbacks() {
            @Override
            public void onStoryClicked(StoryModel model, int position) {
                Constants.STORY_POSITION = position;
                Intent i = new Intent(context, StoryActivity.class);
                context.startActivity(i);

            }
        });
        friendsStories.setAdapter(storiesAdapter);
        if (MainActivity.myArrayLists.size() > 0) {
            circleImg.setVisibility(View.VISIBLE);
        } else {
            circleImg.setVisibility(View.GONE);
        }
    }


    private void openGallery() {
        Options options = Options.init()
                .setRequestCode(REQUEST_CODE_CHOOSE)                                           //Request code for activity results
                .setCount(5)                                                   //Number of images to restict selection count
                .setExcludeVideos(true)                                       //Option to exclude videos
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                ;                                       //Custom Path For media Storage

        Pix.start(getActivity(), options);
    }

    private void showDeleteAlert(final Integer idd, final int positionn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Alert");
        builder.setMessage("Do you want to delete this post? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                postList.remove(positionn);
                adapter.setDataArrayList(postList);
                callDeleteApi(idd, positionn);

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void callDeleteApi(Integer idd, final int positionn) {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("post_id", idd);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ResponseBody> call = getResponse.deletePost(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    CommonUtils.showToast("Post Delete");
                    postList.remove(positionn);
//                    adapter.setDataArrayList(postList);
                    if (positionn == 0) {
//                        adapter.setDataArrayList(postList);
                    } else {
//                        adapter.notifyItemRemoved(positionn);
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

    private void getDataFromServer() {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        map.addProperty("friends", SharedPrefs.getUserModel().getCommaFriends());
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AllPostsResponse> call = getResponse.allPosts(map);
        call.enqueue(new Callback<AllPostsResponse>() {
            @Override
            public void onResponse(Call<AllPostsResponse> call, Response<AllPostsResponse> response) {
                if (response.code() == 200) {
                    List<PostModel> data = response.body().getPosts();
                    likesList = response.body().getLikesList() == null ? new ArrayList<>() : response.body().getLikesList();
                    if (data != null && data.size() > 0) {
                        noPosts.setVisibility(View.GONE);
                        postList = data;

                        Constants.IS_HOME_LOADED = true;
                        SharedPrefs.setHomeList(postList);
                        adapter.setDataArrayList(postList);

                    } else {
                        SharedPrefs.setHomeList(new ArrayList<>());
                        noPosts.setVisibility(View.VISIBLE);
                        recycler.setVisibility(View.GONE);
                    }
                    getStoriesFromServer();
                }
            }

            @Override
            public void onFailure(Call<AllPostsResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });


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
                        MainActivity.myArrayLists.clear();
                        MainActivity.storiesHasMap.clear();
                        MainActivity.storyViewsHashMap.clear();
                        for (StoryModel model : data) {
                            if (model.getUserId().equals(SharedPrefs.getUserModel().getId())) {
                                MainActivity.myArrayLists.add(model);
                            } else {
                                if (MainActivity.storiesHasMap.containsKey(model.getUserId())) {
                                    ArrayList<StoryModel> list = MainActivity.storiesHasMap.get(model.getUserId());
                                    list.add(model);
                                    MainActivity.storiesHasMap.put(model.getUserId(), list);
                                } else {
                                    ArrayList<StoryModel> list = new ArrayList<>();
                                    list.add(model);
                                    MainActivity.storiesHasMap.put(model.getUserId(), list);
                                }
                            }
                        }
                        if (MainActivity.storiesHasMap.size() > 0) {
                            MainActivity.arrayLists.clear();
                            ArrayList<ArrayList<StoryModel>> listtt = new ArrayList<>(MainActivity.storiesHasMap.values());
                            Collections.sort(listtt, new Comparator<ArrayList<StoryModel>>() {
                                @Override
                                public int compare(ArrayList<StoryModel> listData, ArrayList<StoryModel> t1) {
                                    Long ob1 = listData.get(listData.size() - 1).getTime();
                                    Long ob2 = t1.get(t1.size() - 1).getTime();
                                    return ob2.compareTo(ob1);

                                }
                            });
                            MainActivity.arrayLists.addAll(listtt);
                            SharedPrefs.setHomeStories(MainActivity.arrayLists);
                        } else {
                            MainActivity.arrayLists = new ArrayList<>();

                            SharedPrefs.setHomeStories(MainActivity.arrayLists);

                        }
                    } else {
                        MainActivity.arrayLists = new ArrayList<>();

                        SharedPrefs.setHomeStories(MainActivity.arrayLists);
                    }
                    List<StoryViewsModel> storyViewsList = response.body().getStoryViews();
                    if (storyViewsList != null && storyViewsList.size() > 0) {
                        for (StoryViewsModel storyView : storyViewsList) {
                            if (MainActivity.storyViewsHashMap.containsKey(storyView.getStoryId())) {
                                ArrayList<StoryViewsModel> list = MainActivity.storyViewsHashMap.get(storyView.getStoryId());
                                list.add(storyView);
                                MainActivity.storyViewsHashMap.put(storyView.getStoryId(), list);
                            } else {
                                ArrayList<StoryViewsModel> list = new ArrayList<>();
                                list.add(storyView);
                                MainActivity.storyViewsHashMap.put(storyView.getStoryId(), list);
                            }
                        }
                    }
                    setStoriesList();

                }
            }

            @Override
            public void onFailure(Call<AllStoriesResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 25 && resultCode == Activity.RESULT_OK) {
            mSelected = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
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
                startActivity(new Intent(getContext(), StoryRedirectActivity.class));

            }
        }
    }


    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


}
