package com.appsinventiv.social.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.social.Activities.Comments.CommentsActivity;
import com.appsinventiv.social.Activities.HomeManagement.HomeFragment;
import com.appsinventiv.social.Activities.HomeManagement.PostsAdapter;
import com.appsinventiv.social.Activities.OtherUser.ViewProfile;
import com.appsinventiv.social.Activities.UserManagement.LoginActivity;
import com.appsinventiv.social.Adapters.MainSliderAdapter;
import com.appsinventiv.social.CommonFunctions;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.NetworkResponses.AllPostsResponse;
import com.appsinventiv.social.NetworkResponses.AllStoriesResponse;
import com.appsinventiv.social.NetworkResponses.PostResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.Constants;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPost extends AppCompatActivity {


    int postId;
    private PostModel model;
    TextView postByName, likesCount, time, addComment, lastComment, commentsCount, picCount, duration;
    ImageView mainImage, showLike;
    CircleImageView commenterImg, postByPic, flag;
    ImageView muteIcon, comments, likeBtn, menu, forward, download, repost;
    ViewPager slider;
    WormDotsIndicator dots_indicator;
    private List<Integer> likesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        postId = getIntent().getIntExtra("postId", 0);

        slider = findViewById(R.id.slider);
        dots_indicator = findViewById(R.id.dots_indicator);
        picCount = findViewById(R.id.picCount);
        postByName = findViewById(R.id.postByName);
        likesCount = findViewById(R.id.likesCount);
        time = findViewById(R.id.time);
        addComment = findViewById(R.id.addComment);
        lastComment = findViewById(R.id.lastComment);
        commentsCount = findViewById(R.id.commentsCount);
        mainImage = findViewById(R.id.mainImage);
        commenterImg = findViewById(R.id.commenterImg);
        postByPic = findViewById(R.id.postByPic);
        likeBtn = findViewById(R.id.likeBtn);
        comments = findViewById(R.id.comments);
        menu = findViewById(R.id.menu);
        forward = findViewById(R.id.forward);
        showLike = findViewById(R.id.showLike);


        getDataFromServer();

    }

    private void getDataFromServer() {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("post_id", postId);
        map.addProperty("user_id", SharedPrefs.getUserModel().getId());
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<AllPostsResponse> call = getResponse.viewPost(map);
        call.enqueue(new Callback<AllPostsResponse>() {
            @Override
            public void onResponse(Call<AllPostsResponse> call, Response<AllPostsResponse> response) {
                if (response.code() == 200) {
                    List<PostModel> data = response.body().getPosts();
                    likesList = response.body().getLikesList() == null ? new ArrayList<>() : response.body().getLikesList();
                    if (data != null && data.size() > 0) {
                        model = data.get(0);
                        setupUI();
                    }
                }
            }

            @Override
            public void onFailure(Call<AllPostsResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });


    }

    private void setupUI() {

        likesCount.setText(model.getLikesCount() + " likes");

        postByName.setText(model.getUserModel().getName());
        Glide.with(this).load(AppConfig.BASE_URL_Image+model.getUserModel().getThumbnailUrl()).into(postByPic);
        Glide.with(this).load(AppConfig.BASE_URL_Image+SharedPrefs.getUserModel().getThumbnailUrl()).into(commenterImg);
        if (HomeFragment.likesList.contains(model.getId())) {
            likeBtn.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_like_fill));
            model.setLiked(true);
        } else {
            likeBtn.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_like_empty));
            model.setLiked(false);

        }


        if (model.getPostType().equalsIgnoreCase("Image")) {
            mainImage.setVisibility(View.VISIBLE);
            slider.setVisibility(View.GONE);
            picCount.setVisibility(View.GONE);
            dots_indicator.setVisibility(View.GONE);
            Glide.with(this).load(AppConfig.BASE_URL_Image + model.getImagesUrl()).into(mainImage);
        } else if (model.getPostType().equalsIgnoreCase("multi")) {
            slider.setVisibility(View.VISIBLE);
            mainImage.setVisibility(View.GONE);
            dots_indicator.setVisibility(View.VISIBLE);
            picCount.setVisibility(View.VISIBLE);
            final List<String> list = new ArrayList<String>(Arrays.asList(model.getImagesUrl().split(",")));
            picCount.setText(1 + "/" + (list == null ? 1 : list.size()));

            MainSliderAdapter mViewPagerAdapter = new MainSliderAdapter(this, list, new MainSliderAdapter.ClicksCallback() {
                @Override
                public void onDoubleClick() {

                    performLikeOperation(model);


//                    likePost(finalLiked[0], viewHolders, model);
//                    if (liked[0]) {
//                        model.setLiked(false);
//                        liked[0] = false;
//                        finalLiked[0] = false;
//                        viewHolders.likeBtn.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_like_empty));
//
//                    } else {
//                        model.setLiked(true);
//                        liked[0] = true;
//                        finalLiked[0] = true;
//                        viewHolders.likeBtn.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_like_fill));
//
//                    }
//                    viewHolders.likesCount.setText(model.getLikesCount() + " likes");
//                    viewHolders.showLike.setVisibility(View.GONE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // yourMethod();
                            showLike.setVisibility(View.GONE);
                        }
                    }, 2500);
                }

                @Override
                public void onPicChanged(int position) {
//                    viewHolders.picCount.setText((position) + "/" + list.size());
                }
            });
            slider.setAdapter(mViewPagerAdapter);
