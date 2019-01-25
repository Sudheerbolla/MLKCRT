package com.godavarisandroid.mystore.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.CategoriesActivity;
import com.godavarisandroid.mystore.activities.HomeActivity;
import com.godavarisandroid.mystore.activities.ModifyNextDeliveryActivity;
import com.godavarisandroid.mystore.adapters.NextDeliveryAdapter;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.Categories;
import com.godavarisandroid.mystore.models.NextDelivery;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.godavarisandroid.mystore.utils.UserDetails;
import com.godavarisandroid.mystore.utils.calenderutils.materialcalendarview.CalendarDay;
import com.godavarisandroid.mystore.utils.calenderutils.materialcalendarview.EventDecorator;
import com.godavarisandroid.mystore.utils.calenderutils.materialcalendarview.MaterialCalendarView;
import com.godavarisandroid.mystore.utils.calenderutils.materialcalendarview.OnDateSelectedListener;
import com.godavarisandroid.mystore.utils.calenderutils.materialcalendarview.OnMonthChangedListener;
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

public class CalenderFragmentNew extends com.godavarisandroid.mystore.fragments.BaseFragment implements OnDateSelectedListener, OnMonthChangedListener, View.OnClickListener, IParseListener {

    private MaterialCalendarView widget;
    private View view;
    private TextView mTxtModify, mTxtAddProduct;
    private RecyclerView mRecyclerView;
    private LinearLayout mLlBottom;

    private Calendar mCalendar, minCal, maxCal;
    private int mCurrentMonth;
    public static String currentDate;
    private ArrayList<String> selectedDates;
    private ArrayList<NextDelivery> mNextDelivery = new ArrayList<>();
    private ArrayList<Categories> mCategories = new ArrayList<>();
    private HomeActivity mContext;
    private SimpleDateFormat mSimpleDateFormat;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mContext = (HomeActivity) getActivity();

            mCalendar = Calendar.getInstance();
            selectedDates = new ArrayList<>();
            minCal = Calendar.getInstance();
            minCal.add(Calendar.MONTH, -1);
            minCal.set(Calendar.DATE, 1);
            maxCal = Calendar.getInstance();
            maxCal.add(Calendar.MONTH, +1);
            maxCal.set(Calendar.DATE, maxCal.getActualMaximum(Calendar.DATE));

            mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            currentDate = mSimpleDateFormat.format(mCalendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar_new, container, false);
        mContext.mImgLogo.setVisibility(View.GONE);
        mContext.mImgBack.setVisibility(View.VISIBLE);
        mContext.mImgHelp.setVisibility(View.VISIBLE);
        mContext.mTxtTitle.setVisibility(View.VISIBLE);
        mContext.mTxtTitle.setText("My Calendar");
        initComponents();
        return view;
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
        getBundleData();
        widget.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        widget.state().edit().setMinimumDate(minCal).setMaximumDate(maxCal).commit();
        widget.setCurrentDate(mCalendar);
        widget.setSelectedDate(mCalendar);
        if (PopUtils.checkInternetConnection(getActivity())) {
            requestForCalenderDatesWS();
        } else {
            PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
        }
    }

    /*getting data through bundle*/
    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("SELECTEDDATE") && !bundle.getString("SELECTEDDATE").equalsIgnoreCase("")) {
            currentDate = bundle.getString("SELECTEDDATE");
        }
    }

    /*Initializing Views*/
    private void setReferences() {
        widget = view.findViewById(R.id.calendarView);

        mTxtModify = view.findViewById(R.id.txtModify);
        mTxtModify.setVisibility(View.GONE);
        mTxtAddProduct = view.findViewById(R.id.txtAddProduct);
        mTxtAddProduct.setVisibility(View.GONE);

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mLlBottom = view.findViewById(R.id.llBottom);
        mLlBottom.setVisibility(View.GONE);
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mTxtModify.setOnClickListener(this);
        mTxtAddProduct.setOnClickListener(this);
        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);
        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        widget.setSelectedDate(date);
        currentDate = mSimpleDateFormat.format(date.getDate());
        if (PopUtils.checkInternetConnection(mContext)) {
            BaseApplication.nextDeliveryDate = currentDate;
            requestForCalenderProductsWS();
        } else {
            PopUtils.alertDialog(mContext, mContext.getString(R.string.pls_check_internet), null);
        }
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        if (date.getMonth() == mCalendar.get(Calendar.MONTH)) {
            currentDate = mSimpleDateFormat.format(mCalendar.getTime());
            widget.setSelectedDate(mCalendar.getTime());
        } else {
            currentDate = mSimpleDateFormat.format(date.getDate());
            widget.setSelectedDate(date);
        }
        if (PopUtils.checkInternetConnection(mContext)) {
            BaseApplication.nextDeliveryDate = currentDate;
            requestForCalenderProductsWS();
        } else {
            PopUtils.alertDialog(mContext, mContext.getString(R.string.pls_check_internet), null);
        }
    }

    /**
     * Simulate an API call to show how to add decorators
     */
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final ArrayList<CalendarDay> dates = new ArrayList<>();
            for (int i = 0; i < selectedDates.size(); i++) {
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = mSimpleDateFormat.parse(selectedDates.get(i));
                    Calendar mCalendar = new GregorianCalendar();
                    mCalendar.setTime(date);
                    dates.add(CalendarDay.from(mCalendar));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);
            if (getActivity().isFinishing()) {
                return;
            }
            widget.addDecorator(new EventDecorator(Color.BLUE, calendarDays));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 102 && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if (data.getExtras().containsKey("DATE")) {
                    currentDate = data.getExtras().getString("DATE");
                } else
                    currentDate = mSimpleDateFormat.format(Calendar.getInstance().getTime());

                if (PopUtils.checkInternetConnection(getActivity())) {
                    requestForCalenderDatesWS();
                } else {
                    PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
                }
            }
        } else if (requestCode == 103 && data != null) {
            mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (data.getExtras().containsKey("DATE")) {
                currentDate = data.getExtras().getString("DATE");
            } else
                currentDate = mSimpleDateFormat.format(Calendar.getInstance().getTime());

            if (PopUtils.checkInternetConnection(getActivity())) {
                requestForCalenderDatesWS();
            } else {
                PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtModify:
                if (PopUtils.checkDateTimeCondition(getActivity(), currentDate)) {
                    PopUtils.alertDialog(getActivity(), "Oops!! Your request for tomorrow cant be proccessed as, cut off time is crossed. Please try with a future date.", null);
                } else {
                    /*Navigateing modify next delivery screen*/
                    Intent mIntent = new Intent(getActivity(), ModifyNextDeliveryActivity.class);
                    mIntent.putExtra("ARRAYLIST", BaseApplication.mNextDelivery);
                    mIntent.putExtra("DATE", currentDate);
                    startActivityForResult(mIntent, 102);
                }
                break;
            case R.id.txtAddProduct:
                if (PopUtils.checkDateTimeCondition(getActivity(), currentDate)) {
                    PopUtils.alertDialog(getActivity(), "Oops!! Your request for tomorrow cant be proccessed as, cut off time is crossed. Please try with a future date.", null);
                } else {
                    /*Navigating to categories screen*/
                    BaseApplication.nextDeliveryDate = currentDate;
                    Intent mIntent1 = new Intent(getActivity(), CategoriesActivity.class);
                    mIntent1.putExtra("from", "4");
                    mIntent1.putExtra("DATE", currentDate);
                    startActivityForResult(mIntent1, 103);
                }
                break;
            case R.id.txtSave:
                PopUtils.alertTwoButtonDialog(getActivity(), "Your modification request for delivery is successful",
                        "OK", "CANCEL", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setAdapter();
                                mLlBottom.setVisibility(View.GONE);
                                mTxtModify.setVisibility(View.VISIBLE);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                break;
            default:
                break;
        }
    }

    /*Requesting scheduled calender dates service call*/
    private void requestForCalenderDatesWS() {
        showLoadingDialog(getActivity(), "Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(getContext()).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "calender_dates");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(getActivity(), "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_MY_CURRENT_CALENDAR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Requesting for calenderv products*/
    private void requestForCalenderProductsWS() {
        showLoadingDialog(mContext, "Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(getContext()).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "next_delivery");
            mJsonObject.put("date", currentDate);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(mContext, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_NEXT_DAY_DELIVERY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ErrorResponse(String response, int requestCode) {
        hideLoadingDialog(mContext);
        PopUtils.alertDialog(mContext, response, null);
    }

    /*Called when success occured from service call*/
    @Override
    public void SuccessResponse(String response, int requestCode) {
        hideLoadingDialog(mContext);
        switch (requestCode) {
            case WsUtils.WS_CODE_MY_CURRENT_CALENDAR:
                responseForMyCalender(response);
                break;
            case WsUtils.WS_CODE_NEXT_DAY_DELIVERY:
                responseForCalenderProducts(response);
                break;
            case WsUtils.WS_CODE_CATEGORIES:
                responseForCategories(response);
                break;
            default:
                break;
        }
    }

    /*Response for categories service call*/
    private void responseForCategories(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray mDataArray = mJsonObject.getJSONArray("data");
                    mCategories.clear();
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);

                        mCategories.add(new Categories(mDataObject.optString("cid"),
                                mDataObject.optString("cat_name"),
                                mDataObject.optString("cat_image"),
                                mDataObject.optString("image_path"), null));
                    }

                    PopUtils.alertAddProductCategory(getActivity(), mCategories);
                } else {
                    Toast.makeText(mContext, "No products were founs st this time for adding", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Response for mycalender service call*/
    private void responseForMyCalender(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray mDataArray = mJsonObject.getJSONArray("data");
                    selectedDates.clear();

                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);
                        selectedDates.add(mDataObject.optString("date"));
//                        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                        try {
//                            Date date = mSimpleDateFormat.parse(mDataObject.optString("date"));
//                            Calendar mCalendar = new GregorianCalendar();
//                            mCalendar.setTime(date);
//                            String day = mCalendar.get(Calendar.DAY_OF_MONTH) + "";
//                            selectedDates.add(day);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
                    }

                    setCalenderAdapter();

                    if (PopUtils.checkInternetConnection(getActivity())) {
                        requestForCalenderProductsWS();
                    } else {
                        PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
                    }
                } else {
                    selectedDates.clear();
                    setCalenderAdapter();
                    if (PopUtils.checkInternetConnection(getActivity())) {
                        requestForCalenderProductsWS();
                    } else {
                        PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Response for calender products service call*/
    private void responseForCalenderProducts(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLlBottom.setVisibility(View.VISIBLE);

                    /*showing modify button only for future dates*/
//                    final String todayDate = mSimpleDateFormat.format(Calendar.getInstance().getTime());
//                    final Calendar todayCalendar = Calendar.getInstance();
//                    final Calendar selectedCalendar = Calendar.getInstance();
//                    try {
//                        todayCalendar.setTime(mSimpleDateFormat.parse(todayDate));
//                        selectedCalendar.setTime(mSimpleDateFormat.parse(currentDate));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
////                    if (!currentDate.equalsIgnoreCase(mSimpleDateFormat.format(Calendar.getInstance().getTime()))) {
//                    if (selectedCalendar.getTimeInMillis() > todayCalendar.getTimeInMillis()) {
//                        mTxtModify.setVisibility(View.VISIBLE);
//                        mTxtAddProduct.setVisibility(View.VISIBLE);
//                    } else {
//                        mTxtModify.setVisibility(View.GONE);
//                        mTxtAddProduct.setVisibility(View.GONE);
//                    }
                    Calendar tomCal = Calendar.getInstance();
                    tomCal.add(Calendar.DATE, +1);

                    if (widget.getSelectedDate().isBefore(CalendarDay.from(tomCal))) {
                        mTxtModify.setVisibility(View.GONE);
                        mTxtAddProduct.setVisibility(View.GONE);
                    } else {
                        mTxtModify.setVisibility(View.VISIBLE);
                        mTxtAddProduct.setVisibility(View.VISIBLE);
                    }

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

                    BaseApplication.mNextDelivery = mNextDelivery;
                    setAdapter();
                } else if (mJsonObject.getString("status").equalsIgnoreCase("400")) {
                    mRecyclerView.setVisibility(View.GONE);
                    mLlBottom.setVisibility(View.VISIBLE);

                    Calendar tomCal = Calendar.getInstance();
                    tomCal.add(Calendar.DATE, +1);

                    if (widget.getSelectedDate().isBefore(CalendarDay.from(tomCal))) {
                        mTxtModify.setVisibility(View.GONE);
                        mTxtAddProduct.setVisibility(View.GONE);
                    } else {
                        mTxtModify.setVisibility(View.GONE);
                        mTxtAddProduct.setVisibility(View.VISIBLE);
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
        boolean showDelHistory = false;
        Calendar tomCal = Calendar.getInstance();
        tomCal.add(Calendar.DATE, +1);

        showDelHistory = widget.getSelectedDate().isBefore(CalendarDay.from(tomCal));

        mRecyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        NextDeliveryAdapter mNextDeliveryAdapter = new NextDeliveryAdapter(getContext(), mNextDelivery, "Next", showDelHistory);
        mRecyclerView.setAdapter(mNextDeliveryAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new NextDeliveryAdapter(getContext(), new NextDeliveryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }

    /*Set adapter for calender*/
    private void setCalenderAdapter() {
        mCalendar = Calendar.getInstance();
        mCurrentMonth = mCalendar.get(Calendar.MONTH);
        new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
    }

}
