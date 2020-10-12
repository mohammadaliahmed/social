package com.appsinventiv.social.Activities.OtherUser;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.social.Interfaces.PictureClickCallbacks;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfilePostsAdapter extends RecyclerView.Adapter<UserProfilePostsAdapter.ViewHolder> {
    Context context;
    List<PostModel> itemList = new ArrayList<>();
    PictureClickCallbacks callBacks;

    public UserProfilePostsAdapter(Context context, List<PostModel> itemList, PictureClickCallbacks callBacks) {
        this.context = context;
        this.itemList = itemList;
        this.callBacks = callBacks;
    }

    public void setItemList(List<PostModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_pic_item_layout, parent, false);
        UserProfilePostsAdapter.ViewHolder viewHolder = new UserProfilePostsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final PostModel model = itemList.get(position);
        if (model.getPostType().equalsIgnoreCase("image")) {
            holder.videoIcon.setVisibility(View.GONE);
            holder.multiIcon.setVisibility(View.GONE);
            Glide.with(context).load(AppConfig.BASE_URL_Image + model.getImagesUrl()).into(holder.image);


        } else if (model.getPostType().equalsIgnoreCase("video")) {
            holder.videoIcon.setVisibility(View.VISIBLE);
            holder.multiIcon.setVisibility(View.GONE);


            Glide.with(context).load(AppConfig.BASE_URL_Image+model.getVideo_image_url()).into(holder.image);
        } else if (model.getPostType().equalsIgnoreCase("multi")) {

            holder.videoIcon.setVisibility(View.GONE);
            holder.multiIcon.setVisibility(View.VISIBLE);
            final List<String> list = new ArrayList<String>(Arrays.asList(model.getImagesUrl().split(",")));
            Glide.with(context).load(AppConfig.BASE_URL_Image + list.get(0)).into(holder.image);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBacks != null) {
                    callBacks.onPictureClicked(position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!model.getPostType().equalsIgnoreCase("video")) {
                    showDialog(model);
                }
                return false;
            }
        });
    }

    private void showDialog(PostModel model) {
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_pic_popup, null);

        dialog.setContentView(layout);

        TextView name = layout.findViewById(R.id.name);
        CircleImageView userImage = layout.findViewById(R.id.userImage);
        name.setText(model.getUserModel().getName());
        Glide.with(context).load(AppConfig.BASE_URL_Image + model.getUserModel().getThumbnailUrl()).into(userImage);

        ImageView image = layout.findViewById(R.id.image);

        if (model.getPostType().equalsIgnoreCase("multi")) {
            final List<String> list = new ArrayList<String>(Arrays.asList(model.getImagesUrl().split(",")));
            Glide.with(context).load(AppConfig.BASE_URL_Image + list.get(0)).into(image);

        } else if (model.getPostType().equalsIgnoreCase("image")) {
            Glide.with(context).load(AppConfig.BASE_URL_Image + model.getImagesUrl()).into(image);

        }

        dialog.show();

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
