package com.godavarisandroid.mystore.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.HomeActivity;
import com.godavarisandroid.mystore.adapters.MyCalenderViewPagerAdapter;
import com.godavarisandroid.mystore.views.SlidingTabLayout;

import java.util.ArrayList;

/**
 * Created by UMA on 4/21/2018.
 */

public class MyCalenderFragment extends BaseFragment implements View.OnClickListener {
    private View view;

    private SlidingTabLayout mTabLayout;
    private ViewPager mViewPager;
    private String selectedDate = "";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_calender, container, false);

        ((HomeActivity) getActivity()).mLlBottom.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgLogo.setVisibility(View.GONE);
        ((HomeActivity) getActivity()).mImgBack.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgHelp.setVisibility(View.VISIBLE);

        initComponents();
        return view;
    }

    private void initComponents() {
        getBundleData();
        setReferences();
        setClickListeners();
    }

    /*getting data through bundle*/
    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("selectedDate")) {
            selectedDate = bundle.getString("selectedDate");
        }
    }

    /*Initializing Views*/
    private void setReferences() {
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mTabLayout = (SlidingTabLayout) view.findViewById(R.id.tabLayout);
        setupViewPager(mViewPager);
    }

    /*Set adapter for viewpager and tablayout*/
    private void setupViewPager(ViewPager mViewPager) {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Calendar");
        arrayList.add("Active Subscription");
        arrayList.add("Inactive Subscription");

        MyCalenderViewPagerAdapter myCalenderViewPagerAdapter = new MyCalenderViewPagerAdapter(getContext(),
                getActivity().getSupportFragmentManager(),
                arrayList.size(), arrayList, selectedDate);
        mViewPager.setAdapter(myCalenderViewPagerAdapter);
        mTabLayout.setViewPager(mViewPager);
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
