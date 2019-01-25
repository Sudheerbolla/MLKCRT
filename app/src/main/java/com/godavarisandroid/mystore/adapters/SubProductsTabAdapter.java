package com.godavarisandroid.mystore.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.godavarisandroid.mystore.fragments.SubProductsInnerFragment;
import com.godavarisandroid.mystore.models.Brands;

import java.util.ArrayList;

/**
 * Created by Excentd11 on 4/27/2018.
 */

public class SubProductsTabAdapter extends FragmentStatePagerAdapter {
    private int tabCount;
    private String brandId, categoryId, from;
    private Context context;
    private boolean isfilter;
    private ArrayList<Brands> brandsList;

    public SubProductsTabAdapter(Context context, FragmentManager fm, int size, ArrayList<Brands> brandsList,
                                 String cId, String from) {
        super(fm);
        this.context = context;
        this.tabCount = size;
        this.categoryId = cId;
        this.brandsList = brandsList;
        this.from = from;
    }

    @Override
    public Fragment getItem(int position) {
        for (int i = 0; i < brandsList.size(); i++) {
            if (brandsList.get(position).equals(brandsList.get(i))) {
//                typeName = grocerySinglePOjoArrayList.get(i).getType_name();
//                brandId = sandalBrandModelArrayList.get(i).getBrandId();
//                categoryId = sandalBrandModelArrayList.get(i).getCategoryId();
//                productsFragment = new ProductsFragment();
//                productsFragment.sendData(brandId, categoryId, sandalBrandModelArrayList.get(i).getBrandName(), isfilter);
            }
        }

//        Bundle bundle = new Bundle();
//        bundle.putString("brand_id", brandsList.get(position).id);
        SubProductsInnerFragment subProductsInnerFragment = new SubProductsInnerFragment();
//        subProductsInnerFragment.setArguments(bundle);

        subProductsInnerFragment.showData(brandsList.get(position).id, brandsList.get(position).cId,
                brandsList.get(position).cName, from);

        return subProductsInnerFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return brandsList.get(position).name;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
