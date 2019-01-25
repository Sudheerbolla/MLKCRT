package com.godavarisandroid.mystore.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.models.AlertAddProducts;
import com.godavarisandroid.mystore.utils.PopUtils;

import java.util.ArrayList;

/**
 * Created by UMA on 5/5/2018.
 */
public class AlertAddProductsAdapter extends RecyclerView.Adapter<AlertAddProductsAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private AlertAddProductsAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<AlertAddProducts> mAlertAddProducts;
    private Dialog firstDialog, secondDialog;

    public AlertAddProductsAdapter(Context context, ArrayList<AlertAddProducts> mAlertAddProducts, Dialog firstDialog, Dialog secondDialog) {
        mContext = context;
        this.mAlertAddProducts = mAlertAddProducts;
        this.firstDialog = firstDialog;
        this.secondDialog = secondDialog;
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

    public AlertAddProductsAdapter(Context context, AlertAddProductsAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public AlertAddProductsAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert_add_products, parent, false);
        return new AlertAddProductsAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlertAddProductsAdapter.VideoHolder holder, final int position) {
        final AlertAddProducts mAlertAddProducts = this.mAlertAddProducts.get(position);

        holder.mTxtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUtils.alertAddProductsDetails(mContext, firstDialog, secondDialog);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mAlertAddProducts.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        TextView mTxtAdd;

        public VideoHolder(View view) {
            super(view);
            mTxtAdd = (TextView) view.findViewById(R.id.txtAdd);
        }
    }
}