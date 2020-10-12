package com.appsinventiv.social.Activities.Chat;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.appsinventiv.social.Activities.ViewPictures;
import com.appsinventiv.social.Models.MessageModel;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.Constants;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    Context context;

    List<MessageModel> itemList;

    public int RIGHT_CHAT = 1;
    public int LEFT_CHAT = 0;

    public MessagesAdapter(Context context, List<MessageModel> itemList) {
        this.context = context;
        this.itemList = itemList;

    }

    public void setItemList(List<MessageModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    @Override
    public int getItemViewType(int position) {
        MessageModel model = itemList.get(position);
        if (model.getMessageById() != null) {
            if (model.getMessageById() == SharedPrefs.getUserModel().getId()) {
                return RIGHT_CHAT;
            } else {
                return LEFT_CHAT;
            }
        }
        return -1;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        if (viewType == RIGHT_CHAT) {
            View view = LayoutInflater.from(context).inflate(R.layout.right_chat_layout, parent, false);
            viewHolder = new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.left_chat_layout, parent, false);
            viewHolder = new ViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final MessageModel model = itemList.get(i);
        if (model.getMessageType().equals(Constants.MESSAGE_TYPE_IMAGE)) {
            viewHolder.image.setVisibility(View.VISIBLE);
            viewHolder.messageText.setVisibility(View.GONE);
            Glide.with(context).load(AppConfig.BASE_URL_Image + model.getImageUrl()).into(viewHolder.image);
        } else if (model.getMessageType().equals(Constants.MESSAGE_TYPE_TEXT)) {
            viewHolder.image.setVisibility(View.GONE);
            viewHolder.messageText.setVisibility(View.VISIBLE);
            viewHolder.messageText.setText(model.getMessageText());
        }

        viewHolder.time.setText(CommonUtils.getFormattedDate(model.getTime()));

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ViewPictures.class);
                i.putExtra("url", AppConfig.BASE_URL_Image + model.getImageUrl());
                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, time;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            image = itemView.findViewById(R.id.image);
            time = itemView.findViewById(R.id.time);
        }
    }


}
