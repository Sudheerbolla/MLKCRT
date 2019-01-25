package com.godavarisandroid.mystore.adapters;

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
import com.godavarisandroid.mystore.fragments.ModifyNextDeliveryFragment;
import com.godavarisandroid.mystore.fragments.NextDeliveryFragment;
import com.godavarisandroid.mystore.models.NextDelivery;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Excentd11 on 4/26/2018.
 */

public class NextDeliveryAdapter extends RecyclerView.Adapter<NextDeliveryAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private NextDeliveryAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<NextDelivery> mNextDelivery;
    private String from;
    private int quantity;
    private boolean showDelHistory = false;

    public NextDeliveryAdapter(Context context, ArrayList<NextDelivery> mNextDelivery, NextDeliveryFragment mNextDeliveryFragment,
                               ModifyNextDeliveryFragment mModifyNextDeliveryFragment, String from) {
        mContext = context;
        this.mNextDelivery = mNextDelivery;
        NextDeliveryFragment mNextDeliveryFragment1 = mNextDeliveryFragment;
        ModifyNextDeliveryFragment mModifyNextDeliveryFragment1 = mModifyNextDeliveryFragment;
        this.from = from;
    }

    public NextDeliveryAdapter(Context context, ArrayList<NextDelivery> mNextDelivery, String from, boolean showDelHistory) {
        mContext = context;
        this.mNextDelivery = mNextDelivery;
        this.from = from;
        this.showDelHistory = showDelHistory;
    }

    public NextDeliveryAdapter(Context context, ArrayList<NextDelivery> mNextDelivery, String from) {
        mContext = context;
        this.mNextDelivery = mNextDelivery;
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

    public NextDeliveryAdapter(Context context, NextDeliveryAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public NextDeliveryAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_next_delivery, parent, false);
        return new NextDeliveryAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final NextDeliveryAdapter.VideoHolder holder, final int position) {
        final NextDelivery mNextDelivery1 = this.mNextDelivery.get(position);

        if (from.equalsIgnoreCase("Modify")) {
            holder.mLlRight.setVisibility(View.VISIBLE);
            holder.mTxtQuantityCount.setVisibility(View.GONE);
            holder.mTxtDeleted.setText("Reactivate");
        } else {
            holder.mLlRight.setVisibility(View.GONE);
            holder.mTxtQuantityCount.setVisibility(View.VISIBLE);
        }

        if (showDelHistory) {
            holder.txtDeliveryStatus.setText("Delivery Status: " + mNextDelivery1.deliveryStatus);
            holder.txtDeliveryStatus.setVisibility(View.VISIBLE);
        } else {
            holder.txtDeliveryStatus.setVisibility(View.GONE);
        }

        if (from.equalsIgnoreCase("Next") && mNextDelivery1.status.equalsIgnoreCase("0")) {
            holder.mLlDelete.setVisibility(View.GONE);
            holder.mLlRight.setVisibility(View.VISIBLE);
            holder.mTxtDeleted.setVisibility(View.VISIBLE);
            holder.mTxtQuantityCount.setVisibility(View.GONE);
        } else if (from.equalsIgnoreCase("Next") && mNextDelivery1.status.equalsIgnoreCase("1")) {
            holder.mLlDelete.setVisibility(View.GONE);
            holder.mTxtDeleted.setVisibility(View.GONE);
            holder.mLlRight.setVisibility(View.GONE);
            holder.mTxtQuantityCount.setVisibility(View.VISIBLE);
        } else if (from.equalsIgnoreCase("Modify") && mNextDelivery1.getStatus().equalsIgnoreCase("0")) {
            holder.mLlDelete.setVisibility(View.GONE);
            holder.mTxtDeleted.setVisibility(View.VISIBLE);
        } else if (from.equalsIgnoreCase("Modify") && mNextDelivery1.getStatus().equalsIgnoreCase("1")) {
            holder.mLlDelete.setVisibility(View.VISIBLE);
            holder.mTxtDeleted.setVisibility(View.GONE);
        }

      /*  if (mNextDelivery1.status.equalsIgnoreCase("0")) {
            holder.mLlDelete.setVisibility(View.GONE);
            holder.mTxtDeleted.setVisibility(View.VISIBLE);
        } else {
            holder.mLlDelete.setVisibility(View.VISIBLE);
            holder.mTxtDeleted.setVisibility(View.GONE);
        }*/

        holder.mTxtQuantity.setText(mNextDelivery1.quantity + "");
        holder.mTxtProduct.setText(mNextDelivery1.productName);
        holder.mTxtQuantityName.setText(mNextDelivery1.quanityName);
        holder.mTxtPrice.setText("₹ " + mNextDelivery1.price);
        holder.mTxtQuantityCount.setText("Quantity: " + mNextDelivery1.quantity);
        holder.mTxtQuantity.setText(mNextDelivery1.quantity + "");
        Picasso.with(mContext).load(mNextDelivery1.image).into(holder.mImgProduct);

        holder.mTxtDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = mNextDelivery1.quantity;
                if (quantity > 1) {
                    quantity = quantity - 1;
                    mNextDelivery1.setQuantity(quantity);
                    notifyDataSetChanged();
                }
            }
        });
        holder.mTxtIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = mNextDelivery1.quantity;
                if (quantity < 100) {
                    quantity = quantity + 1;
                    mNextDelivery1.setQuantity(quantity);
                    notifyDataSetChanged();
                }
            }
        });
        holder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUtils.alertTwoButtonDialog(mContext, "Are you sure, you want to delete this product from the subscription?",
                        "YES", "NO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                mNextDelivery.remove(position);
//                                notifyDataSetChanged();
                                mNextDelivery1.setStatus("0");
                                notifyDataSetChanged();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
            }
        });

        holder.mTxtDeleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equalsIgnoreCase("Modify")) {
                    mNextDelivery1.setStatus("1");
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNextDelivery.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        LinearLayout mLlRight, mLlDelete;
        ImageView mImgProduct;
        TextView mTxtProduct, mTxtQuantityName, mTxtPrice, mTxtQuantityCount, mTxtDecrease, mTxtIncrease, mTxtQuantity, mTxtDeleted, txtDelete, txtDeliveryStatus;

        public VideoHolder(View view) {
            super(view);
            mLlRight = view.findViewById(R.id.llRight);
            txtDeliveryStatus = view.findViewById(R.id.txtDeliveryStatus);
            mLlDelete = view.findViewById(R.id.llDelete);
            mImgProduct = view.findViewById(R.id.imgProduct);
            txtDelete = view.findViewById(R.id.txtDelete);
            mTxtProduct = view.findViewById(R.id.txtProduct);
            mTxtQuantityName = view.findViewById(R.id.txtQuantityName);
            mTxtPrice = view.findViewById(R.id.txtPrice);
            mTxtQuantityCount = view.findViewById(R.id.txtQuantityCount);
            mTxtDecrease = view.findViewById(R.id.txtDecrease);
            mTxtIncrease = view.findViewById(R.id.txtIncrease);
            mTxtQuantity = view.findViewById(R.id.txtQuantity);
            mTxtDeleted = view.findViewById(R.id.txtDeleted);
        }
    }
}