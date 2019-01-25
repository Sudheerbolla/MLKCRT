package com.godavarisandroid.mystore.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.models.Notification;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.VideoHolder> {
    private Context mContext;
    private ArrayList<Notification> mNotification;

    public NotificationAdapter(Context context, ArrayList<Notification> mNotification) {
        mContext = context;
        this.mNotification = mNotification;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @NonNull
    @Override
    public NotificationAdapter.VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.VideoHolder holder, final int position) {
        final Notification mNotification = this.mNotification.get(position);
        holder.mTxtNotification.setText(mNotification.description);
        holder.txtTime.setText(mNotification.time);
        holder.txtTitle.setText(mNotification.title);
    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        TextView mTxtNotification,txtTitle,txtTime;

        public VideoHolder(View view) {
            super(view);
            mTxtNotification = view.findViewById(R.id.txtNotification);
            txtTitle = view.findViewById(R.id.txtTitle);
            txtTime = view.findViewById(R.id.txtTime);
        }
    }

}