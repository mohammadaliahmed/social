package com.appsinventiv.social.Activities.HomeManagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.social.Adapters.NotificationsListAdapter;
import com.appsinventiv.social.Models.NotificationModel;
import com.appsinventiv.social.NetworkResponses.ApiResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {
    private View rootView;
    RecyclerView recycler;
    NotificationsListAdapter adapter;
    private List<NotificationModel> itemList = new ArrayList<>();

    TextView noNoti;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.list_of_notifications, container, false);

        noNoti = rootView.findViewById(R.id.noNoti);
        recycler = rootView.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new NotificationsListAdapter(getContext(), itemList);
        recycler.setAdapter(adapter);
        getDataFromServer();

        return rootView;

    }

    private void getDataFromServer() {
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        Call<ApiResponse> call = getResponse.getMyNotifications(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    itemList = response.body().getNotificationList();
                    if (itemList != null && itemList.size() > 0) {
                        Collections.reverse(itemList);
                        adapter.setItemList(itemList);
                        noNoti.setVisibility(View.GONE);
                    } else {
                        noNoti.setVisibility(View.VISIBLE);
                    }

                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                noNoti.setVisibility(View.VISIBLE);

            }
        });

    }
}