//            mViewPagerAdapter.notifyDataSetChanged();
            dots_indicator.setViewPager(slider);
            slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    picCount.setText((position + 1) + "/" + list.size());

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }
        if (model.getLastComment() != null && model.getLastComment().size() > 0) {
            String sourceString = "<b>" + model.getLastComment().get(0).getName() + "</b> " + model.getLastComment().get(0).getText();
            lastComment.setText(Html.fromHtml(sourceString));
            lastComment.setVisibility(View.VISIBLE);

        } else {

            lastComment.setVisibility(View.GONE);

        }

        likesCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewPost.this, LikesList.class);
                i.putExtra("postId", model.getId());
                startActivity(i);
            }
        });


        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLikeOperation(model);
            }
        });


        commentsCount.setText("View " + model.getCommentsCount() + " comments");
//        likesCount.setText(model.getLikesCount() + " likes");
        time.setText(CommonUtils.getFormattedDate(model.getTime()));
        postByPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeUserToProfile(model.getUserId());
            }
        });
        postByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeUserToProfile(model.getUserId());

            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ViewPost.this);
                dialog.setContentView(R.layout.pop_up_menu);


                TextView shareLink = dialog.findViewById(R.id.shareLink);
                TextView copyLink = dialog.findViewById(R.id.copyLink);
                TextView delete = dialog.findViewById(R.id.delete);


                if (model.getUserId() == SharedPrefs.getUserModel().getId()) {
                    delete.setVisibility(View.VISIBLE);
                } else {
                    delete.setVisibility(View.GONE);
                }


                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
//                        callBacks.onDelete(model, position);
                    }
                });
                copyLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String url = AppConfig.BASE_URL + model.getRandom_id();
                        CommonUtils.showToast("Copied to clipboard");
                    }
                });
                shareLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
//                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//
//                        shareIntent.setType("text/plain");
//                        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://umetechnology.com/" + model.getId());
//                        startActivity(Intent.createChooser(shareIntent, "Share post via.."));
                        CommonUtils.shareUrl(ViewPost.this, "post", model.getRandom_id());
                    }
                });

//                download.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        if (!model.getType().equalsIgnoreCase("multi")) {
//                            String filen = (model.getType().equalsIgnoreCase("image") ? model.getPictureUrl().substring(model.getPictureUrl().length() - 10, model.getPictureUrl().length()) + ".jpg" :
//                                    model.getVideoUrl().substring(model.getVideoUrl().length() - 10, model.getVideoUrl().length()) + ".mp4");
//                            File applictionFile = new File(Environment.getExternalStoragePublicDirectory(
//                                    Environment.DIRECTORY_DOWNLOADS) + "/" + filen
//                            );
//                            if (applictionFile != null && applictionFile.exists()) {
//                                Intent intent = new Intent();
//                                intent.setAction(Intent.ACTION_VIEW);
//                                intent.setDataAndType(Uri.fromFile(applictionFile), getMimeType(applictionFile.getAbsolutePath()));
//                                startActivity(intent);
//
//                            } else {
//                                DownloadFile.fromUrll((model.getType().equalsIgnoreCase("image")
//                                        ? model.getPictureUrl() : model.getVideoUrl()), filen);
//                                callBacks.onFileDownload(filen);
//
//                            }
//                        }
//                    }
//                });


                dialog.show();
            }
        });
//
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewPost.this, CommentsActivity.class);
                i.putExtra("postId", model.getId());
                startActivity(i);
            }
        });
        lastComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewPost.this, CommentsActivity.class);
                i.putExtra("postId", model.getId());
                startActivity(i);
            }
        });
        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewPost.this, CommentsActivity.class);
                i.putExtra("postId", model.getId());
                startActivity(i);
            }
        });

        mainImage.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(ViewPost.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    performLikeOperation(model);

//                    likePost(finalLiked[0], viewHolders, model);

//                    if (liked[0]) {
//                        model.setLiked(false);
//                        liked[0] = false;
//                        finalLiked[0] = false;
//                        viewHolders.likeBtn.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_like_empty));
//
//                    } else {
//                        model.setLiked(true);
//                        liked[0] = true;
//                        finalLiked[0] = true;
//                        viewHolders.likeBtn.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_like_fill));
//
//                    }
//                    viewHolders.likesCount.setText(model.getLikesCount() + " likes");

//                    viewHolders.showLike.setVisibility(View.GONE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // yourMethod();
                            showLike.setVisibility(View.GONE);
                        }
                    }, 2500);
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    private void performLikeOperation(PostModel model) {
        showLike.setVisibility(View.VISIBLE);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        showLike.startAnimation(myFadeInAnimation);
        CommonFunctions.likePost(model);
        if (model.isLiked()) {
            likeBtn.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_like_empty));
            model.setLiked(false);
            model.setLikesCount(model.getLikesCount() - 1);
            likesCount.setText((model.getLikesCount()) + " likes");

        } else {
            likeBtn.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_like_fill));
            model.setLiked(true);
            model.setLikesCount(model.getLikesCount() + 1);
            likesCount.setText((model.getLikesCount()) + " likes");

        }
    }

    public void takeUserToProfile(int userId) {
        if (userId == SharedPrefs.getUserModel().getId()) {
//            callBacks.takeUserToMyUserProfile(userId);
        } else {
            Intent i = new Intent(ViewPost.this, ViewProfile.class);
            i.putExtra("userId", userId);
            startActivity(i);
        }
    }


}
