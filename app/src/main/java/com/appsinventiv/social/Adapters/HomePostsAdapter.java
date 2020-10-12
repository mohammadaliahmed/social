package com.appsinventiv.social.Adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.social.Activities.HomeManagement.PostAdaptersCallbacks;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.bumptech.glide.Glide;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomePostsAdapter{
//        extends RecyclerView.Adapter<HomePostsAdapter.ViewHolder> {
//    Context context;
//    List<PostModel> dataArrayList;
//    boolean isMuteByDefault;
//    long initTimeStamp;
//    PostAdaptersCallbacks callBacks;
//
//    public HomePostsAdapter(Context context,
//                            List<PostModel> dataArrayList,
//                            boolean isMuteByDefault,
//                            long initTimeStamp, PostAdaptersCallbacks callBacks
//    ) {
//        this.context = context;
//        this.initTimeStamp = initTimeStamp;
//        this.isMuteByDefault = isMuteByDefault;
//        this.dataArrayList = dataArrayList;
//        setHasStableIds(true);
//        this.callBacks = callBacks;
//    }
//
//    public void setDataArrayList(List<PostModel> dataArrayList) {
//        this.dataArrayList = dataArrayList;
//        notifyDataSetChanged();
//    }
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = null;
//        view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.home_post_item_layout, parent, false);
//        HomePostsAdapter.ViewHolder
//                viewHolder = new HomePostsAdapter.ViewHolder(view);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder viewHolders, int position) {
//        PostModel model = dataArrayList.get(position);
//        viewHolders.postByName.setText(model.getUserModel().getName());
//        Glide.with(context).load(AppConfig.BASE_URL_Image + model.getUserModel().getThumbnailUrl()).into(viewHolders.postByPic);
//        if (model.getPostType().equalsIgnoreCase("Image")) {
//            viewHolders.video_view.setVisibility(View.GONE);
//            viewHolders.mainImage.setVisibility(View.VISIBLE);
////            viewHolders.imageView_sound.setVisibility(View.GONE);
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
////            viewHolders.imageView_sound.setVisibility(View.VISIBLE);
//
//
//        } else if (model.getPostType().equalsIgnoreCase("multi")) {
//            viewHolders.slider.setOffscreenPageLimit(0);
//            viewHolders.video_view.setVisibility(View.GONE);
//            viewHolders.mainImage.setVisibility(View.GONE);
//            viewHolders.picCount.setVisibility(View.VISIBLE);
//            viewHolders.muteIcon.setVisibility(View.GONE);
//            viewHolders.dots_indicator.setVisibility(View.VISIBLE);
////            viewHolders.imageView_sound.setVisibility(View.GONE);
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
//                    viewHolders.picCount.setText((position+1) + "/" + list.size());
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
//    }
//
////    @Override
////    public int getItemCount() {
////        return dataArrayList.size();
////    }
//    @Override
//    public int getItemCount() {
//        if (dataArrayList != null && dataArrayList.size() > 0) {
//            if (dataArrayList.size() < 30) {
//                return dataArrayList.size();
//            } else {
//                return 30;
//            }
//        } else {
//            return 0;
//        }
//    }
//
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView textView_description;
//        private static final String TAG = "IOSTUDIO:Video:Holder";
//
//        private Context context;
//        public ExoPlayerViewHelper helper;
//        Uri mediaUri;
//
//        TextView postByName, likesCount, time, addComment, lastComment, commentsCount, picCount, duration;
//        ImageView mainImage, showLike;
//        CircleImageView commenterImg, postByPic, flag;
//        ImageView muteIcon, comments, likeBtn, menu, forward, download, repost;
//        ViewPager slider;
//        WormDotsIndicator dots_indicator;
//        TextView age;
//        RelativeLayout genderBg;
//        ImageView gender;
//        ImageView progress_image;
//        ProgressBar videoProgress;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            duration = itemView.findViewById(R.id.duration);
//            videoProgress = itemView.findViewById(R.id.videoProgress);
//            slider = itemView.findViewById(R.id.slider);
//            dots_indicator = itemView.findViewById(R.id.dots_indicator);
//            picCount = itemView.findViewById(R.id.picCount);
////        progress_image = itemView.findViewById(R.id.progress_image);
//            video_view = itemView.findViewById(R.id.video_view);
////        imageView_sound = itemView.findViewById(R.id.imageView_sound);
//            postByName = itemView.findViewById(R.id.postByName);
//            likesCount = itemView.findViewById(R.id.likesCount);
//            time = itemView.findViewById(R.id.time);
//            addComment = itemView.findViewById(R.id.addComment);
//            lastComment = itemView.findViewById(R.id.lastComment);
//            commentsCount = itemView.findViewById(R.id.commentsCount);
//            mainImage = itemView.findViewById(R.id.mainImage);
////        commenterImg = itemView.findViewById(R.id.commenterImg);
//            postByPic = itemView.findViewById(R.id.postByPic);
//            muteIcon = itemView.findViewById(R.id.muteIcon);
//            likeBtn = itemView.findViewById(R.id.likeBtn);
//            comments = itemView.findViewById(R.id.comments);
//            menu = itemView.findViewById(R.id.menu);
//            forward = itemView.findViewById(R.id.forward);
//            showLike = itemView.findViewById(R.id.showLike);
//        }
//    }
}
