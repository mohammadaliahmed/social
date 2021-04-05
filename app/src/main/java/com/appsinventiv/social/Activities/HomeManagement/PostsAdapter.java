package com.appsinventiv.social.Activities.HomeManagement;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.social.Activities.Comments.CommentsActivity;
import com.appsinventiv.social.Activities.LikesList;
import com.appsinventiv.social.Adapters.MainSliderAdapter;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    Context context;
    private List<PostModel> dataArrayList = new ArrayList<>();
    PostAdaptersCallbacks callBacks;
    public static final int GOOGLE_AD_LAYOUT = 1;
    public static final int POST_LAYOUT = 0;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == GOOGLE_AD_LAYOUT) {
            View view = LayoutInflater.from(context).inflate(R.layout.google_ad_in_post_layout, parent, false);
            PostsAdapter.ViewHolder viewHolder = new PostsAdapter.ViewHolder(view);
            return viewHolder;
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home_post_item_layout, parent, false);
            PostsAdapter.ViewHolder viewHolder = new PostsAdapter.ViewHolder(view);
            return viewHolder;
        }
    }

    public PostsAdapter(Context context, List<PostModel> dataArrayList, PostAdaptersCallbacks callBacks) {
        this.context = context;
        this.dataArrayList = dataArrayList;
        this.callBacks = callBacks;
    }

    public void setDataArrayList(List<PostModel> dataArrayList) {
        this.dataArrayList = dataArrayList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case POST_LAYOUT:

                final PostModel model = dataArrayList.get(position);
                if (model.getUserModel().getType() == 1) {
                    holder.profileType.setVisibility(View.VISIBLE);
                } else {
                    holder.profileType.setVisibility(View.GONE);
                }
                holder.postByName.setText(model.getUserModel().getUsername());
                Glide.with(context).load(AppConfig.BASE_URL_Image + model.getUserModel().getUsername() + "/" + model.getUserModel().getThumbnailUrl()).placeholder(R.drawable.ic_profile_plc).into(holder.postByPic);
                Glide.with(context).load(AppConfig.BASE_URL_Image + SharedPrefs.getUserModel().getUsername() + "/" + SharedPrefs.getUserModel().getThumbnailUrl()).placeholder(R.drawable.ic_profile_plc).into(holder.commenterImg);
                holder.likesCount.setText(model.getLikesCount() + " likes");
                if (HomeFragment.likesList.contains(model.getId())) {
                    holder.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_fill));
                    model.setLiked(true);
                } else {
                    holder.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_empty));
                    model.setLiked(false);

                }


                if (model.getPostType().equalsIgnoreCase("Image")) {
                    holder.mainImage.setVisibility(View.VISIBLE);
                    holder.slider.setVisibility(View.GONE);
                    holder.picCount.setVisibility(View.GONE);
                    holder.dots_indicator.setVisibility(View.GONE);
                    Glide.with(context).load(AppConfig.BASE_URL_Image +model.getUserModel().getUsername()+"/"+ model.getImagesUrl()).into(holder.mainImage);
                } else if (model.getPostType().equalsIgnoreCase("multi")) {
                    holder.slider.setVisibility(View.VISIBLE);
                    holder.mainImage.setVisibility(View.GONE);
                    holder.dots_indicator.setVisibility(View.VISIBLE);
                    holder.picCount.setVisibility(View.VISIBLE);
                    final List<String> list = new ArrayList<String>(Arrays.asList(model.getImagesUrl().split(",")));
                    holder.picCount.setText(1 + "/" + (list == null ? 1 : list.size()));

                    MainSliderAdapter mViewPagerAdapter = new MainSliderAdapter(context, list,model.getUserModel().getUsername(), new MainSliderAdapter.ClicksCallback() {
                        @Override
                        public void onDoubleClick() {

                            performLikeOperation(holder, model);


//                    likePost(finalLiked[0], viewHolders, model);
//                    if (liked[0]) {
//                        model.setLiked(false);
//                        liked[0] = false;
//                        finalLiked[0] = false;
//                        viewHolders.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_empty));
//
//                    } else {
//                        model.setLiked(true);
//                        liked[0] = true;
//                        finalLiked[0] = true;
//                        viewHolders.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_fill));
//
//                    }
//                    viewHolders.likesCount.setText(model.getLikesCount() + " likes");
//                    viewHolders.showLike.setVisibility(View.GONE);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    // yourMethod();
                                    holder.showLike.setVisibility(View.GONE);
                                }
                            }, 2500);
                        }

                        @Override
                        public void onPicChanged(int position) {
//                    viewHolders.picCount.setText((position) + "/" + list.size());
                        }
                    });
                    holder.slider.setAdapter(mViewPagerAdapter);
