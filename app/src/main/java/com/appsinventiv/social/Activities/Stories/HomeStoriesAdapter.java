package com.appsinventiv.social.Activities.Stories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsinventiv.social.Models.StoryModel;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeStoriesAdapter extends RecyclerView.Adapter<HomeStoriesAdapter.ViewHolder> {
    Context context;
    public ArrayList<ArrayList<StoryModel>> itemList = new ArrayList<>();
    HomeStoriesAdapterCallbacks callbacks;
    HashMap<String, Boolean> hashMap = new HashMap<>();


    public HomeStoriesAdapter(Context context, ArrayList<ArrayList<StoryModel>> itemList, HomeStoriesAdapterCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;

    }

    public void setHashMap(HashMap<String, Boolean> hashMap) {
        this.hashMap = hashMap;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeStoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_story_item_layout, parent, false);
        HomeStoriesAdapter.ViewHolder viewHolder = new HomeStoriesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeStoriesAdapter.ViewHolder holder, final int position) {
        final ArrayList<StoryModel> model = itemList.get(position);

//        if (hashMap != null && hashMap.size() > 0) {
//            if (hashMap.get(model.get(0).getStoryByUsername())) {
//                holder.seenLayout.setVisibility(View.VISIBLE);
//            } else {
//                holder.seenLayout.setVisibility(View.GONE);
//
//            }
//        }

        holder.storyName.setText(model.get(0).getUser().getUsername());
        Glide.with(context).load(AppConfig.BASE_URL_Image + model.get(0).getUser().getUsername() + "/" + model.get(0).getUser().getThumbnailUrl()).into(holder.storyImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    callbacks.onStoryClicked(model.get(0), position);

                } catch (Exception e) {
                    CommonUtils.showToast("Something wrong");
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView storyName;
        CircleImageView storyImg;
        View seenLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storyName = itemView.findViewById(R.id.storyName);
            storyImg = itemView.findViewById(R.id.storyImg);
            seenLayout = itemView.findViewById(R.id.seenLayout);
        }
    }

    public interface HomeStoriesAdapterCallbacks {


        public void onStoryClicked(StoryModel model, int position);
    }
}
