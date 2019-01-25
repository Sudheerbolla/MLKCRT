package com.godavarisandroid.mystore.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.adapters.NotificationAdapter;

public class NotificationsActivity extends BaseActivity {

    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notification);
        initComponents();
    }

    private void initComponents() {
        setReferences();
        setAdapter();
    }

    private void setReferences() {
        mRecyclerView = findViewById(R.id.recyclerView);
        ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setAdapter() {
        NotificationAdapter mNotificationAdapter = new NotificationAdapter(this, BaseApplication.mNotificationList);
        mRecyclerView.setAdapter(mNotificationAdapter);
    }

}