//            mViewPagerAdapter.notifyDataSetChanged();
                    holder.dots_indicator.setViewPager(holder.slider);
                    holder.slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            holder.picCount.setText((position + 1) + "/" + list.size());

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });

                }
                if (model.getLastComment() != null && model.getLastComment().size() > 0) {
                    String sourceString = "<b>" + model.getLastComment().get(0).getName() + "</b> " + model.getLastComment().get(0).getText();
                    holder.lastComment.setText(Html.fromHtml(sourceString));
                    holder.lastComment.setVisibility(View.VISIBLE);

                } else {

                    holder.lastComment.setVisibility(View.GONE);

                }

                holder.likesCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, LikesList.class);
                        i.putExtra("postId", model.getId());
                        context.startActivity(i);
                    }
                });


                holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        performLikeOperation(holder, model);
                    }
                });


                holder.commentsCount.setText("View " + model.getCommentsCount() + " comments");
//        holder.likesCount.setText(model.getLikesCount() + " likes");
                holder.time.setText(CommonUtils.getFormattedDate(model.getTime()));
                holder.postByPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takeUserToProfile(model.getUserId());
                    }
                });
                holder.postByName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takeUserToProfile(model.getUserId());

                    }
                });
                holder.menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(context);
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
                                callBacks.onDelete(model, position);
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
//                        context.startActivity(Intent.createChooser(shareIntent, "Share post via.."));
                                CommonUtils.shareUrl(context, "post", model.getRandom_id());
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
//                                context.startActivity(intent);
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
                holder.addComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, CommentsActivity.class);
                        i.putExtra("postId", model.getId());
                        context.startActivity(i);
                    }
                });
                holder.lastComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, CommentsActivity.class);
                        i.putExtra("postId", model.getId());
                        context.startActivity(i);
                    }
                });
                holder.comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, CommentsActivity.class);
                        i.putExtra("postId", model.getId());
                        context.startActivity(i);
                    }
                });

                holder.mainImage.setOnTouchListener(new View.OnTouchListener() {
                    private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            performLikeOperation(holder, model);

//                    likePost(finalLiked[0], viewHolders, model);

//                    if (liked[0]) {
//                        model.setLiked(false);
//                        liked[0] = false;
//                        finalLiked[0] = false;
//                        viewHolders.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_empty));
//
//                    } else {
//                        model.setLiked(true);
//                        liked[0] = true;
//                        finalLiked[0] = true;
//                        viewHolders.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_fill));
//
//                    }
//                    viewHolders.likesCount.setText(model.getLikesCount() + " likes");

//                    viewHolders.showLike.setVisibility(View.GONE);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    // yourMethod();
                                    holder.showLike.setVisibility(View.GONE);
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
                break;
            case GOOGLE_AD_LAYOUT:
                AdRequest adRequest = new AdRequest.Builder().build();
                holder.adView.loadAd(adRequest);
                break;
            default:
        }
    }

    private void performLikeOperation(ViewHolder holder, PostModel model) {
        holder.showLike.setVisibility(View.VISIBLE);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fadein);
        holder.showLike.startAnimation(myFadeInAnimation);
        callBacks.onLikedPost(model);
        if (model.isLiked()) {
            holder.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_empty));
            model.setLiked(false);
            model.setLikesCount(model.getLikesCount() - 1);
            holder.likesCount.setText((model.getLikesCount()) + " likes");

        } else {
            holder.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_fill));
            model.setLiked(true);
            model.setLikesCount(model.getLikesCount() + 1);
            holder.likesCount.setText((model.getLikesCount()) + " likes");

        }

    }

    public void takeUserToProfile(int userId) {
        if (userId == SharedPrefs.getUserModel().getId()) {
            callBacks.takeUserToMyUserProfile(userId);
        } else {
            callBacks.takeUserToOtherUserProfile(userId);
        }
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
//        if ( position % 10 == 0)
//            return GOOGLE_AD_LAYOUT;
//        return AD_LAYOUT;

        return (position + 1) % 7 == 0 ? GOOGLE_AD_LAYOUT : POST_LAYOUT;

//        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView postByName, likesCount, time, addComment, lastComment, commentsCount, picCount, profileType;
        ImageView mainImage, showLike;
        CircleImageView commenterImg, postByPic, flag;
        ImageView muteIcon, comments, likeBtn, menu, forward, download, repost;
        ViewPager slider;
        WormDotsIndicator dots_indicator;
        AdView adView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileType = itemView.findViewById(R.id.profileType);
            slider = itemView.findViewById(R.id.slider);
            dots_indicator = itemView.findViewById(R.id.dots_indicator);
            picCount = itemView.findViewById(R.id.picCount);
            postByName = itemView.findViewById(R.id.postByName);
            likesCount = itemView.findViewById(R.id.likesCount);
            time = itemView.findViewById(R.id.time);
            addComment = itemView.findViewById(R.id.addComment);
            lastComment = itemView.findViewById(R.id.lastComment);
            commentsCount = itemView.findViewById(R.id.commentsCount);
            mainImage = itemView.findViewById(R.id.mainImage);
            commenterImg = itemView.findViewById(R.id.commenterImg);
            postByPic = itemView.findViewById(R.id.postByPic);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            comments = itemView.findViewById(R.id.comments);
            menu = itemView.findViewById(R.id.menu);
            forward = itemView.findViewById(R.id.forward);
            showLike = itemView.findViewById(R.id.showLike);
            adView = itemView.findViewById(R.id.adView);


        }
    }
}
