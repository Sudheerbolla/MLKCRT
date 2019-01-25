package com.godavarisandroid.mystore.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.fragments.OfferDetailsFragment;
import com.godavarisandroid.mystore.models.MyStore;

import java.util.ArrayList;

/**
 * Created by UMA on 4/22/2018.
 */
public class MyStoreAdapter extends RecyclerView.Adapter<MyStoreAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private MyStoreAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<MyStore> mMyStore;
    private OfferDetailsFragment mOfferDetailsFragment;

    public MyStoreAdapter(Context context, ArrayList<MyStore> mMyStore, OfferDetailsFragment mOfferDetailsFragment) {
        mContext = context;
        this.mMyStore = mMyStore;
        this.mOfferDetailsFragment = mOfferDetailsFragment;
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

    public MyStoreAdapter(Context context, MyStoreAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public MyStoreAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_store, parent, false);
        return new MyStoreAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyStoreAdapter.VideoHolder holder, final int position) {
        final MyStore mMyStore = this.mMyStore.get(position);

    }

    @Override
    public int getItemCount() {
        return mMyStore.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        public VideoHolder(View view) {
            super(view);

        }
    }
}