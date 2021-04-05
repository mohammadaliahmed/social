package com.appsinventiv.social.Adapters;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.appsinventiv.social.Activities.Chat.ChattingScreen;
import com.appsinventiv.social.Models.UserMessages;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.Constants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    Context context;

    List<UserMessages> itemList;
    List<UserMessages> arrayList;

    public ChatListAdapter(Context context, List<UserMessages> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.arrayList = new ArrayList<>(itemList);

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_layout, viewGroup, false);
        ChatListAdapter.ViewHolder viewHolder = new ChatListAdapter.ViewHolder(view);
        return viewHolder;
    }

    public void updateList(List<UserMessages> itemList) {
        this.itemList = itemList;
        arrayList.clear();
        arrayList.addAll(itemList);
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        itemList.clear();
        if (charText.length() == 0) {
            itemList.addAll(arrayList);
        } else {
            for (UserMessages item : arrayList) {
                if (item.getUserName().toLowerCase().contains(charText.toLowerCase())) {

                    itemList.add(item);
                }

            }


        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final UserMessages userModel = itemList.get(i);
        viewHolder.name.setText(userModel.getUserName());
        viewHolder.time.setText(CommonUtils.getFormattedDate(userModel.getTime()));
        if (userModel.getMessageType().equals(Constants.MESSAGE_TYPE_TEXT)) {
            viewHolder.message.setText(userModel.getMessageText());
        } else if (userModel.getMessageType().equals(Constants.MESSAGE_TYPE_IMAGE)) {
            viewHolder.message.setText("\uD83D\uDCF7  Image");
        } else if (userModel.getMessageType().equals(Constants.MESSAGE_TYPE_STORY)) {
            viewHolder.message.setText("Story reply: " + userModel.getMessageText());
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ChattingScreen.class);
                i.putExtra("roomId", userModel.getRoomId());
                i.putExtra("name", userModel.getUserName());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(i);
            }
        });
        Glide.with(context).load(AppConfig.BASE_URL_Image +userModel.getUserName()+"/"+ userModel.getThumbnailUrl()).placeholder(R.drawable.ic_profile_plc).into(viewHolder.picture);


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, message, time;
        CircleImageView picture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            picture = itemView.findViewById(R.id.picture);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }
    }


}
