package com.godavarisandroid.mystore.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.fragments.CalenderFragment;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.NextDelivery;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.godavarisandroid.mystore.utils.UserDetails;
import com.godavarisandroid.mystore.webUtils.ServerResponse;
import com.godavarisandroid.mystore.webUtils.WebServices;
import com.godavarisandroid.mystore.webUtils.WsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Excentd11 on 5/22/2018.
 */

public class CalendarAdapter1 extends BaseAdapter {
    static final int FIRST_DAY_OF_WEEK = 0; // Sunday = 0, Monday = 1
    private Context mContext;

    private java.util.Calendar month;
    private Calendar selectedDate;
    private ArrayList<String> items;
    private CalenderFragment calenderFragment;
    private int selectedPosition = -1;

    private ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerView;
    private LinearLayout mLlBottom;
    private TextView mTxtModify, mTxtAddProduct;
    private ArrayList<NextDelivery> mNextDelivery = new ArrayList<>();

    private String arrayList = "";
    private String modifiedDate = "";

    public CalendarAdapter1(Context c, Calendar monthCalendar, CalenderFragment calenderFragment,
                            RecyclerView mRecyclerView, LinearLayout mLlBottom, TextView txtModify,
                            TextView mTxtAddProduct, String modifiedDate) {
        this.calenderFragment = calenderFragment;
        this.mTxtModify = txtModify;
        this.mTxtAddProduct = mTxtAddProduct;
        this.mRecyclerView = mRecyclerView;
        this.mLlBottom = mLlBottom;
        this.modifiedDate = modifiedDate;

        month = monthCalendar;
        selectedDate = (Calendar) monthCalendar.clone();
        mContext = c;
        month.set(Calendar.DAY_OF_MONTH, 1);
        this.items = new ArrayList<String>();
        refreshDays();
    }

    public void setItems(ArrayList<String> items) {
        for (int i = 0; i != items.size(); i++) {
            if (items.get(i).length() == 1) {
                items.set(i, "0" + items.get(i));
            }
        }
        this.items = items;
    }

