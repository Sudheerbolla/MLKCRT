package com.godavarisandroid.mystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.godavarisandroid.mystore.models.DialogList;
import com.godavarisandroid.mystore.R;

import java.util.ArrayList;

/**
 * Created by Excentd11 on 7/15/2017.
 */

public class DialogListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<DialogList> mDialogList;
    private LayoutInflater mInflater;

    private String textType;

    public DialogListAdapter(Context context, ArrayList<DialogList> mDialogList, String textType) {
        this.mContext = context;
        this.mDialogList = mDialogList;
        this.textType = textType;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDialogList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDialogList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class Holder {
        TextView mTxtValue;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Holder mHolder;
        if (view == null) {
            mHolder = new Holder();
            view = mInflater.inflate(R.layout.item_dialog_list_center, null);
            mHolder.mTxtValue = (TextView) view.findViewById(R.id.txtValue);

            view.setTag(mHolder);
        } else {
            mHolder = (Holder) view.getTag();
        }
        DialogList mDialogList = (DialogList) getItem(position);
        mHolder.mTxtValue.setText(mDialogList.value);

       /* if (!mDialogList.id.equalsIgnoreCase("")) {
            mHolder.mTxtValue.setTextColor(Color.parseColor(mDialogList.id));
        } else {
            mHolder.mTxtValue.setTextColor(Color.parseColor("#000000"));
        }*/

        return view;
    }
}
