package com.godavarisandroid.mystore.adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.fragments.HomeFragment;
import com.godavarisandroid.mystore.models.Categories;

import java.util.ArrayList;

/**
 * Created by UMA on 4/21/2018.
 */
public class AddProductCategoriesAdapter extends RecyclerView.Adapter<AddProductCategoriesAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private AddProductCategoriesAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<Categories> mCategories;
    private HomeFragment mHomeFragment;
    private int selectedPosition = -1;
    private Dialog firstDialog;

    public AddProductCategoriesAdapter(Context context, ArrayList<Categories> mCategories, HomeFragment mHomeFragment, Dialog dialog) {
        mContext = context;
        this.mCategories = mCategories;
        this.mHomeFragment = mHomeFragment;
        this.firstDialog = dialog;
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

    public AddProductCategoriesAdapter(Context context, AddProductCategoriesAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public AddProductCategoriesAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories, parent, false);
        return new AddProductCategoriesAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddProductCategoriesAdapter.VideoHolder holder, final int position) {
        final Categories mCategories = this.mCategories.get(position);

        if (selectedPosition == position) {
            holder.mRecyclerView.setVisibility(View.VISIBLE);
            holder.mTxtCategory.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.img_drop_up, null), null);
        } else {
            holder.mRecyclerView.setVisibility(View.GONE);
            holder.mTxtCategory.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.img_drop_down, null), null);
        }

       /* GridLayoutManager mLinearLayoutManager = new GridLayoutManager(mContext, 3);
        AddProductSubCategoriesAdapter mAddProductSubCategoriesAdapter = new AddProductSubCategoriesAdapter(mContext,
                mCategories.mSubCategories, new HomeFragment(), firstDialog);
        holder.mRecyclerView.setAdapter(mAddProductSubCategoriesAdapter);
        holder.mRecyclerView.scrollToPosition(0); //use to focus the item with index
        mAddProductSubCategoriesAdapter.notifyDataSetChanged();
        holder.mRecyclerView.setLayoutManager(mLinearLayoutManager);
        holder.mRecyclerView.addOnItemTouchListener(new SubCategoriesAdapter(mContext, new SubCategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));

        holder.mRlHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition == position) {
                    holder.mRecyclerView.setVisibility(View.GONE);
                    holder.mTxtCategory.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.img_drop_down, null), null);
                    selectedPosition = -1;
                } else {
                    holder.mRecyclerView.setVisibility(View.VISIBLE);
                    holder.mTxtCategory.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.img_drop_up, null), null);
                    selectedPosition = position;
                    notifyDataSetChanged();
                }
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
        return mCategories.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        ImageView mImgCategory;
        TextView mTxtCategory;
        RecyclerView mRecyclerView;
        RelativeLayout mRlHeader;

        public VideoHolder(View view) {
            super(view);

            mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            mTxtCategory = (TextView) view.findViewById(R.id.txtCategory);
            mImgCategory = (ImageView) view.findViewById(R.id.imgCategory);
            mRlHeader = (RelativeLayout) view.findViewById(R.id.rlHeader);
        }
    }
}