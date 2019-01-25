package com.godavarisandroid.mystore.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.models.Wallet;
import com.godavarisandroid.mystore.views.OpensansTextView;

import java.util.ArrayList;

/**
 * Created by Excentd11 on 4/27/2018.
 */

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private WalletAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<Wallet> mWallet;
    private int selectedPosition = -1;

    public WalletAdapter(Context context, ArrayList<Wallet> mWallet) {
        mContext = context;
        this.mWallet = mWallet;
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

    public WalletAdapter(Context context, WalletAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public WalletAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet, parent, false);
        return new WalletAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final WalletAdapter.VideoHolder holder, final int position) {
        final Wallet mWallet = this.mWallet.get(position);

        if (selectedPosition == position) {
            holder.mLlDetails.setVisibility(View.VISIBLE);
            holder.mTxtTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.img_drop_up, null), null);
        } else {
            holder.mLlDetails.setVisibility(View.GONE);
            holder.mTxtTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.img_drop_down, null), null);
        }

        holder.mLlDetails.removeAllViews();
        for (int i = 0; i < mWallet.innerItems; i++) {
            OpensansTextView mOpenTextView1 = new OpensansTextView(mContext);
            mOpenTextView1.setText("Apr'18");
            mOpenTextView1.setTextColor(ContextCompat.getColor(mContext, R.color.app_color));
            mOpenTextView1.setTextSize(14);
            mOpenTextView1.setBackground(mContext.getResources().getDrawable(R.drawable.border_lite_white, null));

            OpensansTextView mOpenTextView2 = new OpensansTextView(mContext);
            mOpenTextView2.setText("Total Value : XXX.XXX");
            mOpenTextView2.setTextColor(ContextCompat.getColor(mContext, R.color.color_text));
            mOpenTextView2.setTextSize(14);
            mOpenTextView2.setBackground(mContext.getResources().getDrawable(R.drawable.border_lite_white, null));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 0);
            mOpenTextView1.setLayoutParams(params);
            mOpenTextView1.setPadding(15, 15, 15, 15);

            holder.mLlDetails.addView(mOpenTextView1);
        }

        holder.mTxtTitle.setText(mWallet.name);
        holder.mTxtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition == position) {
                    holder.mLlDetails.setVisibility(View.GONE);
                    holder.mTxtTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.img_drop_down, null), null);
                    selectedPosition = -1;
                } else {
                    holder.mLlDetails.setVisibility(View.VISIBLE);
                    holder.mTxtTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.img_drop_up, null), null);
                    selectedPosition = position;
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWallet.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        LinearLayout mLlDetails, mLlDetailsInner;
        TextView mTxtTitle;

        public VideoHolder(View view) {
            super(view);

            mLlDetails = (LinearLayout) view.findViewById(R.id.llDetails);
            mLlDetailsInner = (LinearLayout) view.findViewById(R.id.llDetailsInner);
            mTxtTitle = (TextView) view.findViewById(R.id.txtTitle);
        }
    }
}