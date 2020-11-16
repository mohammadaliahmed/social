package com.appsinventiv.social.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppConfig {
        public static String LPTOP_ID = "http://192.168.100.31/socialserver/";
//        public static String LPTOP_ID = "http://192.168.8.100/social/";
    public static String SERVER_URL = "https://saffronclub.com.au/";
    public static String BASE_URL = LPTOP_ID;
    public static String API_USERNAME = "WF9.FJ8u'FP{c5Pw";
    public static String API_PASSOWRD = "3B~fauh5s93j[FKb";

    public static String BASE_URL_Image = BASE_URL + "public/images/";
    public static String BASE_URL_Videos = BASE_URL + "public/videos/";
    public static String TOKKEN = "http://acnure.com/";

    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit getTokenUrl() {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.TOKKEN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
