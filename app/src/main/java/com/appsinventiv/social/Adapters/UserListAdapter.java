package com.appsinventiv.social.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsinventiv.social.Activities.OtherUser.ViewProfile;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    Context context;
    List<UserModel> itemList = new ArrayList<>();

    public UserListAdapter(Context context, List<UserModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(List<UserModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item_layout_square, parent, false);
        UserListAdapter.ViewHolder viewHolder = new UserListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final UserModel model = itemList.get(position);
        Glide.with(context).load(AppConfig.BASE_URL_Image +model.getUsername()+"/"+ model.getThumbnailUrl()).placeholder(R.drawable.ic_profile_plc).into(holder.image);
        holder.name.setText(model.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(model.getId()!= SharedPrefs.getUserModel().getId()) {
                    Intent i = new Intent(context, ViewProfile.class);
                    i.putExtra("userId", model.getId());
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
        TextView name;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
        }
    }


}
