package com.godavarisandroid.mystore.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.models.PaymentHistory;

import java.util.ArrayList;

/**
 * Created by UMA on 5/5/2018.
 */
public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private PaymentHistoryAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<PaymentHistory> mPaymentHistory;

    public PaymentHistoryAdapter(Context context, ArrayList<PaymentHistory> mPaymentHistory) {
        mContext = context;
        this.mPaymentHistory = mPaymentHistory;
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

    public PaymentHistoryAdapter(Context context, PaymentHistoryAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public PaymentHistoryAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_history, parent, false);
        return new PaymentHistoryAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final PaymentHistoryAdapter.VideoHolder holder, final int position) {
        final PaymentHistory mPaymentHistory = this.mPaymentHistory.get(position);

        holder.mTxtReceiptNumber.setText(mPaymentHistory.recieptNumber);
        holder.mTxtTotalValue.setText("Total Value: " + mPaymentHistory.amount);
        holder.mTxtPaymentMode.setText("Mode: " + mPaymentHistory.paymentType);
        holder.mTxtDate.setText("Date: " + mPaymentHistory.date);
    }

    @Override
    public int getItemCount() {
        return mPaymentHistory.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        TextView mTxtReceiptNumber, mTxtTotalValue, mTxtPaymentMode, mTxtDate;

        public VideoHolder(View view) {
            super(view);

            mTxtReceiptNumber = (TextView) view.findViewById(R.id.txtReceiptNumber);
            mTxtTotalValue = (TextView) view.findViewById(R.id.txtTotalValue);
            mTxtPaymentMode = (TextView) view.findViewById(R.id.txtPaymentMode);
            mTxtDate = (TextView) view.findViewById(R.id.txtDate);
        }
    }
}