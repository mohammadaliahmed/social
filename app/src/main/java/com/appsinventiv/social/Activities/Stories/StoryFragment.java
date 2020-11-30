package com.appsinventiv.social.Activities.Stories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.appsinventiv.social.Activities.MainActivity;
import com.appsinventiv.social.Activities.OtherUser.ViewProfile;
import com.appsinventiv.social.Models.StoryModel;
import com.appsinventiv.social.NetworkResponses.ApiResponse;
import com.appsinventiv.social.NetworkResponses.NewMessageResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.Constants;
import com.appsinventiv.social.Utils.KeyboardUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class StoryFragment extends Fragment implements StoriesProgressView.StoriesListener {
    Context context;


    RelativeLayout storyyy;
    int position;

    private int PROGRESS_COUNT = 0;
    private StoriesProgressView storiesProgressView;
    private ProgressBar mProgressBar;
    private LinearLayout mVideoViewLayout;
    private int counter = 0;
    private ArrayList<StoryModel> mStoriesList = new ArrayList<>();

    private ArrayList<View> mediaPlayerArrayList = new ArrayList<>();

    long pressTime = 0L;
    long limit = 500L;
    MediaPlayer mmmmedia;

    CircleImageView storyByPic, userPic;
    TextView storyByName, time;
    EditText message;

    ImageView send;
    RelativeLayout viewStoryBy;


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
    private VideoView video1;

    @SuppressLint("ValidFragment")
    public StoryFragment() {

    }

    @SuppressLint("ValidFragment")
    public StoryFragment(Context context, int position, int count) {
        this.context = context;
        this.position = position;
        this.PROGRESS_COUNT = count;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_story, null);
        storyyy = rootView.findViewById(R.id.storyyy);
        viewStoryBy = rootView.findViewById(R.id.viewStoryBy);
        userPic = rootView.findViewById(R.id.userPic);
        storyByPic = rootView.findViewById(R.id.storyByPic);
        storyByName = rootView.findViewById(R.id.storyByName);
        time = rootView.findViewById(R.id.time);
        message = rootView.findViewById(R.id.message);
        send = rootView.findViewById(R.id.send);

        viewStoryBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Constants.USER_ID = MainActivity.arrayLists.get(position).get(0).getStoryByUsername();
                Intent i = new Intent(context, ViewProfile.class);
                i.putExtra("userId", mStoriesList.get(position).getUserId());
                context.startActivity(i);
            }
        });



        KeyboardUtils.addKeyboardToggleListener(getActivity(), new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {
                    storiesProgressView.pause();
                } else {
                    storiesProgressView.resume();
                }
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().length() == 0) {
                    message.setError("Enter message");
                } else {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    sendStoryReply(message.getText().toString(), mStoriesList.get(counter));
                }
            }
        });


        mProgressBar = rootView.findViewById(R.id.progressBar);
        mVideoViewLayout = rootView.findViewById(R.id.videoView);
        storiesProgressView = (StoriesProgressView) rootView.findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(PROGRESS_COUNT);


        if (SharedPrefs.getUserModel().getPicUrl() != null) {
            Glide.with(context).load(AppConfig.BASE_URL_Image + SharedPrefs.getUserModel().getThumbnailUrl()).into(userPic);
        }


        prepareStoriesList();
        storiesProgressView.setStoriesListener(this);

        for (int i = 0; i < mStoriesList.size(); i++) {
            if (mStoriesList.get(i).getUrl().contains("mp4")) {

                mediaPlayerArrayList.add(getVideoView(i));
            } else if (mStoriesList.get(i).getUrl().contains("jpg")) {
                mediaPlayerArrayList.add(getImageView(i));
            }
        }

        setStoryView(counter);

        // bind reverse view
        View reverse = rootView.findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = rootView.findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
        return rootView;
    }

    private void sendStoryReply(String msg, StoryModel model) {
//        CommonUtils.showToast(s);
        message.setText("");
        KeyboardUtils.forceCloseKeyboard(storyyy);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("messageText", msg);
        map.addProperty("messageType", Constants.MESSAGE_TYPE_STORY);
        map.addProperty("imageUrl", model.getUrl());
        map.addProperty("messageByName", SharedPrefs.getUserModel().getName());
        map.addProperty("messageById", SharedPrefs.getUserModel().getId());
        map.addProperty("hisUserId", model.getUserId());
        map.addProperty("storyId", model.getId());
        map.addProperty("userIds", SharedPrefs.getUserModel().getId() + "," + model.getUserId());

        map.addProperty("time", model.getTime());
        Call<ApiResponse> call = getResponse.sendStoryMessage(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                CommonUtils.showToast("Reply sent");

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        storiesProgressView.resume();

    }

    @Override
    public void onPause() {
        super.onPause();
        storiesProgressView.pause();
    }

    private void setStoryView(final int counter) {
        final View view = (View) mediaPlayerArrayList.get(counter);

        mVideoViewLayout.addView(view);
        time.setText(CommonUtils.getFormattedDate(MainActivity.arrayLists.get(position).get(counter).getTime()));


//        HashMap<String, String> smap = SharedPrefs.getStorySeenMap();
//        if (smap != null && smap.size() > 0) {
//            if (!smap.containsKey(mStoriesList.get(counter).getId())) {
//                StoryViewsModel viewsModel = new StoryViewsModel(
//                        mStoriesList.get(counter).getId(),
//                        SharedPrefs.getUserModel().getName(),
//                        SharedPrefs.getUserModel().getThumbnailUrl(),
//                        System.currentTimeMillis()
//
//                );
//                mDatabase.child("StoryViews")
//                        .child(MainActivity.arrayLists.get(position).get(counter).getStoryByUsername()).
//                        child(MainActivity.arrayLists.get(position).get(counter).getId()).child(SharedPrefs.getUserModel().getUsername())
//                        .setValue(viewsModel);
//
//
//            }
//        }
        callStoryViewApi(MainActivity.arrayLists.get(position).get(counter).getId());
//

        if (view instanceof VideoView) {


            final VideoView video = (VideoView) view;
            storiesProgressView.pause();
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
                    storiesProgressView.startanim(counter);

                    storiesProgressView.startStories(counter);
                }
            });
        } else if (view instanceof ImageView) {
            final ImageView image = (ImageView) view;
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);

            mProgressBar.setVisibility(View.GONE);
            storiesProgressView.pause();
            Glide.with(this).load(AppConfig.BASE_URL_Image + mStoriesList.get(counter).getUrl()).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    image.setImageDrawable(resource);
                    mProgressBar.setVisibility(View.GONE);
