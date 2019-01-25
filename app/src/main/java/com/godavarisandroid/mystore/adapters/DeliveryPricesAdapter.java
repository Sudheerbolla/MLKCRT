package com.godavarisandroid.mystore.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.fragments.DeliveryPricesFragment;
import com.godavarisandroid.mystore.models.DeliveryPrices;

import java.util.ArrayList;

/**
 * Created by Excentd11 on 4/27/2018.
 */

public class DeliveryPricesAdapter extends RecyclerView.Adapter<DeliveryPricesAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private DeliveryPricesAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<DeliveryPrices> mDeliveryPrices;
    private DeliveryPricesFragment mDeliveryPricesFragment;

    public DeliveryPricesAdapter(Context context, ArrayList<DeliveryPrices> mDeliveryPrices, DeliveryPricesFragment mDeliveryPricesFragment) {
        mContext = context;
        this.mDeliveryPrices = mDeliveryPrices;
        this.mDeliveryPricesFragment = mDeliveryPricesFragment;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public DeliveryPricesAdapter(Context context, DeliveryPricesAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public DeliveryPricesAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_prices, parent, false);
        return new DeliveryPricesAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeliveryPricesAdapter.VideoHolder holder, final int position) {
        final DeliveryPrices mDeliveryPrices = this.mDeliveryPrices.get(position);

    }

    @Override
    public int getItemCount() {
        return mDeliveryPrices.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        public VideoHolder(View view) {
            super(view);

        }
    }
}