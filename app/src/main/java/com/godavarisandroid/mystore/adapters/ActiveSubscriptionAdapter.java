package com.godavarisandroid.mystore.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.fragments.ActiveSubscriptionFragment;
import com.godavarisandroid.mystore.models.ActiveSubscriptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by UMA on 4/22/2018.
 */
public class ActiveSubscriptionAdapter extends RecyclerView.Adapter<ActiveSubscriptionAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private ActiveSubscriptionAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<ActiveSubscriptions> mActiveSubscriptions;
    private ActiveSubscriptionFragment mActiveSubscriptionFragment;
    private String from;


    public ActiveSubscriptionAdapter(Context context, ArrayList<ActiveSubscriptions> mActiveSubscriptions,
                                     ActiveSubscriptionFragment mActiveSubscriptionFragment, String from) {
        mContext = context;
        this.mActiveSubscriptions = mActiveSubscriptions;
        this.mActiveSubscriptionFragment = mActiveSubscriptionFragment;
        this.from = from;
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

    public ActiveSubscriptionAdapter(Context context, ActiveSubscriptionAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public ActiveSubscriptionAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_subscriptions, parent, false);
        return new ActiveSubscriptionAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final ActiveSubscriptionAdapter.VideoHolder holder, final int position) {
        final ActiveSubscriptions mActiveSubscriptions = this.mActiveSubscriptions.get(position);

        holder.mTxtProductName.setText(mActiveSubscriptions.pName);
        holder.mTxtPrice.setText("₹ " + mActiveSubscriptions.price);

        if (mActiveSubscriptions.status.equalsIgnoreCase("A")) {
            holder.mImgDot.setImageResource(R.drawable.img_dot_green);
        } else {
            holder.mImgDot.setImageResource(R.drawable.img_dot_yellow);
        }

        Picasso.with(mContext).load(mActiveSubscriptions.image).into(holder.mImgProduct);

        /*holder.mTxtViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, ActiveSubscriptionInnerActivity.class);
                mIntent.putExtra("from", from);
                mIntent.putExtra("s_id", mActiveSubscriptions.sId);
                mIntent.putExtra("product_id", mActiveSubscriptions.productId);
                mIntent.putExtra("start_date", mActiveSubscriptions.startDate);
                mIntent.putExtra("end_date", mActiveSubscriptions.endDate);
                mIntent.putExtra("status", mActiveSubscriptions.status);
                mIntent.putExtra("product_name", mActiveSubscriptions.pName);
                mIntent.putExtra("quantity_name", mActiveSubscriptions.quantityName);
                mIntent.putExtra("description", mActiveSubscriptions.description);
                mIntent.putExtra("price", mActiveSubscriptions.price);
                mIntent.putExtra("sub_type", mActiveSubscriptions.subType);
                mIntent.putExtra("image", mActiveSubscriptions.image);
                mContext.startActivity(mIntent);

                *//*mActiveSubscriptionFragment.navigateToActiveSubscriptionInnerActivity(mContext, from,
                        mActiveSubscriptions.sId, mActiveSubscriptions.productId, mActiveSubscriptions.startDate,
                        mActiveSubscriptions.endDate, mActiveSubscriptions.status, mActiveSubscriptions.pName,
                        mActiveSubscriptions.quantityName, mActiveSubscriptions.description, mActiveSubscriptions.price,
                        mActiveSubscriptions.subType, mActiveSubscriptions.image);*//*
            }
        });*/
    }

    public static void navigateFragment(Fragment fragment, String tag, Bundle bundle, FragmentActivity fragmentActivity) {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        fragmentTransaction.replace(R.id.container, fragment, tag);
        if (tag != null) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
    }

    @Override
    public int getItemCount() {
        return mActiveSubscriptions.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        TextView mTxtViewDetails, mTxtProductName, mTxtPrice;
        ImageView mImgProduct, mImgDot;

        public VideoHolder(View view) {
            super(view);
            mTxtProductName = (TextView) view.findViewById(R.id.txtProductName);
            mTxtPrice = (TextView) view.findViewById(R.id.txtPrice);
            mTxtViewDetails = (TextView) view.findViewById(R.id.txtViewDetails);
            mImgProduct = (ImageView) view.findViewById(R.id.imgProduct);
            mImgDot = (ImageView) view.findViewById(R.id.imgDot);
        }
    }
}