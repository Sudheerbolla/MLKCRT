package com.godavarisandroid.mystore.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.godavarisandroid.mystore.fragments.DeliveryHistoryInnerFragment;

import java.util.ArrayList;

/**
 * Created by Excentd11 on 4/26/2018.
 */

public class DeliveryAddressTabAdapter extends FragmentStatePagerAdapter {
    private int tabCount;
    private String brandId, categoryId;
    private Context context;
    private boolean isfilter;
    private ArrayList<String> stringArrayList;

    public DeliveryAddressTabAdapter(Context context, FragmentManager fm, int size,
                                     ArrayList<String> stringArrayList) {
        super(fm);
        this.context = context;
        this.tabCount = size;
        this.stringArrayList = stringArrayList;
        this.isfilter = isfilter;
    }

    @Override
    public Fragment getItem(int position) {
        for (int i = 0; i < stringArrayList.size(); i++) {
            if (stringArrayList.get(position).equals(stringArrayList.get(i))) {
//                typeName = grocerySinglePOjoArrayList.get(i).getType_name();
//                brandId = sandalBrandModelArrayList.get(i).getBrandId();
//                categoryId = sandalBrandModelArrayList.get(i).getCategoryId();
//                productsFragment = new ProductsFragment();
//                productsFragment.sendData(brandId, categoryId, sandalBrandModelArrayList.get(i).getBrandName(), isfilter);
            }
        }

        DeliveryHistoryInnerFragment deliveryHistoryInnerFragment = new DeliveryHistoryInnerFragment();
        if (position == 0) {
            deliveryHistoryInnerFragment.showData("");
        } else {
            deliveryHistoryInnerFragment.showData("L");
        }

        return deliveryHistoryInnerFragment;
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