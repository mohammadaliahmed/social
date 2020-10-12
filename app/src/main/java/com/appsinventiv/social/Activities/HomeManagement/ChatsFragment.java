package com.appsinventiv.social.Activities.HomeManagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.appsinventiv.social.Adapters.ChatListAdapter;
import com.appsinventiv.social.Models.UserMessages;
import com.appsinventiv.social.NetworkResponses.UserMessagesResponse;
import com.appsinventiv.social.R;
import com.appsinventiv.social.Utils.AppConfig;
import com.appsinventiv.social.Utils.CommonUtils;
import com.appsinventiv.social.Utils.SharedPrefs;
import com.appsinventiv.social.Utils.UserClient;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsFragment extends Fragment {
    View rootView;
    Context context;

    RecyclerView recycler;
    ChatListAdapter adapter;
    private List<UserMessages> itemList = new ArrayList<>();
    EditText search;

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.chats_fragment, container, false);

        recycler = rootView.findViewById(R.id.recycler);
        search = rootView.findViewById(R.id.search);
        recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new ChatListAdapter(getContext()
                , itemList);
        recycler.setAdapter(adapter);


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(s.toString());
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        getDataFromServer();
        super.onResume();
    }

    private void getDataFromServer() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUserModel().getId());
        Call<UserMessagesResponse> call = getResponse.userMessages(map);
        call.enqueue(new Callback<UserMessagesResponse>() {
            @Override
            public void onResponse(Call<UserMessagesResponse> call, Response<UserMessagesResponse> response) {
                if (response.isSuccessful()) {

                    UserMessagesResponse object = response.body();
                    if (object != null) {
                        itemList.clear();
                        if (object.getMessages() != null && object.getMessages().size() > 0) {
                            itemList = object.getMessages();
                        }
                        adapter.updateList(itemList);
                    }

                }
            }

            @Override
            public void onFailure(Call<UserMessagesResponse> call, Throwable t) {
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
