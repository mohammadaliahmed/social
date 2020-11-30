package com.appsinventiv.social.Activities.Chat;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.appsinventiv.social.Models.UserMessages;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.CommonUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    Context context;

    List<UserMessages> itemList;

    public ChatListAdapter(Context context, List<UserMessages> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(List<UserMessages> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final UserMessages userModel = itemList.get(i);
        viewHolder.name.setText(userModel.getUserName());
        viewHolder.time.setText(CommonUtils.getFormattedDate(userModel.getTime()));
        viewHolder.message.setText(userModel.getMessageText());
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




    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, message, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }
    }


}
