package com.godavarisandroid.mystore.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.godavarisandroid.mystore.fragments.ActiveSubscriptionFragment;
import com.godavarisandroid.mystore.fragments.CalenderFragmentNew;
import com.godavarisandroid.mystore.fragments.InActiveSubscriptionFragment;

import java.util.ArrayList;

/**
 * Created by Excentd11 on 4/26/2018.
 */

public class MyCalenderViewPagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;
    private String brandId, categoryId, selectedDate = "";
    private Context context;
    private boolean isfilter;
    private ArrayList<String> stringArrayList;

    public MyCalenderViewPagerAdapter(Context context, FragmentManager fm, int size,
                                      ArrayList<String> stringArrayList, String selectedDate) {
        super(fm);
        this.context = context;
        this.tabCount = size;
        this.stringArrayList = stringArrayList;
        this.isfilter = isfilter;
        this.selectedDate = selectedDate;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        bundle.putString("SELECTEDDATE", selectedDate);

        if (stringArrayList.get(position).equalsIgnoreCase("Calendar")) {
            fragment = new CalenderFragmentNew();
            fragment.setArguments(bundle);
        } else if (stringArrayList.get(position).equalsIgnoreCase("Active Subscription")) {
            fragment = new ActiveSubscriptionFragment();
        } else if (stringArrayList.get(position).equalsIgnoreCase("Inactive Subscription")) {
            fragment = new InActiveSubscriptionFragment();
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return stringArrayList.get(position);
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
