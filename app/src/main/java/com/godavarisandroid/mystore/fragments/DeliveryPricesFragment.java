package com.godavarisandroid.mystore.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.HomeActivity;
import com.godavarisandroid.mystore.adapters.CategoriesAdapter;
import com.godavarisandroid.mystore.adapters.DeliveryPricesAdapter;
import com.godavarisandroid.mystore.models.DeliveryPrices;

import java.util.ArrayList;

/**
 * Created by UMA on 4/21/2018.
 */

public class DeliveryPricesFragment extends BaseFragment {
    private View view;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delivery_prices, container, false);

        ((HomeActivity) getActivity()).mLlBottom.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgLogo.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgBack.setVisibility(View.GONE);
        ((HomeActivity) getActivity()).mImgHelp.setVisibility(View.VISIBLE);

        initComponents();
        return view;
    }

    private void initComponents() {
        setReferences();
        setClickListeners();

        setAdapter();
    }

    /*Initializing Views*/
    private void setReferences() {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    }

    private void setClickListeners() {

    }

    private void setAdapter() {
        ArrayList<DeliveryPrices> mDeliveryPrices = new ArrayList<>();

        mDeliveryPrices.add(new DeliveryPrices("", "Milk"));
        mDeliveryPrices.add(new DeliveryPrices("", "Juices"));
        mDeliveryPrices.add(new DeliveryPrices("", "Water"));
        mDeliveryPrices.add(new DeliveryPrices("", "Coffee/Tea"));

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        DeliveryPricesAdapter mDeliveryPricesAdapter = new DeliveryPricesAdapter(getContext(), mDeliveryPrices, new DeliveryPricesFragment());
        mRecyclerView.setAdapter(mDeliveryPricesAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new CategoriesAdapter(getContext(), new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }
}
