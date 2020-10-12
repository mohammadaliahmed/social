package com.appsinventiv.social.Activities.Comments;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsinventiv.social.Activities.OtherUser.ViewProfile;
import com.appsinventiv.social.Models.CommentsModel;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    Context context;
    List<CommentsModel> itemList = new ArrayList<>();

    public CommentsAdapter(Context context, List<CommentsModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(List<CommentsModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comments_item_layout, parent, false);
        CommentsAdapter.ViewHolder viewHolder = new CommentsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final CommentsModel model = itemList.get(position);


        Glide.with(context).load(AppConfig.BASE_URL_Image + model.getUser().getThumbnailUrl()).into(holder.image);
        holder.name.setText(model.getUser().getName());


        String sourceString = "<b>" + model.getUser().getName() + "</b> " + model.getText();
        holder.comment.setText(Html.fromHtml(sourceString));
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(model.getUser().getId()!= SharedPrefs.getUserModel().getId()) {

                    Intent i = new Intent(context, ViewProfile.class);
                    i.putExtra("userId", model.getUserId());
                    context.startActivity(i);
                }
            }
        });
        holder.date.setText(CommonUtils.getFormattedDate(model.getTime()));


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name, date, comment;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            comment = itemView.findViewById(R.id.comment);
            date = itemView.findViewById(R.id.date);
        }
    }


}