//                    storiesProgressView.startanim(counter);

                    storiesProgressView.setStoryDuration(5000);
                    storiesProgressView.startStories(counter);
                    storiesProgressView.startanim(counter);

                    storiesProgressView.resume();
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }
//        HashMap<String, String> map = SharedPrefs.getStorySeenMap();
//        if (map != null && map.size() > 0) {
//            map.put(mStoriesList.get(counter).getId(), mStoriesList.get(counter).getId());
//            SharedPrefs.setStorySeenMap(map);
//        } else {
//            map = new HashMap<>();
//            map.put(mStoriesList.get(counter).getId(), mStoriesList.get(counter).getId());
//            SharedPrefs.setStorySeenMap(map);
//        }


    }

    private void callStoryViewApi(Integer storyId) {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("user_id", SharedPrefs.getUserModel().getId());
        map.addProperty("story_id", storyId);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ResponseBody> call = getResponse.addStoryView(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });

    }

    private void prepareStoriesList() {
        mStoriesList = MainActivity.arrayLists.get(position);
        Glide.with(context).load(AppConfig.BASE_URL_Image + MainActivity.arrayLists.get(position).get(0).getUser().getThumbnailUrl()).into(storyByPic);
        storyByName.setText(MainActivity.arrayLists.get(position).get(0).getUser().getName());
        long dur = System.currentTimeMillis() - (MainActivity.arrayLists.get(position).get(counter).getTime());


    }

    private VideoView getVideoView(int position) {
        VideoView video = new VideoView(context);
        video.setVideoPath(AppConfig.BASE_URL_Videos + mStoriesList.get(position).getUrl());
//        video.setVideoPath(mStoriesList.get(position).getProxyUrl());

        video.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return video;
    }

    private ImageView getImageView(int position) {
        final ImageView imageView = new ImageView(context);
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


        sendMessage();

//        StoryActivity.activity.finish();
    }

    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("storyPosition");
        // You can also include some extra data.
        intent.putExtra("storyPosition", (position + 1));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    @Override
    public void onDestroyView() {
        storiesProgressView.destroy();
        super.onDestroyView();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }


}
