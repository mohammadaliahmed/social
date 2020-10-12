package com.appsinventiv.social.Activities.Stories;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.appsinventiv.social.Activities.MainActivity;
import com.appsinventiv.social.Models.StoryModel;
import com.appsinventiv.social.Models.StoryViewsModel;
import com.appsinventiv.social.NetworkResponses.AllStoriesResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.bumptech.glide.Glide;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonObject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyStoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {


    public static Activity activity;

    private StoriesProgressView storiesProgressView;
    private ProgressBar mProgressBar;
    private LinearLayout mVideoViewLayout;
    private int counter = 0;
    private List<StoryModel> mStoriesList = new ArrayList<>();

    private List<View> mediaPlayerArrayList = new ArrayList<>();

    long pressTime = 0L;
    long limit = 500L;
    TextView storyByName, time;
    CircleImageView storyByPic;
    boolean deleteClicked;


    TextView views, viewCount;
    RecyclerView recyclerview;
    ImageView delete, eye;

    //    HashMap<String, ArrayList<StoryViewsModel>> map = new HashMap<>();
    private StoryViewsAdapter adapter;
    private ArrayList<StoryViewsModel> viewsList = new ArrayList<>();

    MediaPlayer mmmmedia;


    //Minimum Video you want to buffer while Playing
    public static final int MIN_BUFFER_DURATION = 3000;
    //Max Video you want to buffer during PlayBack
    public static final int MAX_BUFFER_DURATION = 5000;
    //Min Video you want to buffer before start Playing it
    public static final int MIN_PLAYBACK_START_BUFFER = 1500;
    //Min video You want to buffer when user resumes video
    public static final int MIN_PLAYBACK_RESUME_BUFFER = 5000;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.my_stories);


        LinearLayout layoutBottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior sheetBehavior;
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        views = findViewById(R.id.views);
        eye = findViewById(R.id.eye);
        viewCount = findViewById(R.id.viewCount);
        delete = findViewById(R.id.delete);
        recyclerview = findViewById(R.id.recyclerview);

//        getViewsFromDB();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClicked = true;
                showDeleteAlert();
            }
        });


//
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                if (!deleteClicked)
//                    storiesProgressView.resume();
//            }
//        });


//        ArrayList<StoryViewsModel> friendsList = new ArrayList<>();


        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerview.setAdapter(adapter);


        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        storiesProgressView.resume();
                        if (mmmmedia != null) {
                            try {
                                mmmmedia.pause();

                            } catch (Exception e) {

                            }
                        }
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
//                        btnBottomSheet.setText("Close Sheet");
                        storiesProgressView.pause();
                        viewCount.setVisibility(View.GONE);
                        eye.setVisibility(View.GONE);
                        if (mmmmedia != null) {
                            try {
                                mmmmedia.pause();

                            } catch (Exception e) {

                            }
                        }
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
//                        btnBottomSheet.setText("Expand Sheet");
                        viewCount.setVisibility(View.VISIBLE);
                        eye.setVisibility(View.VISIBLE);
                        storiesProgressView.resume();
                        if (mmmmedia != null) {
                            try {
                                mmmmedia.start();

                            } catch (Exception e) {

                            }
                        }
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        storiesProgressView.pause();
                        if (mmmmedia != null) {
                            try {
                                mmmmedia.pause();

                            } catch (Exception e) {

                            }
                        }
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        storiesProgressView.pause();
                        if (mmmmedia != null) {
                            try {
                                mmmmedia.pause();

                            } catch (Exception e) {

                            }
                        }
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        mProgressBar = findViewById(R.id.progressBar);
        storyByPic = findViewById(R.id.storyByPic);
        storyByName = findViewById(R.id.storyByName);
        time = findViewById(R.id.time);
        mVideoViewLayout = findViewById(R.id.videoView);
        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);

        storyByName.setText(SharedPrefs.getUserModel().getName());
        Glide.with(this).load(AppConfig.BASE_URL_Image + SharedPrefs.getUserModel().getThumbnailUrl()).into(storyByPic);

        storiesProgressView.setStoriesCount(MainActivity.myArrayLists.size());
        prepareStoriesList();
        storiesProgressView.setStoriesListener(this);
        for (int i = 0; i < mStoriesList.size(); i++) {
            if (mStoriesList.get(i).getUrl().contains("mp4")) {
                mediaPlayerArrayList.add(getVideoView(i));
            } else if (mStoriesList.get(i).getUrl().contains("jpg")) {
                mediaPlayerArrayList.add(getImageView(i));
            }

        }

//        adapter = new StoryViewsAdapter(this, viewsList);
//        views.setText(viewsList.size() + " views");


        setStoryView(counter);

        // bind reverse view
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);


    }

