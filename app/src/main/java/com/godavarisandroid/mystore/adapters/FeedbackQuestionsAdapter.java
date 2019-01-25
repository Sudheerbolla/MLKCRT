package com.godavarisandroid.mystore.adapters;

import android.app.ProgressDialog;
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

import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.fragments.HomeFragment;
import com.godavarisandroid.mystore.models.FeedbackQuestions;

import java.util.ArrayList;

/**
 * Created by Excentd11 on 5/18/2018.
 */

public class FeedbackQuestionsAdapter extends RecyclerView.Adapter<FeedbackQuestionsAdapter.VideoHolder> implements RecyclerView.OnItemTouchListener {
    private Context mContext;
    private FeedbackQuestionsAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<FeedbackQuestions> mFeedbackQuestions;
    private HomeFragment mHomeFragment;
    private int selectedPosition = -1;

    private ProgressDialog mProgressDialog;

    public FeedbackQuestionsAdapter(Context context, ArrayList<FeedbackQuestions> mFeedbackQuestions) {
        mContext = context;
        this.mFeedbackQuestions = mFeedbackQuestions;
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

    public FeedbackQuestionsAdapter(Context context, FeedbackQuestionsAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public FeedbackQuestionsAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_questions, parent, false);
        return new FeedbackQuestionsAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final FeedbackQuestionsAdapter.VideoHolder holder, final int position) {
        final FeedbackQuestions mFeedbackQuestions = this.mFeedbackQuestions.get(position);

        holder.mTxtFeedback.setText(mFeedbackQuestions.name);

        if (selectedPosition == position) {
            BaseApplication.feedbackId = mFeedbackQuestions.id;
            holder.mImgCheckSelector.setImageResource(R.drawable.img_checkbox_select);
        } else {
            holder.mImgCheckSelector.setImageResource(R.drawable.img_checkbox_unselect);
        }

        holder.mLlFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = position;
                notifyDataSetChanged();
            }
        });
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public int getItemCount() {
        return mFeedbackQuestions.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        ImageView mImgCheckSelector;
        TextView mTxtFeedback;
        LinearLayout mLlFeedback;

        public VideoHolder(View view) {
            super(view);

            mTxtFeedback = (TextView) view.findViewById(R.id.txtFeedback);
            mImgCheckSelector = (ImageView) view.findViewById(R.id.imgCheckBox);
            mImgCheckSelector.setTag(0);
            mLlFeedback = (LinearLayout) view.findViewById(R.id.llFeedback);
        }
    }
}