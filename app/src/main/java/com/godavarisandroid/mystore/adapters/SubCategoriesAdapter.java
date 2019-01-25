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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.SubProductsFragment;
import com.godavarisandroid.mystore.fragments.HomeFragment;
import com.godavarisandroid.mystore.models.SubCategories;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by UMA on 4/21/2018.
 */
public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private SubCategoriesAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<SubCategories> mSubCategories;
    private HomeFragment mHomeFragment;
    private String from = "";

    public SubCategoriesAdapter(Context context, ArrayList<SubCategories> mSubCategories, HomeFragment mHomeFragment, String from) {
        this.mContext = context;
        this.from = from;
        this.mSubCategories = mSubCategories;
        this.mHomeFragment = mHomeFragment;
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

    public SubCategoriesAdapter(Context context, SubCategoriesAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public SubCategoriesAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_categories, parent, false);
        return new SubCategoriesAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final SubCategoriesAdapter.VideoHolder holder, final int position) {
        final SubCategories mSubCategories1 = this.mSubCategories.get(position);

        holder.mTxtProduct.setText(mSubCategories1.name);
        Picasso.with(mContext).load(mSubCategories1.image).into(holder.mImgProduct);

        holder.mLlProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, SubProductsFragment.class);
                mIntent.putExtra("c_id", mSubCategories1.id);
                mIntent.putExtra("c_name", mSubCategories1.name);
                mIntent.putExtra("from", from);
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
        return mSubCategories.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        LinearLayout mLlProduct;
        ImageView mImgProduct;
        TextView mTxtProduct;

        public VideoHolder(View view) {
            super(view);
            mLlProduct = (LinearLayout) view.findViewById(R.id.llProduct);
            mImgProduct = (ImageView) view.findViewById(R.id.imgProduct);
            mTxtProduct = (TextView) view.findViewById(R.id.txtProduct);
        }
    }
}