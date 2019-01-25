package com.godavarisandroid.mystore.adapters;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.ActiveSubscriptionInnerActivity;
import com.godavarisandroid.mystore.fragments.ActiveSubscriptionFragment;
import com.godavarisandroid.mystore.models.ActiveSubscriptions;

import java.util.ArrayList;

/**
 * Created by Excentd11 on 5/3/2018.
 */

public class InActiveSubscriptionAdapter extends RecyclerView.Adapter<InActiveSubscriptionAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private InActiveSubscriptionAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<ActiveSubscriptions> mActiveSubscriptions;
    private ActiveSubscriptionFragment mActiveSubscriptionFragment;

    public InActiveSubscriptionAdapter(Context context, ArrayList<ActiveSubscriptions> mActiveSubscriptions, ActiveSubscriptionFragment mActiveSubscriptionFragment) {
        mContext = context;
        this.mActiveSubscriptions = mActiveSubscriptions;
        this.mActiveSubscriptionFragment = mActiveSubscriptionFragment;
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

    public InActiveSubscriptionAdapter(Context context, InActiveSubscriptionAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public InActiveSubscriptionAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inactive_subscriptions, parent, false);
        return new InActiveSubscriptionAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final InActiveSubscriptionAdapter.VideoHolder holder, final int position) {
        final ActiveSubscriptions mActiveSubscriptions = this.mActiveSubscriptions.get(position);

        holder.mTxtViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, ActiveSubscriptionInnerActivity.class);
                mContext.startActivity(mIntent);
            }
        });
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
        TextView mTxtViewDetails;

        public VideoHolder(View view) {
            super(view);
            mTxtViewDetails = (TextView) view.findViewById(R.id.txtViewDetails);
        }
    }
}