package com.appsinventiv.social.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.social.Activities.OtherUser.ViewProfile;
import com.appsinventiv.social.Activities.ViewPost;
import com.appsinventiv.social.Models.NotificationModel;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsListAdapter extends RecyclerView.Adapter<NotificationsListAdapter.ViewHolder> {
    Context context;
    List<NotificationModel> itemList = new ArrayList<>();

    public NotificationsListAdapter(Context context, List<NotificationModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(List<NotificationModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item_layout, parent, false);
        NotificationsListAdapter.ViewHolder viewHolder = new NotificationsListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final NotificationModel model = itemList.get(position);

        if(model.getPicture().contains(",")){
            String[] img = model.getPicture().split(",");
            Glide.with(context).load(AppConfig.BASE_URL_Image + img[0]).placeholder(R.drawable.ic_profile_plc).into(holder.image);

        }else {
            Glide.with(context).load(AppConfig.BASE_URL_Image + model.getPicture()).placeholder(R.drawable.ic_profile_plc).into(holder.image);
        }
        holder.name.setText(model.getTitle());
        holder.date.setText(CommonUtils.getFormattedDate(model.getTime()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getType().equalsIgnoreCase("request")) {
                    Intent i = new Intent(context, ViewProfile.class);
                    i.putExtra("userId", model.getHisId());
                    context.startActivity(i);
                }
                else if (model.getType().equalsIgnoreCase("post")) {
                    Intent i = new Intent(context, ViewPost.class);
                    i.putExtra("postId", model.getPostId());
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
        CircleImageView image;
        TextView name,date;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
        }
    }


}
