package com.godavarisandroid.mystore.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.CategoriesActivity;
import com.godavarisandroid.mystore.activities.HomeActivity;
import com.godavarisandroid.mystore.activities.ModifyNextDeliveryActivity;
import com.godavarisandroid.mystore.adapters.CalendarAdapter1;
import com.godavarisandroid.mystore.adapters.NextDeliveryAdapter;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.Categories;
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by UMA on 4/22/2018.
 */
public class CalenderFragmentOldLib extends BaseFragment implements View.OnClickListener, IParseListener {
    private View view;

    private TextView mTxtModify, mTxtAddProduct, mTxtPrevious, mTxtNext, mTxtMonthYear;
    private RecyclerView mRecyclerView;
    private GridView mGridView;
    private CalendarView mCalendarView;
    private LinearLayout mLlBottom;

    private CalendarAdapter1 mCalendarAdapter;
    private Calendar mCalendar;
    private int mCurrentMonth;
    public static String currentDate;
    private ArrayList<String> selectedDates, selectedDates1;
    private ArrayList<NextDelivery> mNextDelivery = new ArrayList<>();
    private ArrayList<Categories> mCategories = new ArrayList<>();
    private Context mContext;
    private String selectedDate = "", modifiedDate = "";
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
        mCalendar = Calendar.getInstance();
        selectedDates = new ArrayList<>();
        selectedDates1 = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calender, container, false);

        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = mSimpleDateFormat.format(Calendar.getInstance().getTime());

        ((HomeActivity) getActivity()).mImgLogo.setVisibility(View.GONE);
        ((HomeActivity) getActivity()).mImgBack.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgHelp.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mTxtTitle.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mTxtTitle.setText("My Calendar");

        mContext = getActivity();
        initComponents();
        return view;
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
        getBundleData();

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
            mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = mSimpleDateFormat.parse(currentDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            modifiedDate = DateFormat.format("dd", date) + "";
        }
    }

    /*Initializing Views*/
    private void setReferences() {
        mTxtModify = view.findViewById(R.id.txtModify);
        mTxtModify.setVisibility(View.GONE);
        mTxtAddProduct = view.findViewById(R.id.txtAddProduct);
        mTxtAddProduct.setVisibility(View.GONE);

        mTxtPrevious = view.findViewById(R.id.txtPrevious);
        mTxtNext = view.findViewById(R.id.txtNext);
        mTxtMonthYear = view.findViewById(R.id.txtMonthYear);
        mTxtMonthYear.setText(DateFormat.format("MMMM yyyy", mCalendar));

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mCalendarView = view.findViewById(R.id.calendarView);
        mLlBottom = view.findViewById(R.id.llBottom);
        mLlBottom.setVisibility(View.GONE);
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mTxtModify.setOnClickListener(this);
        mTxtAddProduct.setOnClickListener(this);
        mTxtPrevious.setOnClickListener(this);
        mTxtNext.setOnClickListener(this);
        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 102 && data != null) {
            if (resultCode == Activity.RESULT_OK) {
//                currentDate = data.getStringExtra("DATE");
                mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                currentDate = mSimpleDateFormat.format(Calendar.getInstance().getTime());

                if (PopUtils.checkInternetConnection(getActivity())) {
                    requestForCalenderDatesWS();
                } else {
                    PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
                }
//                if (PopUtils.checkInternetConnection(getActivity())) {
//                    requestForCalenderProductsWS();
//                } else {
//                    PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
//                }
            }
        } else if (requestCode == 103 && data != null) {
//            currentDate = data.getStringExtra("DATE");
            mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            currentDate = mSimpleDateFormat.format(Calendar.getInstance().getTime());

            if (PopUtils.checkInternetConnection(getActivity())) {
                requestForCalenderDatesWS();
            } else {
                PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
            }
//            if (PopUtils.checkInternetConnection(getActivity())) {
//                requestForCalenderProductsWS();
//            } else {
//                PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
//            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtModify:
                if (PopUtils.checkDateTimeCondition(getContext(), currentDate)) {
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
                if (PopUtils.checkDateTimeCondition(getContext(), currentDate)) {
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
                                mCalendarView.requestFocus();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                break;
            case R.id.txtPrevious:
                /*Called when click on previous calender month*/
                if (mCalendar.get(Calendar.MONTH) == mCalendar.getActualMinimum(Calendar.MONTH)) {
                    mTxtPrevious.setVisibility(View.INVISIBLE);
                    mTxtNext.setVisibility(View.VISIBLE);

                    mCalendar.set((mCalendar.get(Calendar.YEAR) - 1), mCalendar.getActualMaximum(Calendar.MONTH), 1);
                    if (mCurrentMonth == mCalendar.get(Calendar.MONTH)) {
                        mCalendarAdapter.setItems(selectedDates);
                    } else {
                        mCalendarAdapter.setItems(selectedDates1);
                    }
                } else {
                    mTxtPrevious.setVisibility(View.INVISIBLE);
                    mTxtNext.setVisibility(View.VISIBLE);

                    mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH) - 1);
                    if (mCurrentMonth == mCalendar.get(Calendar.MONTH)) {
                        mCalendarAdapter.setItems(selectedDates);
                    } else {
                        mCalendarAdapter.setItems(selectedDates1);
                    }
                }

                refreshCalendar();
                break;
            case R.id.txtNext:
                /*Called when click on next calender month*/
                if (mCalendar.get(Calendar.MONTH) == mCalendar.getActualMaximum(Calendar.MONTH)) {
                    mTxtPrevious.setVisibility(View.VISIBLE);
                    mTxtNext.setVisibility(View.INVISIBLE);

                    mCalendar.set((mCalendar.get(Calendar.YEAR) + 1), mCalendar.getActualMinimum(Calendar.MONTH), 1);
                    if (mCurrentMonth == mCalendar.get(Calendar.MONTH)) {
                        mCalendarAdapter.setItems(selectedDates);
                    } else {
                        mCalendarAdapter.setItems(selectedDates1);
                    }
                } else {
                    mTxtPrevious.setVisibility(View.VISIBLE);
                    mTxtNext.setVisibility(View.INVISIBLE);

                    mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH) + 1);
                    if (mCurrentMonth == mCalendar.get(Calendar.MONTH)) {
                        mCalendarAdapter.setItems(selectedDates);
                    } else {
                        mCalendarAdapter.setItems(selectedDates1);
                    }
                }

                refreshCalendar();
                break;
            default:
                break;
        }
    }

    private void refreshCalendar() {
        mCalendarAdapter.refreshDays();
        mCalendarAdapter.notifyDataSetChanged();
        mTxtMonthYear.setText(DateFormat.format("MMMM yyyy", mCalendar));
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

  /*  private void requestForCategoriesWS() {
        showLoadingDialog(getActivity(), "Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(getContext()).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "categorys");
            mJsonObject.put("cid", "0");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(getActivity(), "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_CATEGORIES);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/


    /*Called when error occured from service call*/
    @Override
    public void ErrorResponse(String response, int requestCode) {
        hideLoadingDialog(mContext);
        PopUtils.alertDialog(getActivity(), response.toString(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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

                        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date = mSimpleDateFormat.parse(mDataObject.optString("date"));
                            Calendar mCalendar = new GregorianCalendar();
                            mCalendar.setTime(date);
                            String day = mCalendar.get(Calendar.DAY_OF_MONTH) + "";
                            selectedDates.add(day);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
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
                    final String todayDate = mSimpleDateFormat.format(Calendar.getInstance().getTime());
                    final Calendar todayCalendar = Calendar.getInstance();
                    final Calendar selectedCalendar = Calendar.getInstance();
                    try {
                        todayCalendar.setTime(mSimpleDateFormat.parse(todayDate));
                        selectedCalendar.setTime(mSimpleDateFormat.parse(currentDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

//                    if (!currentDate.equalsIgnoreCase(mSimpleDateFormat.format(Calendar.getInstance().getTime()))) {
                    if (selectedCalendar.getTimeInMillis() > todayCalendar.getTimeInMillis()) {
                        mTxtModify.setVisibility(View.VISIBLE);
                        mTxtAddProduct.setVisibility(View.VISIBLE);
                    } else {
                        mTxtModify.setVisibility(View.GONE);
                        mTxtAddProduct.setVisibility(View.GONE);
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
                    if (!currentDate.equalsIgnoreCase(mSimpleDateFormat.format(Calendar.getInstance().getTime()))) {
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
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        NextDeliveryAdapter mNextDeliveryAdapter = new NextDeliveryAdapter(getContext(), mNextDelivery, "Next");
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
        mGridView = view.findViewById(R.id.gridView);
        mCalendarAdapter = new CalendarAdapter1(getActivity(), mCalendar, new CalenderFragment(), mRecyclerView, mLlBottom, mTxtModify, mTxtAddProduct, modifiedDate);
        mGridView.setAdapter(mCalendarAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView date = view.findViewById(R.id.date);
                if (date instanceof TextView && !date.getText().equals("")) {
                    Intent intent = new Intent();
                    String day = date.getText().toString();
                    if (day.length() == 1) {
                        day = "0" + day;
                    }
                    Toast.makeText(getActivity(), DateFormat.format("yyyy-MM", mCalendar) + "-" + day, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mCalendarAdapter.setItems(selectedDates);
        mCalendarAdapter.notifyDataSetChanged();
        mCalendarView.requestFocus();
    }

    /*Set adapter for calender products*/
    public void setCalenderProductsAdapter(ArrayList<NextDelivery> nextDelivery, RecyclerView mRecyclerView,
                                           CalenderFragmentOldLib calenderFragment, String selectedDate,
                                           LinearLayout mLlBottom, TextView mTxtModify, TextView mTxtAddProduct) {
        /*showing modify button only for future dates*/
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final String todayDate = mSimpleDateFormat.format(Calendar.getInstance().getTime());
        final Calendar todayCalendar = Calendar.getInstance();
        final Calendar selectedCalendar = Calendar.getInstance();
        try {
            todayCalendar.setTime(mSimpleDateFormat.parse(todayDate));
            selectedCalendar.setTime(mSimpleDateFormat.parse(selectedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

//      if (!currentDate.equalsIgnoreCase(mSimpleDateFormat.format(Calendar.getInstance().getTime()))) {
        if (selectedCalendar.getTimeInMillis() > todayCalendar.getTimeInMillis()) {
            mTxtModify.setVisibility(View.VISIBLE);
            mTxtAddProduct.setVisibility(View.VISIBLE);
        } else {
            mTxtModify.setVisibility(View.GONE);
            mTxtAddProduct.setVisibility(View.GONE);
        }

        mNextDelivery = nextDelivery;
        BaseApplication.mNextDelivery = nextDelivery;
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        NextDeliveryAdapter mNextDeliveryAdapter = new NextDeliveryAdapter(getContext(), mNextDelivery, "Next");
        mRecyclerView.setAdapter(mNextDeliveryAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new NextDeliveryAdapter(getContext(), new NextDeliveryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }

    public void getSelectedDate(Context context, String date) {
        currentDate = date;
    }

}