//    private void getViewsFromDB() {
//        mDatabase.child("StoryViews").child(SharedPrefs.getUserModel().getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//                    map.clear();
//                    for (DataSnapshot storyIds : dataSnapshot.getChildren()) {
//
//                        ArrayList<StoryViewsModel> storiesList = new ArrayList<>();
//                        for (DataSnapshot users : storyIds.getChildren()) {
//                            StoryViewsModel viewsModel = users.getValue(StoryViewsModel.class);
//                            if (viewsModel != null) {
//                                storiesList.add(viewsModel);
//                            }
//
//                        }
//                        map.put(storyIds.getKey(), storiesList);
//                        viewsList = map.get(mStoriesList.get(counter).getId());
//                        adapter.notifyDataSetChanged();
//                        views.setText((viewsList == null ? 0 : viewsList.size()) + " views");
//                        viewCount.setText((viewsList == null ? 0 : viewsList.size()) + " views");
//                    }
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }


    private void showDeleteAlert() {
        storiesProgressView.pause();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to delete from story? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callDeleteApi();

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void callDeleteApi() {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", MainActivity.myArrayLists.get(counter).getId());
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ResponseBody> call = getResponse.deleteStory(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                MainActivity.myArrayLists.remove(counter);
                CommonUtils.showToast("Story Deleted");
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });


    }

    private void setStoryView(final int counter) {
        final View view = (View) mediaPlayerArrayList.get(counter);
        mVideoViewLayout.addView(view);
        time.setText(CommonUtils.getFormattedDate(MainActivity.myArrayLists.get(counter).getTime()));

        if (view instanceof VideoView) {
            final VideoView video = (VideoView) view;
//            storiesProgressView.pause();

            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mmmmedia = mediaPlayer;
                    mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                            Log.d("mediaStatus", "onInfo: =============>>>>>>>>>>>>>>>>>>>" + i);
                            switch (i) {
                                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                                    mProgressBar.setVisibility(View.GONE);
                                    storiesProgressView.startanim(counter);
                                    storiesProgressView.resume();
                                    return true;
                                }
                                case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    storiesProgressView.pause();
                                    return true;
                                }
                                case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    storiesProgressView.pause();
                                    return true;

                                }
                                case MediaPlayer.MEDIA_ERROR_TIMED_OUT: {
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    storiesProgressView.pause();
                                    return true;
                                }

                                case MediaPlayer.MEDIA_INFO_AUDIO_NOT_PLAYING: {
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    storiesProgressView.pause();
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                    video.start();
                    mProgressBar.setVisibility(View.GONE);
                    storiesProgressView.setStoryDuration(mediaPlayer.getDuration());
                    storiesProgressView.startStories(counter);
                }
            });
        } else if (view instanceof ImageView) {
            final ImageView image = (ImageView) view;
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);

            mProgressBar.setVisibility(View.GONE);
            storiesProgressView.pause();
            Glide.with(this).load(AppConfig.BASE_URL_Image + mStoriesList.get(counter).getUrl()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    image.setImageDrawable(resource);
                    mProgressBar.setVisibility(View.GONE);

                    storiesProgressView.setStoryDuration(5000);
                    storiesProgressView.startStories(counter);
                    storiesProgressView.startanim(counter);
                    storiesProgressView.resume();


                    return false;
                }
            }).into(image);
//
        }

//        viewsList = map.get(mStoriesList.get(counter).getId());
        viewsList = MainActivity.storyViewsHashMap.get(mStoriesList.get(counter).getId());
        if (viewsList == null) {
            viewsList = new ArrayList<>();
        }
        adapter = new StoryViewsAdapter(MyStoryActivity.this, viewsList);
        recyclerview.setAdapter(adapter);
        views.setText((viewsList == null ? 0 : viewsList.size()) + " views");
        viewCount.setText((viewsList == null ? 0 : viewsList.size()) + " views");
    }

    private void prepareStoriesList() {
        mStoriesList = MainActivity.myArrayLists;
//        Collections.sort(mStoriesList, new Comparator<StoryModel>() {
//            @Override
//            public int compare(StoryModel listData, StoryModel t1) {
//                Long ob1 = listData.getTime();
//                Long ob2 = t1.getTime();
//                return ob2.compareTo(ob1);
//
//            }
//        });


    }

    private VideoView getVideoView(int position) {
        VideoView video = new VideoView(this);
//        video.setVideoPath(AppConfig.BASE_URL_Videos + mStoriesList.get(position).getUrl());
        video.setVideoPath(mStoriesList.get(position).getProxyUrl());
        video.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return video;
    }

    private ImageView getImageView(int position) {
        final ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return imageView;
    }

    @Override
    public void onNext() {
        storiesProgressView.destroy();
        mVideoViewLayout.removeAllViews();
        mProgressBar.setVisibility(View.VISIBLE);
        setStoryView(++counter);
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        storiesProgressView.destroy();
        mVideoViewLayout.removeAllViews();
        mProgressBar.setVisibility(View.VISIBLE);
        setStoryView(--counter);
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    if (mmmmedia != null) {
                        try {
                            mmmmedia.pause();

                        } catch (Exception e) {

                        }
                    }
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    if (mmmmedia != null) {
                        try {
                            mmmmedia.start();

                        } catch (Exception e) {

                        }
                    }
                    return limit < now - pressTime;
            }
            return false;
        }
    };

}
