package com.godavarisandroid.mystore.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.HomeActivity;
import com.godavarisandroid.mystore.adapters.SubscriptionsPagerAdapter;

import java.util.ArrayList;

public class SubscriptionsFragment extends BaseFragment implements View.OnClickListener {
    private View view;

//    private SlidingTabLayout mTabLayout;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String selectedDate = "";
    private HomeActivity homeActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeActivity = ((HomeActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subscriptions, container, false);
        homeActivity.mLlBottom.setVisibility(View.VISIBLE);
        homeActivity.mImgLogo.setVisibility(View.GONE);
        homeActivity.mImgBack.setVisibility(View.VISIBLE);
        homeActivity.mImgHelp.setVisibility(View.VISIBLE);
        homeActivity.mTxtTitle.setVisibility(View.VISIBLE);
        homeActivity.mTxtTitle.setText("My Subscriptions");

        initComponents();
        return view;
    }

    private void initComponents() {
        getBundleData();
        setReferences();
        setClickListeners();
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("selectedDate")) {
            selectedDate = bundle.getString("selectedDate");
        }
    }

    private void setReferences() {
        mViewPager = view.findViewById(R.id.viewPager);
        mTabLayout = view.findViewById(R.id.tabLayout);
        setupViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager mViewPager) {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Active Subscription");
        arrayList.add("Inactive Subscription");
        SubscriptionsPagerAdapter subscriptionsPagerAdapter = new SubscriptionsPagerAdapter(getContext(), homeActivity.getSupportFragmentManager(), arrayList.size(), arrayList, selectedDate);
        mViewPager.setAdapter(subscriptionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager, true);
//        mTabLayout.setViewPager(mViewPager);
    }

    private void setClickListeners() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }
    }
}
