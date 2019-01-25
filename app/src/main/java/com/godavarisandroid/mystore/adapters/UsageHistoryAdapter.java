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
import com.godavarisandroid.mystore.models.UsageHistory;

import java.util.ArrayList;

/**
 * Created by UMA on 5/5/2018.
 */
public class UsageHistoryAdapter extends RecyclerView.Adapter<UsageHistoryAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private UsageHistoryAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<UsageHistory> mUsageHistory;

    public UsageHistoryAdapter(Context context, ArrayList<UsageHistory> mUsageHistory) {
        mContext = context;
        this.mUsageHistory = mUsageHistory;
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

    public UsageHistoryAdapter(Context context, UsageHistoryAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public UsageHistoryAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usage_history, parent, false);
        return new UsageHistoryAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final UsageHistoryAdapter.VideoHolder holder, final int position) {
        final UsageHistory mUsageHistory = this.mUsageHistory.get(position);

        holder.mTxtMonthYear.setText(mUsageHistory.monthName + " ' " + mUsageHistory.year);
        holder.mTxtTotalValue.setText("Total Value : " + mUsageHistory.amount);
    }

    @Override
    public int getItemCount() {
        return mUsageHistory.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        TextView mTxtMonthYear, mTxtTotalValue;

        public VideoHolder(View view) {
            super(view);
            mTxtMonthYear = (TextView) view.findViewById(R.id.txtMonthYear);
            mTxtTotalValue = (TextView) view.findViewById(R.id.txtTotalValue);
        }
    }
}