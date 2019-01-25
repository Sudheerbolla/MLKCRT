package com.godavarisandroid.mystore.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.fragments.DeliveryHistoryInnerFragment;
import com.godavarisandroid.mystore.models.DeliveryHistory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by UMA on 4/22/2018.
 */
public class DeliveryHistoryAdapter extends RecyclerView.Adapter<DeliveryHistoryAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private DeliveryHistoryAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<DeliveryHistory> mDeliveryHistory;
    private DeliveryHistoryInnerFragment mDeliveryHistoryInnerFragment;

    public DeliveryHistoryAdapter(Context context, ArrayList<DeliveryHistory> mDeliveryHistory, DeliveryHistoryInnerFragment mDeliveryHistoryInnerFragment) {
        mContext = context;
        this.mDeliveryHistory = mDeliveryHistory;
        this.mDeliveryHistoryInnerFragment = mDeliveryHistoryInnerFragment;
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

    public DeliveryHistoryAdapter(Context context, DeliveryHistoryAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public DeliveryHistoryAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_history, parent, false);
        return new DeliveryHistoryAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeliveryHistoryAdapter.VideoHolder holder, final int position) {
        final DeliveryHistory mDeliveryHistory = this.mDeliveryHistory.get(position);

        holder.mTxtProduct.setText(mDeliveryHistory.pName);
        holder.mTxtQuantityName.setText(mDeliveryHistory.quantityName);
        holder.mTxtQuantityCount.setText(mDeliveryHistory.quantity);
        holder.mTxtPrice.setText(mDeliveryHistory.price);
        holder.mTxtTotalPrice.setText(mDeliveryHistory.totalPrice);
        holder.mTxtDate.setText(mDeliveryHistory.date);
        Picasso.with(mContext).load(mDeliveryHistory.image).into(holder.mImgProduct);
    }

    @Override
    public int getItemCount() {
        return mDeliveryHistory.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        ImageView mImgProduct;
        TextView mTxtProduct, mTxtQuantityName, mTxtQuantityCount, mTxtPrice, mTxtTotalPrice, mTxtDate;

        public VideoHolder(View view) {
            super(view);
            mImgProduct = (ImageView) view.findViewById(R.id.imgProduct);
            mTxtProduct = (TextView) view.findViewById(R.id.txtProduct);
            mTxtQuantityName = (TextView) view.findViewById(R.id.txtQuantity);
            mTxtQuantityCount = (TextView) view.findViewById(R.id.txtQuantityCount);
            mTxtPrice = (TextView) view.findViewById(R.id.txtPrice);
            mTxtTotalPrice = (TextView) view.findViewById(R.id.txtTotalPrice);
            mTxtDate = (TextView) view.findViewById(R.id.txtDate);
        }
    }
}