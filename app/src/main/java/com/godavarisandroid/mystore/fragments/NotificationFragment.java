package com.godavarisandroid.mystore.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.HomeActivity;
import com.godavarisandroid.mystore.adapters.CategoriesAdapter;

public class NotificationFragment extends BaseFragment implements View.OnClickListener {

    private View rootView;
    private RecyclerView mRecyclerView;
    private HomeActivity homeActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeActivity = (HomeActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        homeActivity.mLlBottom.setVisibility(View.VISIBLE);
        homeActivity.mImgLogo.setVisibility(View.GONE);
        homeActivity.mImgBack.setVisibility(View.VISIBLE);
        homeActivity.mImgHelp.setVisibility(View.GONE);
        homeActivity.mTxtTitle.setVisibility(View.VISIBLE);
        homeActivity.mTxtTitle.setText("Notifications");

        initComponents();
        return rootView;
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
        setAdapter();
    }

    /*Initializing Views*/
    private void setReferences() {
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void setClickListeners() {
    }

    private void setAdapter() {
//        NotificationAdapter mNotificationAdapter = new NotificationAdapter(getContext(), BaseApplication.mNotificationList, new NotificationFragment());
//        mRecyclerView.setAdapter(mNotificationAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addOnItemTouchListener(new CategoriesAdapter(getContext(), new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgAd:
                Bundle bundle = new Bundle();
                navigateFragment(new OfferDetailsFragment(), "OFFERDETAILSFRAGMENT", bundle, homeActivity);
                break;
            default:
                break;
        }
    }
}
