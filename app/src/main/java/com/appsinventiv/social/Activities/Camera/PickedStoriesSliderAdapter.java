package com.appsinventiv.social.Activities.Camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.appsinventiv.social.Models.StoriesPickedModel;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.List;

import androidx.viewpager.widget.PagerAdapter;

/**
 * Created by AliAh on 21/02/2018.
 */

public class PickedStoriesSliderAdapter extends PagerAdapter {
    Context context;
    List<StoriesPickedModel> picturesList;
    private MediaController mediaController;
    boolean playing;
    ImageView playPause;

    public PickedStoriesSliderAdapter(Context context, List<StoriesPickedModel> picturesList) {
        super();
        this.context = context;
        this.picturesList = picturesList;
    }


    public void setPicturesList(List<StoriesPickedModel> picturesList) {
        this.picturesList = picturesList;
        notifyDataSetChanged();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        final MediaPlayer[] mediaPlayer = new MediaPlayer[1];
        View view = LayoutInflater.from(context).inflate(R.layout.picked_stories_slider, container, false);
        ImageView imageView = view.findViewById(R.id.slider_image);
        final ImageView playPause = view.findViewById(R.id.playPause);
        final VideoView video = view.findViewById(R.id.video);
        if (picturesList.get(position).getType().equalsIgnoreCase("video")) {
            playPause.setVisibility(View.VISIBLE);

            video.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            try {
                video.setVideoURI(Uri.parse(CommonUtils.getRealPathFromURI(Uri.parse(picturesList.get(position).getUri()))));

            } catch (Exception e) {

            } finally {
                video.setVideoURI(Uri.parse(picturesList.get(position).getUri()));

            }

            mediaController = new MediaController(context);
            mediaController.setAnchorView(video);
//            video.setMediaController(mediaController);
            mediaController.hide();
            video.seekTo(1);


            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer[0] = mp;
                    playing = true;
                    video.start();
                }
            });


            playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playing) {
                        playPause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_black));
                        mediaPlayer[0].pause();
                        playing = false;

                    } else {
                        playPause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause_black));
                        mediaPlayer[0].start();
                        playing = true;
                    }
                }
            });
            video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playing = false;
                    playPause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_black));

                }
            });

        } else {
            playPause.setVisibility(View.GONE);
            if (mediaPlayer[0] != null) {
                mediaPlayer[0].stop();
                mediaPlayer[0].release();
            }
            video.stopPlayback();
            if (mediaController != null) {
                mediaController.hide();
                mediaController = null;
            }
            video.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            try {
                Glide.with(context).load(CommonUtils.getRealPathFromURI(Uri.parse(picturesList.get(position).getUri()))).into(imageView);
            } catch (Exception e) {

            } finally {
                Glide.with(context).load(picturesList.get(position).getUri()).into(imageView);

            }

        }

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {

        if (picturesList == null) {
            return 0;
        } else if (picturesList.size() > 0) {
            return picturesList.size();
        } else {
            return picturesList.size();
        }

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);

    }


}
