package com.appsinventiv.social.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.social.Activities.OtherUser.ViewProfile;
import com.appsinventiv.social.Activities.ViewPost;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExplorePostsAdapter extends RecyclerView.Adapter<ExplorePostsAdapter.ViewHolder> {
    Context context;
    List<PostModel> itemList = new ArrayList<>();

    public ExplorePostsAdapter(Context context, List<PostModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(List<PostModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.explore_item_layout_square, parent, false);
        ExplorePostsAdapter.ViewHolder viewHolder = new ExplorePostsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final PostModel model = itemList.get(position);
        if (model.getPostType().equalsIgnoreCase("image")) {
            Glide.with(context).load(AppConfig.BASE_URL_Image + model.getUsername() + "/" + model.getImagesUrl()).into(holder.image);
        } else {
            String[] items = model.getImagesUrl().split(",");
            Glide.with(context).load(AppConfig.BASE_URL_Image + model.getUsername() + "/" + items[0]).into(holder.image);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getId() != SharedPrefs.getUserModel().getId()) {
                    Intent i = new Intent(context, ViewPost.class);
                    i.putExtra("postId", model.getId());
                    context.startActivity(i);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }


}
