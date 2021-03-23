package com.appsinventiv.social.Activities.Stories;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.appsinventiv.social.Activities.MainActivity;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.Constants;
import com.appsinventiv.social.Utils.CustomViewPager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class StoryActivity extends AppCompatActivity {

    CustomViewPager viewpager;
    StoriesSliderAdapter mViewPagerAdapter;
    public static Activity activity;
    int pos = 0;
    private InterstitialAd mInterstitialAd;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }


        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, getResources().getString(R.string.interstitial_ad_unit_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
//                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
//                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });


        LocalBroadcastManager.getInstance(StoryActivity.this).registerReceiver(mMessageReceiver,
                new IntentFilter("storyPosition"));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.stories_fragment);

        viewpager = findViewById(R.id.viewpager);
        ArrayList<String> pics = new ArrayList<>();

        mViewPagerAdapter = new StoriesSliderAdapter(getSupportFragmentManager(), this);
        viewpager.setAdapter(mViewPagerAdapter);
//        viewpager.setOffscreenPageLimit(3);
        viewpager.setCurrentItem(Constants.STORY_POSITION);
        viewpager.setOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
//                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        mViewPagerAdapter.notifyDataSetChanged();


    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            position = intent.getIntExtra("storyPosition", 0);
//            CommonUtils.showToast("" + position);
            if (position < MainActivity.arrayLists.size()) {
//                viewpager.setCurrentItem(pos + 1);
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(StoryActivity.this);
                    callBacks();
                } else {
                    viewpager.setCurrentItem(pos + 1);
                }
            } else {

                finish();
            }


        }
    };

    private void callBacks() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                Log.d("TAG", "The ad was dismissed.");
                viewpager.setCurrentItem(pos + 1);


            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                Log.d("TAG", "The ad failed to show.");
                viewpager.setCurrentItem(pos + 1);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                mInterstitialAd = null;
                Log.d("TAG", "The ad was shown.");
            }
        });
    }


}
