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
import android.widget.ImageView;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.SubscriptionActivity;
import com.godavarisandroid.mystore.fragments.SubProductsInnerFragment;
import com.godavarisandroid.mystore.models.SubProductsInner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Excentd11 on 4/27/2018.
 */

public class SubProductsInnerAdapter extends RecyclerView.Adapter<SubProductsInnerAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private SubProductsInnerAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<SubProductsInner> mSubProductsInner;
    private SubProductsInnerFragment mSubProductsInnerFragment;
    private String from = "";

    public SubProductsInnerAdapter(Context context, ArrayList<SubProductsInner> mSubProductsInner, SubProductsInnerFragment mSubProductsInnerFragment, String from) {
        mContext = context;
        this.mSubProductsInner = mSubProductsInner;
        this.mSubProductsInnerFragment = mSubProductsInnerFragment;
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

    public SubProductsInnerAdapter(Context context, SubProductsInnerAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public SubProductsInnerAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_products_inner, parent, false);
        return new SubProductsInnerAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final SubProductsInnerAdapter.VideoHolder holder, final int position) {
        final SubProductsInner mSubProductsInner = this.mSubProductsInner.get(position);

        holder.mTxtProductName.setText(mSubProductsInner.name);
        holder.mTxtDescription.setText(mSubProductsInner.cName);
        holder.mTxtQuantityName.setText(mSubProductsInner.quantityName);
        holder.mTxtPrice.setText("₹ " + mSubProductsInner.price);
        if (from.equalsIgnoreCase("4")) {
            holder.mTxtSubscribe.setText("Add");
            holder.txtGetOnce.setVisibility(View.GONE);
        } else if (from.equalsIgnoreCase("1")) {
            holder.txtGetOnce.setVisibility(View.VISIBLE);
        } else if (from.equalsIgnoreCase("3")) {
            holder.txtGetOnce.setVisibility(View.GONE);
            holder.mTxtSubscribe.setText("Add");
        } else {
            holder.txtGetOnce.setVisibility(View.GONE);
        }
        Picasso.with(mContext).load(mSubProductsInner.image).into(holder.mImgProduct);

        holder.mTxtSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, SubscriptionActivity.class);
                mIntent.putExtra("id", mSubProductsInner.id);
                mIntent.putExtra("name", mSubProductsInner.name);
                mIntent.putExtra("from", from);
                mIntent.putExtra("quantityName", mSubProductsInner.quantityName);
                mIntent.putExtra("description", mSubProductsInner.description);
                mIntent.putExtra("status", mSubProductsInner.status);
                mIntent.putExtra("price", mSubProductsInner.price);
                mIntent.putExtra("startDate", mSubProductsInner.startDate);
                mIntent.putExtra("endDate", mSubProductsInner.endDate);
                mIntent.putExtra("image", mSubProductsInner.image);
                mIntent.putExtra("cName", mSubProductsInner.cName);
                mIntent.putExtra("isGetOnce", false);
                mContext.startActivity(mIntent);
//                ((Activity) mContext).finish();
            }
        });

        holder.txtGetOnce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, SubscriptionActivity.class);
                mIntent.putExtra("id", mSubProductsInner.id);
                mIntent.putExtra("name", mSubProductsInner.name);
                mIntent.putExtra("from", from);
                mIntent.putExtra("quantityName", mSubProductsInner.quantityName);
                mIntent.putExtra("description", mSubProductsInner.description);
                mIntent.putExtra("status", mSubProductsInner.status);
                mIntent.putExtra("price", mSubProductsInner.price);
                mIntent.putExtra("startDate", mSubProductsInner.startDate);
                mIntent.putExtra("endDate", mSubProductsInner.endDate);
                mIntent.putExtra("image", mSubProductsInner.image);
                mIntent.putExtra("cName", mSubProductsInner.cName);
                mIntent.putExtra("isGetOnce", true);
                mContext.startActivity(mIntent);
//                ((Activity) mContext).finish();
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
        return mSubProductsInner.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        TextView mTxtProductName, mTxtDescription, mTxtQuantityName, mTxtPrice, mTxtSubscribe, txtGetOnce;
        ImageView mImgProduct;

        public VideoHolder(View view) {
            super(view);

            mTxtProductName = view.findViewById(R.id.txtProductName);
            mTxtDescription = view.findViewById(R.id.txtDescription);
            mTxtQuantityName = view.findViewById(R.id.txtQuantityName);
            mTxtPrice = view.findViewById(R.id.txtPrice);
            mTxtSubscribe = view.findViewById(R.id.txtSubscribe);
            mImgProduct = view.findViewById(R.id.imgProduct);
            txtGetOnce = view.findViewById(R.id.txtGetOnce);
        }
    }
}