    public int getCount() {
        return days.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final TextView dayView;
        final LinearLayout mLlDate;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_calender, null);
        }

        dayView = (TextView) v.findViewById(R.id.date);
        mLlDate = (LinearLayout) v.findViewById(R.id.llDate);
        mLlDate.setTag(0);
        mLlDate.setBackgroundResource(0);

        // disable empty days from the beginning
        if (days[position].equals("")) {
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else {
            // mark current day as focused
            if (month.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) && month.get(Calendar.MONTH)
                    == selectedDate.get(Calendar.MONTH) && days[position].equals("" + selectedDate.get(Calendar.DAY_OF_MONTH))) {
//                v.setBackgroundResource(R.color.colorAccent);
                mLlDate.setBackgroundResource(R.drawable.circle_border_app);
                dayView.setTextColor(ContextCompat.getColor(mContext, R.color.color_black));
            } else {
                mLlDate.setBackgroundResource(0);
              /*  v.setBackgroundResource(0);
                dayView.setTextColor(mContext.getColor(R.color.color_black));*/
                if (selectedPosition == position) {
                    v.setBackgroundResource(R.color.app_color);
                    dayView.setTextColor(ContextCompat.getColor(mContext, R.color.color_white));
                } else {
                    v.setBackgroundResource(0);
                    dayView.setTextColor(ContextCompat.getColor(mContext, R.color.color_black));
                }
            }
        }

        dayView.setText(days[position]);
        if (days[position].equalsIgnoreCase(modifiedDate)) {
//            v.setBackgroundResource(R.color.app_color);
            v.setBackgroundResource(R.color.app_color);
            dayView.setTextColor(ContextCompat.getColor(mContext, R.color.color_white));
        }

        mLlDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifiedDate = "0";
                if (mLlDate.getTag().equals(0)) {
                    mLlDate.setTag(1);
                    v.setBackgroundResource(R.color.app_color);
                    dayView.setTextColor(ContextCompat.getColor(mContext, R.color.color_white));
                    selectedPosition = position;
                    notifyDataSetChanged();

                    String day = dayView.getText().toString();
                    if (day.length() == 1) {
                        day = "0" + day;
                    }

                    calenderFragment.currentDate = android.text.format.DateFormat.format("yyyy-MM", month) + "-" + day;
                    if (PopUtils.checkInternetConnection(mContext)) {
                        BaseApplication.nextDeliveryDate = android.text.format.DateFormat.format("yyyy-MM", month) + "-" + day;
                        requestForCalenderProductsWS(android.text.format.DateFormat.format("yyyy-MM", month) + "-" + day);
                    } else {
                        PopUtils.alertDialog(mContext, mContext.getString(R.string.pls_check_internet), null);
                    }
                    calenderFragment.getSelectedDate(mContext, android.text.format.DateFormat.format("yyyy-MM", month) + "-" + day);
                } else {
                    v.setBackgroundResource(0);
                    mLlDate.setTag(0);
                    dayView.setTextColor(ContextCompat.getColor(mContext, R.color.color_black));
                }
            }
        });

        // create date string for comparison
        String date = days[position];

        if (date.length() == 1) {
            date = "0" + date;
        }
        String monthStr = "" + (month.get(Calendar.MONTH) + 1);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }
        // show icon if date is not empty and it exists in the items array
        ImageView iw = (ImageView) v.findViewById(R.id.date_icon);
        if (date.length() > 0 && items != null && items.contains(date)) {
            iw.setVisibility(View.VISIBLE);
        } else {
            iw.setVisibility(View.INVISIBLE);
        }
        return v;
    }

    /*Requesting for calender products service call*/
    private void requestForCalenderProductsWS(final String selectedDate) {
        showLoadingDialog(mContext, "Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(mContext).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "next_delivery");
            mJsonObject.put("date", selectedDate);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(mContext, "POST", WebServices.BASE_URL, mJsonObject, params,
                    new IParseListener() {
                        @Override
                        public void ErrorResponse(String response, int requestCode) {
                            hideLoadingDialog(mContext);
                            PopUtils.alertDialog(mContext, response.toString(), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });
                        }

                        @Override
                        public void SuccessResponse(String response, int requestCode) {
                            hideLoadingDialog(mContext);
                            switch (requestCode) {
                                case WsUtils.WS_CODE_NEXT_DAY_DELIVERY:
                                    responseForCalenderProducts(response, selectedDate);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }, WsUtils.WS_CODE_NEXT_DAY_DELIVERY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Respons efor calender products service call*/
    private void responseForCalenderProducts(String response, String selectedDate) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLlBottom.setVisibility(View.VISIBLE);
                    mTxtModify.setVisibility(View.VISIBLE);

                    JSONArray mDataArray = mJsonObject.getJSONArray("data");
                    mNextDelivery.clear();
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);

                        mNextDelivery.add(new NextDelivery(mDataObject.optString("s_id"),
                                mDataObject.optString("user_id"),
                                mDataObject.optString("product_id"),
                                mDataObject.optString("start_date"),
                                mDataObject.optString("end_date"),
                                mDataObject.optString("sub_type"),
                                mDataObject.optString("sub_quantity"),
                                mDataObject.optString("sd_status"),
                                mDataObject.optString("subscription_pause_exp_date"),
                                mDataObject.optString("is_paused"),
                                mDataObject.optString("sr_number"),
                                mDataObject.optString("sd_id"),
                                mDataObject.optString("date"),
                                mDataObject.optInt("quantity"),
                                mDataObject.optString("delivery_status"),
                                mDataObject.optString("p_name"),
                                mDataObject.optString("image_path") + mDataObject.optString("image"),
                                mDataObject.optString("quantity_name"),
                                mDataObject.optString("description"),
                                mDataObject.optString("cat_id"),
                                mDataObject.optString("is_all_cities"),
                                mDataObject.optString("brand_id"),
                                mDataObject.optString("price")));
                    }

                    calenderFragment.setCalenderProductsAdapter(mNextDelivery, mRecyclerView, calenderFragment, selectedDate,
                            mLlBottom, mTxtModify, mTxtAddProduct);
//                    setAdapter();
                } else if (mJsonObject.getString("status").equalsIgnoreCase("400")) {
                    mRecyclerView.setVisibility(View.GONE);
                    mLlBottom.setVisibility(View.VISIBLE);
                    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                     /*showing modify button only for future dates*/
                    final String todayDate = mSimpleDateFormat.format(Calendar.getInstance().getTime());
                    final Calendar todayCalendar = Calendar.getInstance();
                    final Calendar selectedCalendar = Calendar.getInstance();
                    try {
                        todayCalendar.setTime(mSimpleDateFormat.parse(todayDate));
                        selectedCalendar.setTime(mSimpleDateFormat.parse(selectedDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

//                   if (!currentDate.equalsIgnoreCase(mSimpleDateFormat.format(Calendar.getInstance().getTime()))) {
                    if (selectedCalendar.getTimeInMillis() > todayCalendar.getTimeInMillis()) {
                        mTxtModify.setVisibility(View.GONE);
                        mTxtAddProduct.setVisibility(View.VISIBLE);
                    } else {
                        mTxtModify.setVisibility(View.GONE);
                        mTxtAddProduct.setVisibility(View.GONE);
                    }
                } else if (mJsonObject.getString("status").equalsIgnoreCase("402")) {
                    mLlBottom.setVisibility(View.GONE);
                    mTxtModify.setVisibility(View.GONE);
                    mTxtAddProduct.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setAdapter() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext);
        NextDeliveryAdapter mNextDeliveryAdapter = new NextDeliveryAdapter(mContext, mNextDelivery, "Next");
        mRecyclerView.setAdapter(mNextDeliveryAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new NextDeliveryAdapter(mContext, new NextDeliveryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }

    public void showLoadingDialog(Context context, final String title, final boolean isCancelable) {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = new ProgressDialog(context, R.style.progressDialog);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage(title);
            mProgressDialog.setCancelable(isCancelable);
            mProgressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideLoadingDialog(Context mContext) {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            mProgressDialog = null;
        } catch (Exception e) {
            mProgressDialog = null;
        }
    }

    public void refreshDays() {
        // clear items
//        items.clear();

        int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDay = (int) month.get(Calendar.DAY_OF_WEEK);
       /* int lastDay = 30;
        int firstDay = 1;*/

        // figure size of the array
        if (firstDay == 1) {
            days = new String[lastDay + (FIRST_DAY_OF_WEEK * 6)];
        } else {
            days = new String[lastDay + firstDay - (FIRST_DAY_OF_WEEK + 1)];
        }

        int j = FIRST_DAY_OF_WEEK;

        // populate empty days before first real day
        if (firstDay > 1) {
            for (j = 0; j < firstDay - FIRST_DAY_OF_WEEK; j++) {
                days[j] = "";
            }
        } else {
            for (j = 0; j < FIRST_DAY_OF_WEEK * 6; j++) {
                days[j] = "";
            }
            j = FIRST_DAY_OF_WEEK * 6 + 1; // sunday => 1, monday => 7
        }

        // populate days
        int dayNumber = 1;
        for (int i = j - 1; i < days.length; i++) {
            days[i] = "" + dayNumber;
            dayNumber++;
        }
    }

    // references to our items
    public String[] days;
}
