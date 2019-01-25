package com.godavarisandroid.mystore.adapters;

import android.app.Dialog;
import android.content.Context;
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
import com.godavarisandroid.mystore.fragments.HomeFragment;
import com.godavarisandroid.mystore.models.SubCategories;
import com.godavarisandroid.mystore.utils.PopUtils;

import java.util.ArrayList;

/**
 * Created by UMA on 4/21/2018.
 */
public class AddProductSubCategoriesAdapter extends RecyclerView.Adapter<AddProductSubCategoriesAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private AddProductSubCategoriesAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<SubCategories> mSubCategories;
    private HomeFragment mHomeFragment;
    private Dialog firstDialog;

    public AddProductSubCategoriesAdapter(Context context, ArrayList<SubCategories> mSubCategories, HomeFragment mHomeFragment,
                                          Dialog firstDialog) {
        mContext = context;
        this.mSubCategories = mSubCategories;
        this.mHomeFragment = mHomeFragment;
        this.firstDialog = firstDialog;
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

    public AddProductSubCategoriesAdapter(Context context, AddProductSubCategoriesAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public AddProductSubCategoriesAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_categories, parent, false);
        return new AddProductSubCategoriesAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddProductSubCategoriesAdapter.VideoHolder holder, final int position) {
        final SubCategories mSubCategories = this.mSubCategories.get(position);

        holder.mTxtProduct.setText(mSubCategories.name);

//        holder.mImgProduct.setImageResource(mSubCategories.image);

        holder.mLlProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUtils.alertAddProducts(mContext, firstDialog);
            }
        });
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