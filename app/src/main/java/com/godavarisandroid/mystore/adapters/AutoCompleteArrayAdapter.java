package com.godavarisandroid.mystore.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.models.DialogList;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteArrayAdapter extends ArrayAdapter<DialogList> {
    private final Context mContext;
    private final List<DialogList> mDepartments;
    private final List<DialogList> mDepartmentsAll;
    private final int mLayoutResourceId;

    public AutoCompleteArrayAdapter(Context context, int resource, List<DialogList> departments) {
        super(context, resource, departments);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.mDepartments = new ArrayList<>(departments);
        this.mDepartmentsAll = new ArrayList<>(departments);
    }

    public int getCount() {
        return mDepartments.size();
    }

    public DialogList getItem(int position) {
        return mDepartments.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }
            DialogList department = getItem(position);
            TextView name = convertView.findViewById(R.id.text1);
            name.setText(department.value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return ((DialogList) resultValue).value;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<DialogList> departmentsSuggestion = new ArrayList<>();
                if (constraint != null) {
                    for (DialogList department : mDepartmentsAll) {
                        if (department.value.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            departmentsSuggestion.add(department);
                        }
                    }
                    filterResults.values = departmentsSuggestion;
                    filterResults.count = departmentsSuggestion.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDepartments.clear();
                if (results != null && results.count > 0) {
                    for (Object object : (List<?>) results.values) {
                        if (object instanceof DialogList) {
                            mDepartments.add((DialogList) object);
                        }
                    }
                    notifyDataSetChanged();
                } else if (constraint == null) {
                    // no filter, add entire original list back in
                    mDepartments.addAll(mDepartmentsAll);
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}