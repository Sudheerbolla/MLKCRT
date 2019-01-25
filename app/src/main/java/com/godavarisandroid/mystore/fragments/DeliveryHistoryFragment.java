package com.godavarisandroid.mystore.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.HomeActivity;
import com.godavarisandroid.mystore.adapters.DeliveryAddressTabAdapter;
import com.godavarisandroid.mystore.views.SlidingTabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by UMA on 4/21/2018.
 */

public class DeliveryHistoryFragment extends BaseFragment implements View.OnClickListener {
    private View view;

    private SlidingTabLayout mSlidingLayoutFragment;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delivery_history, container, false);

        ((HomeActivity) getActivity()).mImgLogo.setVisibility(View.GONE);
        ((HomeActivity) getActivity()).mImgBack.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgHelp.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mTxtTitle.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mTxtTitle.setText("Delivery History");

        initComponents();
        return view;
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
    }

    /*Initializing Views*/
    private void setReferences() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat monthYear = new SimpleDateFormat("MMMM-yyyy");
        String currentMonthYear = monthYear.format(calendar.getTime());

        calendar.add(Calendar.MONTH, -1);
        String previousMonthYear = monthYear.format(calendar.getTime());

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Current Month (" + currentMonthYear + ")");
        arrayList.add("Last Month (" + previousMonthYear + ")");

        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mSlidingLayoutFragment = (SlidingTabLayout) view.findViewById(R.id.tabLayout);

        /*Set adapter for tabs*/
        DeliveryAddressTabAdapter deliveryAddressTabAdapter = new DeliveryAddressTabAdapter(getContext(), getActivity().getSupportFragmentManager(),
                arrayList.size(), arrayList);
        mViewPager.setAdapter(deliveryAddressTabAdapter);
        mSlidingLayoutFragment.setViewPager(mViewPager);
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
