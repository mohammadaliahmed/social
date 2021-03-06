package com.appsinventiv.social.Activities.HomeManagement;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.social.Adapters.MainSliderAdapter;
import com.appsinventiv.social.Activities.Comments.CommentsActivity;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


/**
 * Created by aliah 13/08/2019.
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class VideoRecyclerAdapter {
//        extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CacheManager, PlayerSelector {
//
//    private Context context;
//    private boolean isMuteByDefault;
//    private List<PostModel> dataArrayList = new ArrayList<>();
//    private boolean isErrorView = false;
//    private final int TYPE_ERROR = -1;
//    private final int TYPE_CELL = 1;
//    private final int TYPE_IMAGE_POST = 10;
//    private final int TYPE_VIDEO_POST = 11;
//    private final int TYPE_MULTI_POST = 11;
//    private final int TYPE_CELL_YT = 2;
//    private int isShowAll = 0;
//    private final int VIEW_TYPE_LOADING = 3;
//    private boolean isHorizontalView;
//
//    public static List<String> items;
//    private String message;
//
//    private final long initTimeStamp;
//    @Nullable
//    public CallbackVideo callback;
//    PostAdaptersCallbacks callBacks;
//    private ArrayList<String> likeList = new ArrayList<>();
//
//
//    public void setLikeList(ArrayList<String> likeList) {
//        this.likeList = likeList;
//        notifyDataSetChanged();
//    }
//
//    public void setDataArrayList(List<PostModel> dataArrayList) {
//        this.dataArrayList = dataArrayList;
//        notifyDataSetChanged();
//    }
//
//    @SuppressWarnings("WeakerAccess")
//    PlayerSelector origin;
//    // Keep a cache of the Playback order that is manually paused by User.
//    // So that if User scroll to it again, it will not start play.
//    // Value will be updated by the ViewHolder.
//    final AtomicInteger lastUserPause = new AtomicInteger(-1);
//
//
//    //youtube
//
//
//    public void setAndShowError(String message, boolean isErrorView) {
//        this.message = message;
//        this.isErrorView = isErrorView;
//        notifyDataSetChanged();
//
//    }
//
//
//    public VideoRecyclerAdapter(Context context,
//                                List<PostModel> dataArrayList,
//                                boolean isMuteByDefault,
//                                long initTimeStamp, PostAdaptersCallbacks callBacks
//    ) {
//        this.context = context;
//        this.initTimeStamp = initTimeStamp;
//        this.isMuteByDefault = isMuteByDefault;
//        this.dataArrayList = dataArrayList;
//        setHasStableIds(true);
//        this.callBacks = callBacks;
//    }
//
//    public void setCallback(@Nullable CallbackVideo callback) {
//        this.callback = callback;
//    }
//
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//
//    @Override
//    public int getItemViewType(int position) {
//
//        return TYPE_CELL;
//
//    }
//
//    @Override
//    public int getItemCount() {
//
//        if (dataArrayList != null && dataArrayList.size() > 0) {
//            if (dataArrayList.size() < 30) {
//                return dataArrayList.size();
//            } else {
//                return 30;
//            }
//        } else {
//            return 0;
//        }
////        int size = (dataArrayList != null ? dataArrayList.size() : 0);
////        if (isErrorView) {
////            size = 1;
////        }
////
////        return size;
//    }
//
//    public void swap(ArrayList<PostModel> dataArrayList) {
//        try {
//            this.dataArrayList = dataArrayList;
//            if (dataArrayList.size() > 0) {
//                isErrorView = false;
//            }
//            notifyDataSetChanged();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        View view = null;
//        view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.home_post_item_layout, parent, false);
//        VideoViewHolder
//                viewHolder = new VideoViewHolder(view, context);
//
//        return viewHolder;
//
//
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mVHolder, int position) {
//        PostModel model = dataArrayList.get(position);
//
//        populateAdapterView((VideoViewHolder) mVHolder, position);
//
//
//    }
//
//    @Override
//    public void onViewRecycled(@NonNull RecyclerView.ViewHolder viewHolders) {
//        try {
//            if (viewHolders instanceof VideoViewHolder)
//                ((VideoViewHolder) viewHolders).onRecycled();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public static abstract class CallbackVideo {
//
//        public abstract void onItemClick(@NonNull VideoViewHolder viewHolder, @NonNull View view,
//                                         @NonNull PostModel item, int position);
//    }
//
//    // Implement the CacheManager;
//    @Nullable
//    @Override
//    public Object getKeyForOrder(int order) {
//        return getItem(order);
//    }
//
//    @Nullable
//    @Override
//    public Integer getOrderForKey(@NonNull Object key) {
//        return key instanceof PostModel ? dataArrayList.indexOf(key) : null;
//    }
//
//
//    @NonNull
//    @Override
//    public Collection<ToroPlayer> select(@NonNull Container container,
//                                         @NonNull List<ToroPlayer> items) {
//        Collection<ToroPlayer> originalResult = origin.select(container, items);
//        ArrayList<ToroPlayer> result = new ArrayList<>(originalResult);
//        if (lastUserPause.get() >= 0) {
//            for (Iterator<ToroPlayer> it = result.iterator(); it.hasNext(); ) {
//                if (it.next().getPlayerOrder() == lastUserPause.get()) {
//                    it.remove();
//                    break;
//                }
//            }
//        }
//
//        return result;
//    }
//
//    @NonNull
//    @Override
//    public PlayerSelector reverse() {
//        return origin.reverse();
//    }
//
//
//    private PostModel getItem(final int position) {
//        return dataArrayList.get(position);
//    }
//
//
//    @SuppressLint({"DefaultLocale", "ClickableViewAccessibility"})
//    private void populateAdapterView(final VideoViewHolder viewHolders, final int position) {
//        final PostModel model = getItem(position);
//        viewHolders.postByName.setText(model.getUserModel().getName());
//
//        Glide.with(context).load(AppConfig.BASE_URL_Image + model.getUserModel().getThumbnailUrl()).into(viewHolders.postByPic);
//        Glide.with(context).load(AppConfig.BASE_URL_Image + SharedPrefs.getUserModel().getThumbnailUrl()).into(viewHolders.commenterImg);
//
////        if (model.getPostType().equalsIgnoreCase("image")) {
////            Glide.with(context).load(model.getImagesUrl()).into(holder.mainImage);
////
////
////        } else {
////
////        }
//
////        if (viewHolders.helper != null) {
////            if (viewHolders.helper.getVolume() == 0) {
////                viewHolders.helper.setVolume(1);
////            } else {
////                viewHolders.helper.setVolume(0);
////            }
////        }
////
////
//        if (SharedPrefs.getMuted().equalsIgnoreCase("yes")) {
//            Log.d("muted inside yes", SharedPrefs.getMuted());
//
//            viewHolders.imageView_sound.setImageResource(R.drawable.ic_mute);
//            viewHolders.getCurrentPlaybackInfo().setVolumeInfo(new VolumeInfo(true, 0.0f));
//            model.setMute(true);
//        } else {
//            Log.d("muted inside no", SharedPrefs.getMuted());
//
//            viewHolders.imageView_sound.setImageResource(R.drawable.ic_unmute);
//            viewHolders.getCurrentPlaybackInfo().setVolumeInfo(new VolumeInfo(false, 1.0f));
//            model.setMute(false);
//        }
////
////        try {
////
////
//        viewHolders.bind(model, position);
////
////
//        viewHolders.imageView_sound
//                .setOnClickListener(new ClickHandler(model, viewHolders, position));
////
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////
////        final boolean[] liked = {false};
////
////
////        if (likeList != null) {
////            if (likeList.size() > 0) {
////                if (likeList.contains(model.getId())) {
////                    liked[0] = true;
////                }
////            }
////        }
////        if (liked[0]) {
////            viewHolders.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_fill));
//////            model.setLiked(true);
////        } else {
////            viewHolders.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_empty));
//////            model.setLiked(false);
////
////        }
////
////        final boolean[] finalLiked = {liked[0]};
////
////        Glide.with(context).load(SharedPrefs.getUserModel().getThumbnailUrl()).into(viewHolders.commenterImg);
//        if (model.getPostType().equalsIgnoreCase("Image")) {
//            viewHolders.video_view.setVisibility(View.GONE);
//            viewHolders.mainImage.setVisibility(View.VISIBLE);
//            viewHolders.imageView_sound.setVisibility(View.GONE);
//
//            viewHolders.muteIcon.setVisibility(View.GONE);
//            viewHolders.dots_indicator.setVisibility(View.GONE);
//            viewHolders.slider.setVisibility(View.GONE);
//            viewHolders.dots_indicator.setVisibility(View.GONE);
//
//            Glide.with(context).load(AppConfig.BASE_URL_Image + model.getImagesUrl()).into(viewHolders.mainImage);
//
//        } else if (model.getPostType().equalsIgnoreCase("video")) {
//            viewHolders.video_view.setVisibility(View.VISIBLE);
//            viewHolders.mainImage.setVisibility(View.GONE);
//            viewHolders.muteIcon.setVisibility(View.VISIBLE);
//            viewHolders.dots_indicator.setVisibility(View.GONE);
//            viewHolders.slider.setVisibility(View.GONE);
//            viewHolders.picCount.setVisibility(View.GONE);
//            viewHolders.dots_indicator.setVisibility(View.GONE);
//
//            viewHolders.imageView_sound.setVisibility(View.VISIBLE);
//
//
//        } else if (model.getPostType().equalsIgnoreCase("multi")) {
//            viewHolders.slider.setOffscreenPageLimit(0);
//            viewHolders.video_view.setVisibility(View.GONE);
//            viewHolders.mainImage.setVisibility(View.GONE);
//            viewHolders.picCount.setVisibility(View.VISIBLE);
//            viewHolders.muteIcon.setVisibility(View.GONE);
//            viewHolders.dots_indicator.setVisibility(View.VISIBLE);
//            viewHolders.imageView_sound.setVisibility(View.GONE);
//            viewHolders.slider.setVisibility(View.VISIBLE);
////            this.items = model.getImagesUrl();
//            final List<String> list = new ArrayList<String>(Arrays.asList(model.getImagesUrl().split(",")));
//            viewHolders.picCount.setText(1 + "/" + (list == null ? 1 : list.size()));
//
//            MainSliderAdapter mViewPagerAdapter = new MainSliderAdapter(context, list, new MainSliderAdapter.ClicksCallback() {
//                @Override
//                public void onDoubleClick() {
//
//                    viewHolders.showLike.setVisibility(View.VISIBLE);
////                    Animation myFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fadein);
////                    viewHolders.showLike.startAnimation(myFadeInAnimation);
////                    likePost(finalLiked[0], viewHolders, model);
////                    if (liked[0]) {
////                        model.setLiked(false);
////                        liked[0] = false;
////                        finalLiked[0] = false;
////                        viewHolders.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_empty));
////
////                    } else {
////                        model.setLiked(true);
////                        liked[0] = true;
////                        finalLiked[0] = true;
////                        viewHolders.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_fill));
////
////                    }
////                    viewHolders.likesCount.setText(model.getLikesCount() + " likes");
////                    viewHolders.showLike.setVisibility(View.GONE);
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        public void run() {
//                            // yourMethod();
//                            viewHolders.showLike.setVisibility(View.GONE);
//                        }
//                    }, 2500);
//                }
//
//                @Override
//                public void onPicChanged(int position) {
////                    viewHolders.picCount.setText((position) + "/" + list.size());
//                }
//            });
//            viewHolders.slider.setAdapter(mViewPagerAdapter);
////            mViewPagerAdapter.notifyDataSetChanged();
//            viewHolders.dots_indicator.setViewPager(viewHolders.slider);
//            viewHolders.slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                @Override
//                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                }
//
//                @Override
//                public void onPageSelected(int position) {
//                    viewHolders.picCount.setText((position + 1) + "/" + list.size());
//
//                }
//
//                @Override
//                public void onPageScrollStateChanged(int state) {
//
//                }
//            });
//
//
//        } else {
//            viewHolders.video_view.setVisibility(View.GONE);
//            viewHolders.mainImage.setVisibility(View.GONE);
//            viewHolders.slider.setVisibility(View.GONE);
//
//            viewHolders.muteIcon.setVisibility(View.GONE);
//            viewHolders.dots_indicator.setVisibility(View.GONE);
//        }
//        if (model.getLastComment() != null && model.getLastComment().size() > 0) {
//            String sourceString = "<b>" + model.getLastComment().get(0).getName() + "</b> " + model.getLastComment().get(0).getText();
//            viewHolders.lastComment.setText(Html.fromHtml(sourceString));
//            viewHolders.lastComment.setVisibility(View.VISIBLE);
//
//        } else {
//
//            viewHolders.lastComment.setVisibility(View.GONE);
//
//        }
//        viewHolders.commentsCount.setText("View " + model.getCommentsCount() + " comments");
//
//
////        viewHolders.likesCount.setText(model.getLikesCount() + " likes");
////        viewHolders.commentsCount.setText(model.getCommentsCount() + " comments");
//        viewHolders.time.setText(CommonUtils.getFormattedDate(model.getTime()));
////
////        viewHolders.forward.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                callBacks.onSharePostWithFriends(model);
//////                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//////
//////                shareIntent.setType("text/plain");
//////                shareIntent.putExtra(Intent.EXTRA_TEXT, "http://umetechnology.com/" + model.getId());
//////                context.startActivity(Intent.createChooser(shareIntent, "Share post via.."));
////
////            }
////        });
////        viewHolders.likesCount.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                callBacks.takeUserToLikesScreen(model.getId());
////            }
////        });
//        viewHolders.postByPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                takeUserToProfile(model.getUserId());
//            }
//        });
////        viewHolders.postByName.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                takeUserToProfile(model.getPostBy());
////
////            }
////        });
////
//        viewHolders.menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Dialog dialog = new Dialog(context);
//                dialog.setContentView(R.layout.pop_up_menu);
//
//
//                TextView shareLink = dialog.findViewById(R.id.shareLink);
//                TextView copyLink = dialog.findViewById(R.id.copyLink);
//                TextView delete = dialog.findViewById(R.id.delete);
//
//
//                if (model.getUserId() == SharedPrefs.getUserModel().getId()) {
//                    delete.setVisibility(View.VISIBLE);
//                } else {
//                    delete.setVisibility(View.GONE);
//                }
//
//
//                delete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        callBacks.onDelete(model, position);
//                    }
//                });
//                copyLink.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        String url = AppConfig.BASE_URL + model.getRandom_id();
//                        CommonUtils.showToast("Copied to clipboard");
//                    }
//                });
//                shareLink.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
////                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
////
////                        shareIntent.setType("text/plain");
////                        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://umetechnology.com/" + model.getId());
////                        context.startActivity(Intent.createChooser(shareIntent, "Share post via.."));
//                        CommonUtils.shareUrl(context, "post", model.getRandom_id());
//                    }
//                });
//
////                download.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        dialog.dismiss();
////                        if (!model.getType().equalsIgnoreCase("multi")) {
////                            String filen = (model.getType().equalsIgnoreCase("image") ? model.getPictureUrl().substring(model.getPictureUrl().length() - 10, model.getPictureUrl().length()) + ".jpg" :
////                                    model.getVideoUrl().substring(model.getVideoUrl().length() - 10, model.getVideoUrl().length()) + ".mp4");
////                            File applictionFile = new File(Environment.getExternalStoragePublicDirectory(
////                                    Environment.DIRECTORY_DOWNLOADS) + "/" + filen
////                            );
////                            if (applictionFile != null && applictionFile.exists()) {
////                                Intent intent = new Intent();
////                                intent.setAction(Intent.ACTION_VIEW);
////                                intent.setDataAndType(Uri.fromFile(applictionFile), getMimeType(applictionFile.getAbsolutePath()));
////                                context.startActivity(intent);
////
////                            } else {
////                                DownloadFile.fromUrll((model.getType().equalsIgnoreCase("image")
////                                        ? model.getPictureUrl() : model.getVideoUrl()), filen);
////                                callBacks.onFileDownload(filen);
////
////                            }
////                        }
////                    }
////                });
//
//
//                dialog.show();
//            }
//        });
//
//        viewHolders.addComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(context, CommentsActivity.class);
//                i.putExtra("postId", model.getId());
//                context.startActivity(i);
//            }
//        });
//        viewHolders.lastComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(context, CommentsActivity.class);
//                i.putExtra("postId", model.getId());
//                context.startActivity(i);
//            }
//        });
//        viewHolders.comments.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(context, CommentsActivity.class);
//                i.putExtra("postId", model.getId());
//                context.startActivity(i);
//            }
//        });
////
////        if (model.getCountryCode() != null) {
////            viewHolders.flag.setVisibility(View.VISIBLE);
////            Glide.with(context).load(CountryUtils.getFlagDrawableResId(model.getCountryCode())).into(viewHolders.flag);
////        } else {
////            viewHolders.flag.setVisibility(View.GONE);
////        }
////
////        viewHolders.likeBtn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                likePost(finalLiked[0], viewHolders, model);
////            }
////        });
////
//        viewHolders.mainImage.setOnTouchListener(new View.OnTouchListener() {
//            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//                @Override
//                public boolean onDoubleTap(MotionEvent e) {
//                    viewHolders.showLike.setVisibility(View.VISIBLE);
//                    Animation myFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fadein);
//                    viewHolders.showLike.startAnimation(myFadeInAnimation);
////                    likePost(finalLiked[0], viewHolders, model);
//
////                    if (liked[0]) {
////                        model.setLiked(false);
////                        liked[0] = false;
////                        finalLiked[0] = false;
////                        viewHolders.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_empty));
////
////                    } else {
////                        model.setLiked(true);
////                        liked[0] = true;
////                        finalLiked[0] = true;
////                        viewHolders.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_fill));
////
////                    }
////                    viewHolders.likesCount.setText(model.getLikesCount() + " likes");
//
////                    viewHolders.showLike.setVisibility(View.GONE);
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        public void run() {
//                            // yourMethod();
//                            viewHolders.showLike.setVisibility(View.GONE);
//                        }
//                    }, 2500);
//                    return super.onDoubleTap(e);
//                }
//            });
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
//                gestureDetector.onTouchEvent(event);
//                return true;
//            }
//        });
////        viewHolders.video_view.setOnTouchListener(new View.OnTouchListener() {
////            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
////                @Override
////                public boolean onDoubleTap(MotionEvent e) {
////                    viewHolders.showLike.setVisibility(View.VISIBLE);
////                    Animation myFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fadein);
////                    viewHolders.showLike.startAnimation(myFadeInAnimation);
////                    likePost(finalLiked[0], viewHolders, model);
//////                    viewHolders.showLike.setVisibility(View.GONE);
////                    if (liked[0]) {
////                        model.setLiked(false);
////                        liked[0] = false;
////                        finalLiked[0] = false;
////                        viewHolders.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_empty));
////
////                    } else {
////                        model.setLiked(true);
////                        liked[0] = true;
////                        finalLiked[0] = true;
////                        viewHolders.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_fill));
////
////                    }
////                    viewHolders.likesCount.setText(model.getLikesCount() + " likes");
////                    Handler handler = new Handler();
////                    handler.postDelayed(new Runnable() {
////                        public void run() {
////                            // yourMethod();
////                            viewHolders.showLike.setVisibility(View.GONE);
////                        }
////                    }, 2500);
////                    return super.onDoubleTap(e);
////                }
////            });
////
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
////                gestureDetector.onTouchEvent(event);
////                return true;
////            }
////        });
////
////
////        viewHolders.repost.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                callBacks.onRePost(model);
////            }
////        });
////        viewHolders.download.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                callBacks.onShowDownloadMenu(model);
////            }
////        });
////
////
////        if (model.getGender() != null) {
////            viewHolders.genderBg.setVisibility(View.VISIBLE);
////            if (model.getGender().equalsIgnoreCase("female")) {
////                Glide.with(context).load(R.drawable.ic_female).into(viewHolders.gender);
////                viewHolders.genderBg.setBackground(context.getResources().getDrawable(R.drawable.custom_corners_pink));
////            } else {
////                Glide.with(context).load(R.drawable.ic_male).into(viewHolders.gender);
////                viewHolders.genderBg.setBackground(context.getResources().getDrawable(R.drawable.custom_corners_blue));
////
////            }
////
////            if (model.getUserAge() != 0) {
////                viewHolders.age.setText("" + model.getUserAge());
////            } else {
////
////            }
////        } else {
////            viewHolders.genderBg.setVisibility(View.GONE);
////        }
////
//    }
//
//    //
////
//    public void likePost(boolean value, VideoViewHolder holder, PostModel model) {
//        if (value) {
//            holder.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_empty));
////            model.setLikesCount(model.getLikesCount() - 1);
//            callBacks.onUnlikedPost(model);
//
//        } else {
//            holder.likeBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_fill));
////            model.setLikesCount(model.getLikesCount() + 1);
//            callBacks.onLikedPost(model);
//
//        }
//
//    }
//
//    private String getMimeType(String url) {
//        String parts[] = url.split("\\.");
//        String extension = parts[parts.length - 1];
//        String type = null;
//        if (extension != null) {
//            MimeTypeMap mime = MimeTypeMap.getSingleton();
//            type = mime.getMimeTypeFromExtension(extension);
//        }
//        return type;
//    }
//
//    public void takeUserToProfile(int userId) {
//        if (userId == SharedPrefs.getUserModel().getId()) {
//            callBacks.takeUserToMyUserProfile(userId);
//        } else {
//            callBacks.takeUserToOtherUserProfile(userId);
//        }
//    }
//
//
//    private class ClickHandler implements View.OnClickListener {
//
//        int position;
//        VideoViewHolder viewHolders;
//        PostModel model;
//
//        public ClickHandler(PostModel model,
//                            VideoViewHolder viewHolders,
//                            int position) {
//            this.position = position;
//            this.model = model;
//            this.viewHolders = viewHolders;
//        }
//
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.imageView_sound:
//                    ImageView imageView = (ImageView) v;
//                    checkAndSetMute(model, viewHolders, imageView);
//                    break;
//            }
//        }
//
//    }
//
//    private void checkAndSetMute(PostModel model,
//                                 VideoViewHolder viewHolders, ImageView imageView) {
//        if (model.isMute()) {
//            imageView.setImageResource(R.drawable.ic_unmute);
//            model.setMute(false);
//            viewHolders.helper.setVolumeInfo(new VolumeInfo(false, 1.0f));
//            SharedPrefs.setMuted("no");
//        } else {
//            imageView.setImageResource(R.drawable.ic_mute);
//            model.setMute(true);
//            viewHolders.helper.setVolumeInfo(new VolumeInfo(true, 0.0f));
//            SharedPrefs.setMuted("yes");
//        }
//    }


}
