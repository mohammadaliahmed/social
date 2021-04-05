package com.appsinventiv.social.Activities.HomeManagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.appsinventiv.social.Adapters.ExplorePostsAdapter;
import com.appsinventiv.social.Adapters.UserListAdapter;
import com.appsinventiv.social.Models.MessageModel;
import com.appsinventiv.social.Models.PostModel;
import com.appsinventiv.social.Models.UserModel;
import com.appsinventiv.social.NetworkResponses.AllRoomMessagesResponse;
import com.appsinventiv.social.NetworkResponses.ApiResponse;
import com.appsinventiv.social.NetworkResponses.SearchedUsersResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.KeyboardUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.google.android.gms.common.api.Api;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    View rootView;
    Context context;

    RecyclerView recyclerview;
    EditText searchWord;
    ImageView search;
    private List<UserModel> itemList = new ArrayList<>();
    UserListAdapter adapter;
    ExplorePostsAdapter explorePostsAdapter;
    RelativeLayout wholeLayout;
    RecyclerView recyclerviewExplore;
    private List<PostModel> postList = new ArrayList<>();


    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.search_fragment, container, false);

        search = rootView.findViewById(R.id.search);
        searchWord = rootView.findViewById(R.id.searchWord);
        recyclerview = rootView.findViewById(R.id.recyclerview);
        recyclerviewExplore = rootView.findViewById(R.id.recyclerviewExplore);
        wholeLayout = rootView.findViewById(R.id.wholeLayout);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchWord.getText().length() == 0) {
                    searchWord.setError("Cant be empty");
                } else if (searchWord.getText().length() < 3) {
                    searchWord.setError("Enter at least 3 characters");
                } else {
                    searchNow();
                }
            }
        });
        searchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    recyclerviewExplore.setVisibility(View.GONE);
                    recyclerview.setVisibility(View.VISIBLE);

                } else {
                    recyclerviewExplore.setVisibility(View.VISIBLE);
                    recyclerview.setVisibility(View.GONE);

                }
            }
        });

//        recyclerview.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerviewExplore.setLayoutManager(new GridLayoutManager(getContext(), 3));

        adapter = new UserListAdapter(getContext(), itemList);
        recyclerview.setAdapter(adapter);
        explorePostsAdapter = new ExplorePostsAdapter(getContext(), postList);
        recyclerviewExplore.setAdapter(explorePostsAdapter);

        getExploreDataFromServer();

        return rootView;
    }

    private void getExploreDataFromServer() {
        wholeLayout.setVisibility(View.GONE);
        recyclerviewExplore.setVisibility(View.VISIBLE);
        recyclerview.setVisibility(View.GONE);

        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("search", searchWord.getText().toString());
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        Call<ApiResponse> call = getResponse.explorePosts(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    recyclerviewExplore.setVisibility(View.VISIBLE);
                    recyclerview.setVisibility(View.GONE);


                    if (response.body().getPosts() != null) {
                        postList = response.body().getPosts();
                        explorePostsAdapter.setItemList(postList);
                    }

                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                wholeLayout.setVisibility(View.GONE);
                CommonUtils.showToast(t.getMessage());
            }
        });

    }

    private void searchNow() {
        recyclerviewExplore.setVisibility(View.GONE);
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        recyclerview.setVisibility(View.VISIBLE);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("search", searchWord.getText().toString());
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        Call<SearchedUsersResponse> call = getResponse.searchUsers(map);
        call.enqueue(new Callback<SearchedUsersResponse>() {
            @Override
            public void onResponse(Call<SearchedUsersResponse> call, Response<SearchedUsersResponse> response) {
                wholeLayout.setVisibility(View.GONE);
                if (response.code() == 200) {
                    KeyboardUtils.forceCloseKeyboard(wholeLayout);

                    itemList = response.body().getUsers();

                    if (itemList != null && itemList.size() > 0) {
                        recyclerview.setVisibility(View.VISIBLE);
                        recyclerviewExplore.setVisibility(View.GONE);


                        adapter.setItemList(itemList);

                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<SearchedUsersResponse> call, Throwable t) {
                wholeLayout.setVisibility(View.GONE);
                CommonUtils.showToast(t.getMessage());
            }
        });

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
