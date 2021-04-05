package com.appsinventiv.social.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appsinventiv.social.Interfaces.PictureClickCallbacks;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyProfilePostsAdapter extends RecyclerView.Adapter<MyProfilePostsAdapter.ViewHolder> {
    Context context;
    List<PostModel> itemList = new ArrayList<>();
    PictureClickCallbacks callbacks;

    public MyProfilePostsAdapter(Context context, List<PostModel> itemList, PictureClickCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;
    }

    public void setItemList(List<PostModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_pic_item_layout, parent, false);
        MyProfilePostsAdapter.ViewHolder viewHolder = new MyProfilePostsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final PostModel model = itemList.get(position);
        if (model.getPostType().equalsIgnoreCase("image")) {
            holder.videoIcon.setVisibility(View.GONE);
            holder.multiIcon.setVisibility(View.GONE);
            Glide.with(context).load(AppConfig.BASE_URL_Image +model.getUserModel().getUsername()+"/"+ model.getImagesUrl()).into(holder.image);


        } else if (model.getPostType().equalsIgnoreCase("video")) {
            holder.videoIcon.setVisibility(View.VISIBLE);
            holder.multiIcon.setVisibility(View.GONE);


            Glide.with(context).load(AppConfig.BASE_URL_Image + model.getUserModel().getUsername() + "/" + model.getVideo_image_url()).into(holder.image);
        } else if (model.getPostType().equalsIgnoreCase("multi")) {

            holder.videoIcon.setVisibility(View.GONE);
            holder.multiIcon.setVisibility(View.VISIBLE);
            final List<String> list = new ArrayList<String>(Arrays.asList(model.getImagesUrl().split(",")));
            Glide.with(context).load(AppConfig.BASE_URL_Image +model.getUserModel().getUsername()+"/"+ list.get(0)).into(holder.image);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callbacks != null) {
                    callbacks.onPictureClicked(position);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, multiIcon, videoIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            videoIcon = itemView.findViewById(R.id.videoIcon);
            multiIcon = itemView.findViewById(R.id.multiIcon);
        }
    }

}
