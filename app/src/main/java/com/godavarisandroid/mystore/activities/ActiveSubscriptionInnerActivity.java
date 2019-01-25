package com.godavarisandroid.mystore.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.interfaces.DatePickerInterface;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.godavarisandroid.mystore.utils.UserDetails;
import com.godavarisandroid.mystore.utils.calenderutils.materialcalendarview.CalendarDay;
import com.godavarisandroid.mystore.webUtils.ServerResponse;
import com.godavarisandroid.mystore.webUtils.WebServices;
import com.godavarisandroid.mystore.webUtils.WsUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by UMA on 4/22/2018.
 */
public class ActiveSubscriptionInnerActivity extends BaseActivity implements View.OnClickListener, IParseListener {
    private TextView mTxtTitle, mTxtProductName, mTxtSubCategory, mTxtQuantity, mTxtPrice, mTxtModify, mTxtPause, mTxtStartDate, mTxtDelete,
            mTxtEndDate, mDeactivationDate, mTxtDeactivationDate, mTxtDescription, mTxtSubType, mTxtQuantityCount, mTxtPauseDescription;
    private ImageView mImgLogo, mImgBack, mImgNotification, mImgProduct;
    private LinearLayout mLlRight;

    private Calendar mCalendar = Calendar.getInstance();
    private int day1, month1, year1;
    private String sId, productId, startDate, endDate, status, productName, quantityName, description, price, subType, image,
            quantityObject, pauseStartDate, pauseEndDate;
    private String modifyStartDate, modifyEndDate;

    private JSONObject quantityObject1;

    private String pausingDate, reactivationDate;
    private SimpleDateFormat mSimpleDateFormat;
    private boolean isEveryDay = false, isMonthlyOnce = false, isMonthlyTwice = false, isAlternateDay = false, isWeekSchedule = false;
    private TextView mTxtEveryDay, mTxtMonthlyOnce, mTxtMonthlyTwice, mTxtAlternateDay, mTxtWeekSchedule,
            mTxtSundayQ, mTxtMondayQ, mTxtTuesdayQ, mTxtWednesdayQ, mTxtThursdayQ, mTxtFridayQ, mTxtSaturdayQ,
            mTxtDayOne, mTxtDayTwo, mTxtDayOneText, mSelectQuantity, mTxtSelectQuantity, txtSchedule;
    private LinearLayout mLDays, mLlMonthly, mLlDayOne, mLlDayTwo;
    private View mViewSelectQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_subscription_inner);

        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        initComponents();
    }

    private void initComponents() {
        year1 = mCalendar.get(Calendar.YEAR);
        month1 = mCalendar.get(Calendar.MONTH);
        day1 = mCalendar.get(Calendar.DAY_OF_MONTH);

        setReferences();
        getBundleData();
        setClickListeners();
    }

    /*Initializing Views*/
    private void setReferences() {
        mTxtTitle = findViewById(R.id.txtTitle);
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText("Subscription Details");

        mTxtProductName = findViewById(R.id.txtProduct);
        mTxtSubCategory = findViewById(R.id.txtSubCategory);
        mTxtQuantity = findViewById(R.id.txtQuantity);
        mTxtPrice = findViewById(R.id.txtPrice);
        mTxtModify = findViewById(R.id.txtModify);
        mTxtPause = findViewById(R.id.txtPause);
        txtSchedule = findViewById(R.id.txtSchedule);

        mTxtPauseDescription = findViewById(R.id.txtPauseDescription);
        mTxtPauseDescription.setVisibility(View.GONE);

        mTxtDescription = findViewById(R.id.txtDescription);
        mTxtStartDate = findViewById(R.id.txtStartDate);
        mTxtEndDate = findViewById(R.id.txtEndDate);
        mDeactivationDate = findViewById(R.id.deactivationDate);
        mTxtDeactivationDate = findViewById(R.id.txtDeactivationDate);
        mTxtSubType = findViewById(R.id.txtSubType);
        mTxtQuantityCount = findViewById(R.id.txtQuantityCount);

        mImgLogo = findViewById(R.id.imgLogo);
        mImgLogo.setVisibility(View.GONE);

        mTxtDelete = findViewById(R.id.txtDelete);

        mImgBack = findViewById(R.id.imgBack);
        mImgBack.setVisibility(View.VISIBLE);

        mImgNotification = findViewById(R.id.imgNotification);
        mImgNotification.setVisibility(View.VISIBLE);

        mImgProduct = findViewById(R.id.imgProduct);

        mLlRight = findViewById(R.id.llRight);

        mTxtEveryDay = findViewById(R.id.txtEveryDay);
        mTxtMonthlyOnce = findViewById(R.id.txtMonthlyOnce);
        mTxtMonthlyTwice = findViewById(R.id.txtMonthlyTwice);
        mTxtAlternateDay = findViewById(R.id.txtAlternateDay);
        mTxtWeekSchedule = findViewById(R.id.txtWeekSchedule);
        mTxtSundayQ = findViewById(R.id.txtSundayQ);
        mTxtMondayQ = findViewById(R.id.txtMondayQ);
        mTxtTuesdayQ = findViewById(R.id.txtTuesdayQ);
        mTxtWednesdayQ = findViewById(R.id.txtWednesdayQ);
        mTxtThursdayQ = findViewById(R.id.txtThursdayQ);
        mTxtFridayQ = findViewById(R.id.txtFridayQ);
        mTxtSaturdayQ = findViewById(R.id.txtSaturdayQ);
        mTxtDayOne = findViewById(R.id.txtDayOne);
        mTxtDayOneText = findViewById(R.id.txtDayOneText);
        mTxtDayTwo = findViewById(R.id.txtDayTwo);
        mLDays = findViewById(R.id.llDays);
        mLlMonthly = findViewById(R.id.llMonthly);
        mLlDayOne = findViewById(R.id.llDayOne);
        mLlDayTwo = findViewById(R.id.llDayTwo);
        mSelectQuantity = findViewById(R.id.selectQuantity);
        mTxtSelectQuantity = findViewById(R.id.txtSelectQuantity);
        mViewSelectQuantity = findViewById(R.id.viewSelectQuantity);
    }

    /*getting data through intent*/
    private void getBundleData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("from").equalsIgnoreCase("1")) {
                mLlRight.setVisibility(View.VISIBLE);
                mTxtModify.setVisibility(View.VISIBLE);
                mDeactivationDate.setVisibility(View.GONE);
                mTxtDeactivationDate.setVisibility(View.GONE);
            } else {
                mLlRight.setVisibility(View.GONE);
                mTxtModify.setVisibility(View.GONE);
                mTxtPauseDescription.setVisibility(View.GONE);
                mDeactivationDate.setVisibility(View.VISIBLE);
                mTxtDeactivationDate.setVisibility(View.VISIBLE);
            }

            sId = intent.getStringExtra("s_id");
            productId = intent.getStringExtra("product_id");
            startDate = intent.getStringExtra("start_date");
            reactivationDate = intent.getStringExtra("pause_start_date");
            pauseStartDate = intent.getStringExtra("pause_start_date");
            pauseEndDate = intent.getStringExtra("pause_end_date");
//            pauseStartDate = "2018-07-10";
//            pauseEndDate = "2018-07-07";
            mTxtStartDate.setText(startDate);

            endDate = intent.getStringExtra("end_date");
            mTxtEndDate.setText(endDate);

            status = intent.getStringExtra("status");
            if (status.equalsIgnoreCase("A")) {
                mTxtPause.setText("Pause");
                mTxtDelete.setVisibility(View.VISIBLE);
                mTxtModify.setVisibility(View.VISIBLE);
                mTxtPauseDescription.setVisibility(View.GONE);
            } else {
                mTxtPause.setText("Cancel Pause");
                mTxtDelete.setVisibility(View.GONE);
                mTxtModify.setVisibility(View.GONE);
                if (intent.getStringExtra("from").equalsIgnoreCase("1")) {
                    mTxtPauseDescription.setVisibility(View.VISIBLE);
                }
                mTxtPauseDescription.setText("This Subscription is paused from " + pauseStartDate + " to " + pauseEndDate);
            }

            mTxtPause.setVisibility(endDate.equalsIgnoreCase(startDate) ? View.GONE : View.VISIBLE);

            productName = intent.getStringExtra("product_name");
            mTxtProductName.setText(productName);

            quantityName = intent.getStringExtra("quantity_name");
            mTxtQuantity.setText(quantityName);

            description = intent.getStringExtra("description");
            mTxtDescription.setText(description);

            price = intent.getStringExtra("price");
            mTxtPrice.setText("₹" + price);

            subType = intent.getStringExtra("sub_type");
            mTxtSubType.setText(subType);

            image = intent.getStringExtra("image");
            Picasso.with(this).load(image).into(mImgProduct);

            quantityObject = intent.getStringExtra("sub_quantity");

            mTxtDeactivationDate.setText(intent.getStringExtra("date_of_status"));

            /*set quantity*/
            try {
                quantityObject1 = new JSONObject(intent.getStringExtra("sub_quantity") + "");
                if (subType.equalsIgnoreCase("Weekly schedule")) {
                    scheduleWeekScheduleSelected();

                    JSONArray dayArray = quantityObject1.getJSONArray("day");
                    JSONArray quantityArray = quantityObject1.getJSONArray("quantity");
                    for (int i = 0; i < dayArray.length(); i++) {
                        if (dayArray.optString(i).equalsIgnoreCase("Sun")) {
                            mTxtSundayQ.setText(quantityArray.get(i) + "");
                        } else if (dayArray.optString(i).equalsIgnoreCase("Mon")) {
                            mTxtMondayQ.setText(quantityArray.get(i) + "");
                        } else if (dayArray.optString(i).equalsIgnoreCase("Tue")) {
                            mTxtTuesdayQ.setText(quantityArray.get(i) + "");
                        } else if (dayArray.optString(i).equalsIgnoreCase("Wed")) {
                            mTxtWednesdayQ.setText(quantityArray.get(i) + "");
                        } else if (dayArray.optString(i).equalsIgnoreCase("Thu")) {
                            mTxtThursdayQ.setText(quantityArray.get(i) + "");
                        } else if (dayArray.optString(i).equalsIgnoreCase("Fri")) {
                            mTxtFridayQ.setText(quantityArray.get(i) + "");
                        } else if (dayArray.optString(i).equalsIgnoreCase("Sat")) {
                            mTxtSaturdayQ.setText(quantityArray.get(i) + "");
                        }
                    }
                } else if (subType.equalsIgnoreCase("Monthly Once")) {
                    scheduleMonthlyOnceSelected();

                    mTxtDayOne.setText(quantityObject1.optString("day"));
                    mTxtSelectQuantity.setText(quantityObject1.optString("quantity"));
                } else if (subType.equalsIgnoreCase("Every Day")) {
                    scheduleEveryDaySelected();

                    mTxtSelectQuantity.setText(quantityObject1.optString("quantity"));
                } else if (subType.equalsIgnoreCase("Monthly Twice")) {
                    scheduleMonthlyTwiceSelected();

                    mTxtDayOne.setText(quantityObject1.getJSONArray("dates").optString(0) + "");
                    mTxtDayTwo.setText(quantityObject1.getJSONArray("dates").optString(1) + "");
                    mTxtSelectQuantity.setText(quantityObject1.optString("quantity"));
                } else if (subType.equalsIgnoreCase("Alternate Days")) {
                    scheduleAlternateDaySelected();

                    mTxtSelectQuantity.setText(quantityObject1.optString("quantity"));
                }

                if (subType.equalsIgnoreCase("Every Day") && startDate.equalsIgnoreCase(endDate)) {
                    txtSchedule.setText("Once");
                } else {
                    txtSchedule.setText(subType);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Schedule when every day selected*/
    public void scheduleEveryDaySelected() {
        if (isEveryDay) {
        } else {
            mTxtEveryDay.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_select), null, null, null);
            mTxtMonthlyOnce.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtMonthlyTwice.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtAlternateDay.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtWeekSchedule.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mLDays.setVisibility(View.GONE);
            mLlMonthly.setVisibility(View.GONE);
            mSelectQuantity.setVisibility(View.VISIBLE);
            mTxtSelectQuantity.setVisibility(View.VISIBLE);
            mViewSelectQuantity.setVisibility(View.VISIBLE);
            isEveryDay = true;
            isMonthlyOnce = false;
            isMonthlyTwice = false;
            isAlternateDay = false;
            isWeekSchedule = false;
            mTxtEveryDay.setTextColor(ContextCompat.getColor(this, R.color.app_color));
            mTxtMonthlyOnce.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtMonthlyTwice.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtAlternateDay.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtWeekSchedule.setTextColor(ContextCompat.getColor(this, R.color.color_text));
        }
    }

    /*Schedule when monthly once selected*/
    public void scheduleMonthlyOnceSelected() {
        if (isMonthlyOnce) {
        } else {
            mTxtEveryDay.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtMonthlyOnce.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_select), null, null, null);
            mTxtMonthlyTwice.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtAlternateDay.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtWeekSchedule.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mLDays.setVisibility(View.GONE);
            mLlMonthly.setVisibility(View.VISIBLE);
            mLlDayTwo.setVisibility(View.GONE);
//            mTxtDayOneText.setText("Select a Date");
            mSelectQuantity.setVisibility(View.VISIBLE);
            mTxtSelectQuantity.setVisibility(View.VISIBLE);
            mViewSelectQuantity.setVisibility(View.VISIBLE);
            isEveryDay = false;
            isMonthlyOnce = true;
            isMonthlyTwice = false;
            isAlternateDay = false;
            isWeekSchedule = false;
            mTxtEveryDay.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtMonthlyOnce.setTextColor(ContextCompat.getColor(this, R.color.app_color));
            mTxtMonthlyTwice.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtAlternateDay.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtWeekSchedule.setTextColor(ContextCompat.getColor(this, R.color.color_text));
        }
    }

    /*Schedule when monthly twice selected*/
    public void scheduleMonthlyTwiceSelected() {
        if (isMonthlyTwice) {
        } else {
            mTxtEveryDay.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtMonthlyOnce.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtMonthlyTwice.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_select), null, null, null);
            mTxtAlternateDay.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtWeekSchedule.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mLDays.setVisibility(View.GONE);
            mLlMonthly.setVisibility(View.VISIBLE);
            mLlDayTwo.setVisibility(View.VISIBLE);
//            mTxtDayOneText.setText("Day One");
            mSelectQuantity.setVisibility(View.VISIBLE);
            mTxtSelectQuantity.setVisibility(View.VISIBLE);
            mViewSelectQuantity.setVisibility(View.VISIBLE);
            isEveryDay = false;
            isMonthlyOnce = false;
            isMonthlyTwice = true;
            isAlternateDay = false;
            isWeekSchedule = false;
            mTxtEveryDay.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtMonthlyOnce.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtMonthlyTwice.setTextColor(ContextCompat.getColor(this, R.color.app_color));
            mTxtAlternateDay.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtWeekSchedule.setTextColor(ContextCompat.getColor(this, R.color.color_text));
        }
    }

    /*Schedule when alternate day selected*/
    public void scheduleAlternateDaySelected() {
        if (isAlternateDay) {
        } else {
            mTxtEveryDay.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtMonthlyOnce.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtMonthlyTwice.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtAlternateDay.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_select), null, null, null);
            mTxtWeekSchedule.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mLDays.setVisibility(View.GONE);
            mLlMonthly.setVisibility(View.GONE);
            mSelectQuantity.setVisibility(View.VISIBLE);
            mTxtSelectQuantity.setVisibility(View.VISIBLE);
            mViewSelectQuantity.setVisibility(View.VISIBLE);
            isEveryDay = false;
            isMonthlyOnce = false;
            isMonthlyTwice = false;
            isAlternateDay = true;
            isWeekSchedule = false;
            mTxtEveryDay.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtMonthlyOnce.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtMonthlyTwice.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtAlternateDay.setTextColor(ContextCompat.getColor(this, R.color.app_color));
            mTxtWeekSchedule.setTextColor(ContextCompat.getColor(this, R.color.color_text));
        }
    }

    /*Schedule when week selected*/
    public void scheduleWeekScheduleSelected() {
        if (isWeekSchedule) {
        } else {
            mTxtEveryDay.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtMonthlyOnce.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtMonthlyTwice.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtAlternateDay.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_unselect), null, null, null);
            mTxtWeekSchedule.setCompoundDrawablesWithIntrinsicBounds(ActivityCompat.getDrawable(this, R.drawable.img_checkbox_select), null, null, null);
            mLDays.setVisibility(View.VISIBLE);
            mLlMonthly.setVisibility(View.GONE);
            mSelectQuantity.setVisibility(View.GONE);
            mTxtSelectQuantity.setVisibility(View.GONE);
            mViewSelectQuantity.setVisibility(View.GONE);
            isEveryDay = false;
            isMonthlyOnce = false;
            isMonthlyTwice = false;
            isAlternateDay = false;
            isWeekSchedule = true;
            mTxtEveryDay.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtMonthlyOnce.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtMonthlyTwice.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtAlternateDay.setTextColor(ContextCompat.getColor(this, R.color.color_text));
            mTxtWeekSchedule.setTextColor(ContextCompat.getColor(this, R.color.app_color));
        }
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mTxtModify.setOnClickListener(this);
        mTxtPause.setOnClickListener(this);
        mImgLogo.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mTxtDelete.setOnClickListener(this);
        mImgNotification.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtModify:
                final Calendar todayCalendar = Calendar.getInstance();
                final Calendar tomorrowCalendar = Calendar.getInstance();
                final Calendar subscriptionStartDate = Calendar.getInstance();
                final Calendar subscriptionEndDate = Calendar.getInstance();
                try {
                    mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    todayCalendar.setTime(mSimpleDateFormat.parse(mSimpleDateFormat.format(Calendar.getInstance().getTime())));
                    tomorrowCalendar.setTime(mSimpleDateFormat.parse(mSimpleDateFormat.format(Calendar.getInstance().getTime())));
                    subscriptionStartDate.setTime(mSimpleDateFormat.parse(mTxtStartDate.getText().toString().trim()));
                    subscriptionEndDate.setTime(mSimpleDateFormat.parse(mTxtEndDate.getText().toString().trim()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                /*logic starts here*/
                if (PopUtils.isSevenPmCrossed(this)) {
                    tomorrowCalendar.roll(Calendar.DATE, 1);
                    if (subscriptionStartDate.getTimeInMillis() > tomorrowCalendar.getTimeInMillis()) {
                        /*Allow*/
                        modifySubscription();
                    } else {
                        /*Dont Allow*/
                        int year = todayCalendar.get(Calendar.YEAR);
                        int month = todayCalendar.get(Calendar.MONTH) + 1;
                        int date = todayCalendar.get(Calendar.DATE);
                        String cutOffTime = UserDetails.getInstance(this).getCutOffTime();
                        PopUtils.alertDialog(this,
                                "Oops! You have crossed the cut off time (" + cutOffTime + ":00 of " +
                                        year + "-" + month + "-" + date +
                                        ") for modification of an active subscription. Please try to modify it tomorrow before " + cutOffTime + ":00",
                                null);
                    }
                } else {
                    /*Allow*/
                    modifySubscription();
                }
                break;
            case R.id.txtStartDate:
                showCalender(true, year1, month1, day1 + 1, mTxtStartDate, true);
                break;
            case R.id.txtEndDate:
                if (TextUtils.isEmpty(mTxtStartDate.getText().toString().trim())) {
                    PopUtils.alertDialog(this, "Please select start date first", null);
                } else {
                    showCalender(true, year1, month1, day1 + 1, mTxtEndDate, false);
                }
                break;
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.txtDelete:
                final Calendar todayCalendar3 = Calendar.getInstance();
                final Calendar tomorrowCalendar3 = Calendar.getInstance();
                final Calendar subscriptionStartDate3 = Calendar.getInstance();
                final Calendar subscriptionEndDate3 = Calendar.getInstance();
                try {
                    mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    todayCalendar3.setTime(mSimpleDateFormat.parse(mSimpleDateFormat.format(Calendar.getInstance().getTime())));
                    tomorrowCalendar3.setTime(mSimpleDateFormat.parse(mSimpleDateFormat.format(Calendar.getInstance().getTime())));
                    subscriptionStartDate3.setTime(mSimpleDateFormat.parse(mTxtStartDate.getText().toString().trim()));
                    subscriptionEndDate3.setTime(mSimpleDateFormat.parse(mTxtEndDate.getText().toString().trim()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                /*logic starts here*/
                if (PopUtils.isSevenPmCrossed(this)) {
                    tomorrowCalendar3.roll(Calendar.DATE, 1);
                    if (subscriptionStartDate3.getTimeInMillis() > tomorrowCalendar3.getTimeInMillis()) {
                        /*Allow*/
                        if (PopUtils.checkInternetConnection(this)) {
                            PopUtils.alertTwoButtonDialog(this, "Please confirm to stop subscription", "Confirm", "Cancel", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestForDeactivateSubscriptionWS();
                                }
                            }, null);
                        } else {
                            PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                        }
                    } else {
                        /*Dont Allow*/
                        int year = todayCalendar3.get(Calendar.YEAR);
                        int month = todayCalendar3.get(Calendar.MONTH) + 1;
                        int date = todayCalendar3.get(Calendar.DATE);

                        String cutOffTime = UserDetails.getInstance(this).getCutOffTime();
                        PopUtils.alertDialog(this,
                                "Oops! You have crossed the cut off time (" + cutOffTime + ":00 of " +
                                        year + "-" + month + "-" + date +
                                        ") for deleting the subscription. Please try to delete it tomorrow before " + cutOffTime + ":00",
                                null);
                    }
                } else {
                    if (subscriptionEndDate3.getTimeInMillis() > todayCalendar3.getTimeInMillis()) {
                        /*Allow*/
                        if (PopUtils.checkInternetConnection(this)) {
                            PopUtils.alertTwoButtonDialog(this, "Please confirm to stop subscription", "Confirm", "Cancel", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestForDeactivateSubscriptionWS();
                                }
                            }, null);
                        } else {
                            PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                        }
                    } else {
                        /*Dont Allow*/
                        PopUtils.alertDialog(this,
                                "Oops! As your subscription is ending today, i will get automatically move to inactive subscriptions" +
                                        " tomorrow. Hence you cannot delete the subscription",
                                null);
                    }
                }

                /*final Calendar todayCalendar1 = Calendar.getInstance();
                final Calendar subscriptionStartDate1 = Calendar.getInstance();
                final Calendar subscriptionEndDate1 = Calendar.getInstance();
                try {
                    mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    todayCalendar1.setTime(mSimpleDateFormat.parse(mSimpleDateFormat.format(Calendar.getInstance().getTime())));
                    subscriptionStartDate1.setTime(mSimpleDateFormat.parse(mTxtStartDate.getText().toString().trim()));
                    subscriptionEndDate1.setTime(mSimpleDateFormat.parse(mTxtEndDate.getText().toString().trim()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (PopUtils.isSevenPmCrossed()) {
                    todayCalendar1.roll(Calendar.DATE, 1);

                    if (subscriptionEndDate1.getTimeInMillis() > todayCalendar1.getTimeInMillis()) {
                        *//*allow*//*
                        if (PopUtils.checkInternetConnection(this)) {
                            requestForDeactivateSubscriptionWS();
                        } else {
                            PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                        }
                    } else {
                        *//*dont allow*//*
                        PopUtils.alertDialog(this,
                                "Oops! You have crossed the cut off time (7 PM of " + mSimpleDateFormat.format(Calendar.getInstance().getTime())
                                        + ") for deleting the subscription. " +
                                        "Please try to deete it tomorrow before 7 PM", null);
                    }
                } else {
                    if (subscriptionEndDate1.getTimeInMillis() > todayCalendar1.getTimeInMillis()) {
                        *//*allow*//*
                        if (PopUtils.checkInternetConnection(this)) {
                            requestForDeactivateSubscriptionWS();
                        } else {
                            PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                        }
                    } else {
                        *//*dont allow*//*
                        PopUtils.alertDialog(this,
                                "Oops! As your subscription is ending today, it will get automatically move to inactive subscriptions tomorrow." +
                                        "Hence you cannot delete the subscription", null);
                    }
                }*/
                break;
            case R.id.txtPause:
                final String todayDate = mSimpleDateFormat.format(Calendar.getInstance().getTime());

                if (mTxtPause.getText().toString().equalsIgnoreCase("Pause")) {
                    /*pausing product*/
                    pausingFunctionality();
                } else {
                    /*Reactivating product*/
                    reactivatingFunctionality();
                }
                break;
            case R.id.imgNotification:
                navigateActivity(new Intent(this, HelpActivity.class), false);
                break;
            default:
                break;
        }
    }

    /*Modify Subscription*/
    private void modifySubscription() {
        Intent mIntent = new Intent(this, SubscriptionActivity.class);
        mIntent.putExtra("from", "2");
        mIntent.putExtra("s_id", sId);
        mIntent.putExtra("product_id", productId);
        mIntent.putExtra("start_date", mTxtStartDate.getText().toString().trim());
        mIntent.putExtra("end_date", mTxtEndDate.getText().toString().trim());
        mIntent.putExtra("status", status);
        mIntent.putExtra("product_name", productName);
        mIntent.putExtra("quantity_name", quantityName);
        mIntent.putExtra("description", description);
        mIntent.putExtra("price", price);
        mIntent.putExtra("sub_type", mTxtSubType.getText().toString().trim());
        mIntent.putExtra("image", image);
        if (mTxtSubType.getText().toString().trim().equalsIgnoreCase("Every Day")) {
            mIntent.putExtra("subType", "Every Day");
            mIntent.putExtra("quantity", mTxtSelectQuantity.getText().toString());
        } else if (mTxtSubType.getText().toString().trim().equalsIgnoreCase("Monthly Once")) {
            mIntent.putExtra("subType", "Monthly Once");
            mIntent.putExtra("day_one", mTxtDayOne.getText().toString().trim());
            mIntent.putExtra("quantity", mTxtSelectQuantity.getText().toString());
        } else if (mTxtSubType.getText().toString().trim().equalsIgnoreCase("Monthly Twice")) {
            mIntent.putExtra("subType", "Monthly Twice");
            mIntent.putExtra("day_one", mTxtDayOne.getText().toString().trim());
            mIntent.putExtra("day_two", mTxtDayTwo.getText().toString().trim());
            mIntent.putExtra("quantity", mTxtSelectQuantity.getText().toString());
        } else if (mTxtSubType.getText().toString().trim().equalsIgnoreCase("Alternate Days")) {
            mIntent.putExtra("subType", "Alternate Days");
            mIntent.putExtra("quantity", mTxtSelectQuantity.getText().toString());
        } else if (mTxtSubType.getText().toString().trim().equalsIgnoreCase("Weekly schedule")) {
            mIntent.putExtra("subType", "Weekly schedule");
            mIntent.putExtra("sun_quantity", mTxtSundayQ.getText().toString());
            mIntent.putExtra("mon_quantity", mTxtMondayQ.getText().toString());
            mIntent.putExtra("tue_quantity", mTxtTuesdayQ.getText().toString());
            mIntent.putExtra("wed_quantity", mTxtWednesdayQ.getText().toString());
            mIntent.putExtra("thu_quantity", mTxtThursdayQ.getText().toString());
            mIntent.putExtra("fri_quantity", mTxtFridayQ.getText().toString());
            mIntent.putExtra("sat_quantity", mTxtSaturdayQ.getText().toString());
        }
        startActivityForResult(mIntent, 101);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    /*When click on reactive button*/
    private void reactivatingFunctionality() {
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar todayCalendar = Calendar.getInstance();
        final Calendar tomorrowCalendar = Calendar.getInstance();
        final Calendar pauseStartDate1 = Calendar.getInstance();
        final Calendar pauseEndDate1 = Calendar.getInstance();
        final Calendar subscriptionStartDate = Calendar.getInstance();
        final Calendar subscriptionEndDate = Calendar.getInstance();
        try {
            mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            todayCalendar.setTime(mSimpleDateFormat.parse(mSimpleDateFormat.format(Calendar.getInstance().getTime())));
            tomorrowCalendar.setTime(mSimpleDateFormat.parse(mSimpleDateFormat.format(Calendar.getInstance().getTime())));
            pauseStartDate1.setTime(mSimpleDateFormat.parse(pauseStartDate));
            pauseEndDate1.setTime(mSimpleDateFormat.parse(endDate));
            subscriptionStartDate.setTime(mSimpleDateFormat.parse(mTxtStartDate.getText().toString().trim()));
            subscriptionEndDate.setTime(mSimpleDateFormat.parse(mTxtEndDate.getText().toString().trim()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*logic starts here*/
        if (PopUtils.isSevenPmCrossed(this)) {
            tomorrowCalendar.roll(Calendar.DATE, 1);
            if (pauseStartDate1.getTimeInMillis() > tomorrowCalendar.getTimeInMillis()) {
                if (PopUtils.checkInternetConnection(this)) {
                    requestForResumeSubscriptionWS(reactivationDate, pauseEndDate);
                } else {
                    PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                }
            } else {
                String cutOffTime = UserDetails.getInstance(this).getCutOffTime();
                PopUtils.alertDialog(this,
                        "Oops! You have crossed the cut off time (" + cutOffTime + ":00 of " +
                                todayCalendar.get(Calendar.YEAR) + "-" +
                                (todayCalendar.get(Calendar.MONTH) + 1) + "-" +
                                todayCalendar.get(Calendar.DATE) +
                                ") for resuming the subscription from tomorrow. Please try to resume it tomorrow before " + cutOffTime + ":00",
                        null);
            }
        } else {
            if (todayCalendar.getTimeInMillis() < pauseEndDate1.getTimeInMillis()) {
                int year = tomorrowCalendar.get(Calendar.YEAR);
                int month = tomorrowCalendar.get(Calendar.MONTH) + 1;
                int date = tomorrowCalendar.get(Calendar.DATE);
                reactivationDate = year + "-" + month + "-" + date;

                if (PopUtils.checkInternetConnection(this)) {
                    requestForResumeSubscriptionWS(reactivationDate, pauseEndDate);
                } else {
                    PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                }
            } else {
                PopUtils.alertDialog(this, "As this pause request is ending today, it will get activated automatically from tomorrow", null);
            }
        }


      /*  if (todayCalendar.getTimeInMillis() == pauseEndDate1.getTimeInMillis()) {
            PopUtils.alertDialog(this, "Oops! You cannot reactivate this subscription as it is ending today.", null);
        } else if (todayCalendar.getTimeInMillis() > pauseEndDate1.getTimeInMillis()) {
            PopUtils.alertDialog(this, "You can't reactivate this product as subscription date is already completed", null);
        } else if ((todayCalendar.getTimeInMillis() < pauseEndDate1.getTimeInMillis()) &&
                (PopUtils.isSevenPmCrossed())) {
            todayCalendar.roll(Calendar.DATE, 1);

            if (pauseStartDate1.getTimeInMillis() > todayCalendar.getTimeInMillis()) {
                *//*allow*//*
                if (PopUtils.checkInternetConnection(this)) {
                    requestForResumeSubscriptionWS(reactivationDate, pauseEndDate);
                } else {
                    PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                }
            } else {
                *//*dont allow*//*
                PopUtils.alertDialog(this,
                        "Oops! You have crossed the cut off time (7 PM of " + reactivationDate + ") for resuming the subscription from tomorrow." +
                                " Please try to resume it tomorrow before 7 PM", null);
            }
        } else if ((todayCalendar.getTimeInMillis() < pauseEndDate1.getTimeInMillis()) &&
                (!PopUtils.isSevenPmCrossed())) {

            if (pauseStartDate1.getTimeInMillis() < todayCalendar.getTimeInMillis()) {
                try {
                    pauseStartDate1.setTime(mSimpleDateFormat.parse(mSimpleDateFormat.format(Calendar.getInstance().getTime())));
                    pauseStartDate1.roll(Calendar.DATE, 1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (pauseStartDate1.getTimeInMillis() > todayCalendar.getTimeInMillis()) {
                *//*allow*//*
                if (PopUtils.checkInternetConnection(this)) {
                    requestForResumeSubscriptionWS(reactivationDate, pauseEndDate);
                } else {
                    PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                }
            } else {
                *//*dont allow*//*
                PopUtils.alertDialog(this, "before 7pm but conditions not matched", null);
            }
        }*/
    }

    /*When click on pause button*/
    private void pausingFunctionality() {
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final String pickerStartDate, pickerEndDate;
        Calendar startdatecal = Calendar.getInstance();
        Calendar enddatecal = Calendar.getInstance();
        Calendar currentDayCal = Calendar.getInstance();
        currentDayCal.add(Calendar.DATE, +1);

        try {
            startdatecal.setTime(mSimpleDateFormat.parse(startDate));
            enddatecal.setTime(mSimpleDateFormat.parse(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (CalendarDay.from(startdatecal).isAfter(CalendarDay.from(currentDayCal))) {
            pickerStartDate = mSimpleDateFormat.format(startdatecal.getTime());
        } else {
            pickerStartDate = mSimpleDateFormat.format(currentDayCal.getTime());
        }
        pickerEndDate = mSimpleDateFormat.format(enddatecal.getTime());

        PopUtils.alertTwoButtonDialog(this, "Please select the start date and end date during which you do not " +
                "want the delivery", "YES", "NO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUtils.alertDatePicker(ActiveSubscriptionInnerActivity.this, pickerStartDate, pickerEndDate, new DatePickerInterface() {
                    //                PopUtils.alertDatePicker(this, startDate, endDate, new DatePickerInterface() {
                    @Override
                    public void datePickerInterface(String startDate, String endDate) {
                        reactivationDate = startDate;
                        modifyStartDate = startDate;
                        modifyEndDate = endDate;

                        final Calendar todayCalendar = Calendar.getInstance();
                        final Calendar tomorrowCalendar = Calendar.getInstance();
                        final Calendar pauseStartDate = Calendar.getInstance();
                        final Calendar pauseEndDate = Calendar.getInstance();
                        final Calendar subscriptionStartDate = Calendar.getInstance();
                        final Calendar subscriptionEndDate = Calendar.getInstance();
                        final Calendar previousDate = Calendar.getInstance();
                        try {
                            tomorrowCalendar.setTime(todayCalendar.getTime());
                            previousDate.setTime(mSimpleDateFormat.parse(startDate));
                            pauseStartDate.setTime(mSimpleDateFormat.parse(startDate));
                            pauseEndDate.setTime(mSimpleDateFormat.parse(endDate));
                            subscriptionStartDate.setTime(mSimpleDateFormat.parse(mTxtStartDate.getText().toString().trim()));
                            subscriptionEndDate.setTime(mSimpleDateFormat.parse(mTxtEndDate.getText().toString().trim()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        /*logic starts here*/
                        if (PopUtils.isSevenPmCrossed(ActiveSubscriptionInnerActivity.this)) {
                            tomorrowCalendar.roll(Calendar.DATE, 1);
                            if (pauseStartDate.getTimeInMillis() > tomorrowCalendar.getTimeInMillis() &&
                                    pauseStartDate.getTimeInMillis() >= subscriptionStartDate.getTimeInMillis() &&
                                    pauseEndDate.getTimeInMillis() <= subscriptionEndDate.getTimeInMillis()) {
                                /*Condition Apply*/
                                if (PopUtils.checkInternetConnection(ActiveSubscriptionInnerActivity.this)) {
                                    requestForPauseSubscriptionWS(startDate, endDate);
                                } else {
                                    PopUtils.alertDialog(ActiveSubscriptionInnerActivity.this, getString(R.string.pls_check_internet), null);
                                }
                            } else {
                                final Calendar pausePreviousDate = Calendar.getInstance();
                                try {
                                    pausePreviousDate.setTime(mSimpleDateFormat.parse(startDate));
                                    pausePreviousDate.roll(Calendar.DATE, -1);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                String cutOffTime = UserDetails.getInstance(ActiveSubscriptionInnerActivity.this).getCutOffTime();
                                PopUtils.alertDialog(ActiveSubscriptionInnerActivity.this,
                                        "Oops! You have crossed the cut off time (" + cutOffTime + ":00 of " +
                                                pausePreviousDate.get(Calendar.YEAR) + "-" +
                                                (pausePreviousDate.get(Calendar.MONTH) + 1) + "-" +
                                                pausePreviousDate.get(Calendar.DATE) +
                                                ") for pausing a subscription starting from " +
                                                pauseStartDate.get(Calendar.YEAR) + "-" +
                                                (pauseStartDate.get(Calendar.MONTH) + 1) + "-" +
                                                pauseStartDate.get(Calendar.DATE) +
                                                ". Please try for a future date",
                                        null);
                            }
                        } else {
                            if (pauseStartDate.getTimeInMillis() > todayCalendar.getTimeInMillis() &&
                                    pauseStartDate.getTimeInMillis() >= subscriptionStartDate.getTimeInMillis() &&
                                    pauseEndDate.getTimeInMillis() <= subscriptionEndDate.getTimeInMillis()) {
                                /*Condition Apply*/
                                if (PopUtils.checkInternetConnection(ActiveSubscriptionInnerActivity.this)) {
                                    requestForPauseSubscriptionWS(startDate, endDate);
                                } else {
                                    PopUtils.alertDialog(ActiveSubscriptionInnerActivity.this, getString(R.string.pls_check_internet), null);
                                }
                            } else {
                                PopUtils.alertDialog(ActiveSubscriptionInnerActivity.this,
                                        "",
                                        null);
                            }
                        }

                               /* if (todayCalendar.getTimeInMillis() == subscriptionEndDate.getTimeInMillis()) {
                                    PopUtils.alertDialog(this, "Oops! You cannot pause this subscription as it is ending today.", null);
                                } else if (todayCalendar.getTimeInMillis() > subscriptionEndDate.getTimeInMillis()) {
                                    PopUtils.alertDialog(this, "You can't pause this product as subscription date is already completed", null);
                                } else if ((todayCalendar.getTimeInMillis() < subscriptionEndDate.getTimeInMillis()) &&
                                        (PopUtils.isSevenPmCrossed())) {
                                    todayCalendar.roll(Calendar.DATE, 1);

                                    if ((pauseStartDate.getTimeInMillis() > todayCalendar.getTimeInMillis()) &&
                                            (pauseStartDate.getTimeInMillis() >= subscriptionStartDate.getTimeInMillis()) &&
                                            (pauseEndDate.getTimeInMillis() <= subscriptionEndDate.getTimeInMillis())) {
                                *//*Allow and proceed*//*

                                        if (PopUtils.checkInternetConnection(this)) {
                                            requestForPauseSubscriptionWS(startDate, endDate);
                                        } else {
                                            PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                                        }

                                    } else {
                                *//*Dont allow*//*
//                                        previousDate.roll(Calendar.DATE, -1);
                                        int year = previousDate.get(Calendar.YEAR);
                                        int month = previousDate.get(Calendar.MONTH) + 1;
                                        int date = previousDate.get(Calendar.DATE);
                                        PopUtils.alertDialog(this,
                                                "Oops! You have crossed the cut off time (7 PM of " +
                                                        year + "-" +
                                                        month + "-" +
                                                        date + ") for pausing a subscription " +
                                                        "starting from " + startDate + ". Please try for a future date", null);
                                    }
                                } else if ((todayCalendar.getTimeInMillis() < subscriptionEndDate.getTimeInMillis()) &&
                                        (!PopUtils.isSevenPmCrossed())) {
                                    if ((pauseStartDate.getTimeInMillis() > todayCalendar.getTimeInMillis()) &&
                                            (pauseStartDate.getTimeInMillis() >= subscriptionStartDate.getTimeInMillis()) &&
                                            (pauseEndDate.getTimeInMillis() <= subscriptionEndDate.getTimeInMillis())) {
                                *//*Allow and proceed*//*
                                        if (PopUtils.checkInternetConnection(this)) {
                                            requestForPauseSubscriptionWS(startDate, endDate);
                                        } else {
                                            PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                                        }
                                    } else {
                                *//*Dont allow*//*
                                        PopUtils.alertDialog(this,
                                                "before 7pm but conditions not matched", null);
                                    }
                                }*/
                    }
                }, year1, month1, day1);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*final Calendar todayCalendar = Calendar.getInstance();
        final Calendar startDateCalendar = Calendar.getInstance();
        final Calendar endDateCalendar = Calendar.getInstance();
        try {
            todayCalendar.setTime(mSimpleDateFormat.parse(todayDate));
            startDateCalendar.setTime(mSimpleDateFormat.parse(mTxtStartDate.getText().toString()));
            endDateCalendar.setTime(mSimpleDateFormat.parse(mTxtEndDate.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (todayCalendar.getTimeInMillis() == endDateCalendar.getTimeInMillis()) {
            PopUtils.alertDialog(this, "Oops! You cannot pause this subscription as it is ending today.", null);
        } else if (todayCalendar.getTimeInMillis() > endDateCalendar.getTimeInMillis()) {
            PopUtils.alertDialog(this, "You can't pause this product as subscription date is already completed", null);
        } else if (todayCalendar.getTimeInMillis() < endDateCalendar.getTimeInMillis()) {
            PopUtils.alertTwoButtonDialog(this, "Please select the start date and end date during which you do not " +
                            "want the delivery", "YES", "NO",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (todayCalendar.getTimeInMillis() > startDateCalendar.getTimeInMillis()) {
                                todayCalendar.add(Calendar.DAY_OF_YEAR, 1);
                                startDate = todayCalendar.get(Calendar.YEAR) + "-" + (todayCalendar.get(Calendar.MONTH) + 1) + "-" +
                                        todayCalendar.get(Calendar.DATE);
                            } else if (todayCalendar.getTimeInMillis() == startDateCalendar.getTimeInMillis()) {
                                todayCalendar.add(Calendar.DAY_OF_YEAR, 1);
                                startDate = todayCalendar.get(Calendar.YEAR) + "-" + (todayCalendar.get(Calendar.MONTH) + 1) + "-" +
                                        todayCalendar.get(Calendar.DATE);
                            }

                            PopUtils.alertDatePicker(this, startDate, endDate,
                                    new DatePickerInterface() {
                                        @Override
                                        public void datePickerInterface(String startDate, String endDate) {
                                            modifyStartDate = startDate;
                                            modifyEndDate = endDate;

                                            if (!PopUtils.checkDateTimeCondition(modifyStartDate)) {
                                                Calendar cal = Calendar.getInstance();
                                                try {
                                                    cal.setTime(mSimpleDateFormat.parse(modifyStartDate));
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                                cal.add(Calendar.DAY_OF_YEAR, 2);
                                                if (cal.getTimeInMillis() < endDateCalendar.getTimeInMillis()) {
                                                    pausingDate = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + 1 + "-" +
                                                            cal.get(Calendar.DATE);

                                                    if (PopUtils.checkInternetConnection(this)) {
                                                        requestForPauseSubscriptionWS(pausingDate, endDate);
                                                    } else {
                                                        PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                                                    }

                                                } else {
                                                    PopUtils.alertDialog(this,
                                                            "You can't pause this product as you exceeds the date", null);
                                                }
                                            } else {
                                                Calendar cal = Calendar.getInstance();
                                                try {
                                                    cal.setTime(mSimpleDateFormat.parse(modifyStartDate));
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
//                                                            cal.add(Calendar.DAY_OF_YEAR, 1);
//                                                            pausingDate = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" +
//                                                                    cal.get(Calendar.DATE);
                                                pausingDate = modifyStartDate;

                                                if (PopUtils.checkInternetConnection(this)) {
                                                    requestForPauseSubscriptionWS(pausingDate, endDate);
                                                } else {
                                                    PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                                                }
                                            }

                                        }
                                    }, year1, month1, day1);

                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
        }*/
    }

    /*Showing calender*/
    private void showCalender(boolean showMinDate, int year, int month, int day, final TextView textview, final boolean startDate) {
        DatePickerDialog dialog;
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        dialog = new DatePickerDialog(this, R.style.CalenderTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mCurrentYear, int monthOfYear, int dayOfMonth) {
                if (startDate) {
                    year1 = mCurrentYear;
                    month1 = monthOfYear;
                    day1 = dayOfMonth;
                }

                String calenderDay = dayOfMonth + "";
                String calenderMonth = (monthOfYear + 1) + "";
                String calenderYear = mCurrentYear + "";

                if (dayOfMonth < 10) {
                    calenderDay = "0" + calenderDay;
                }

                if (monthOfYear + 1 < 10) {
                    calenderMonth = "0" + calenderMonth;
                }

                textview.setText(calenderDay + "-" + calenderMonth + "-" + calenderYear);
                if (mTxtStartDate.getText().toString().trim().equalsIgnoreCase(mTxtEndDate.getText().toString().trim())) {

                } else {

                }
            }
        }, year, month - 1, day);
        if (showMinDate) {
            dialog.getDatePicker().setMinDate(c.getTimeInMillis());
        } else {
            dialog.getDatePicker().setMaxDate(new Date().getTime());
        }

        dialog.show();
    }

    /*Requesting a service call for reactivate subscription*/
    private void requestForResumeSubscriptionWS(String startDate, String endDate) {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "resume_subscription");
            mJsonObject.put("s_id", sId);
//            mJsonObject.put("start_date", startDate);
//            mJsonObject.put("end_date", endDate);
            mJsonObject.put("pause_start_date", pauseStartDate);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_RESUME_SUBSCRIPTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Requesting a service call for pausing subscription*/
    private void requestForPauseSubscriptionWS(String startDate, String endDate) {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "pass_subscription");
            mJsonObject.put("s_id", sId);
            mJsonObject.put("start_date", startDate);
            mJsonObject.put("end_date", endDate);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_PAUSE_SUBSCRIPTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Requesting a service call for deactivate subscription*/
    private void requestForDeactivateSubscriptionWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "delete_subscription");
            mJsonObject.put("s_id", sId);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_DEACTIVESUBSCRIPTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Called when error occured from service call*/
    @Override
    public void ErrorResponse(String response, int requestCode) {
        hideLoadingDialog();
        PopUtils.alertDialog(this, response.toString(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /*Called when success occured from service call*/
    @Override
    public void SuccessResponse(String response, int requestCode) {
        hideLoadingDialog();
        switch (requestCode) {
            case WsUtils.WS_CODE_DEACTIVESUBSCRIPTION:
                responseForDeactivateSubscription(response);
                break;
            case WsUtils.WS_CODE_RESUME_SUBSCRIPTION:
                responseForResumeSubscription(response);
                break;
            case WsUtils.WS_CODE_PAUSE_SUBSCRIPTION:
                responseForPauseSubscription(response);
                break;
            default:
                break;
        }
    }

    /*Response from pause service call*/
    private void responseForPauseSubscription(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    mTxtPause.setText("Cancel Pause");
                    mTxtModify.setVisibility(View.GONE);
                    mTxtPauseDescription.setVisibility(View.VISIBLE);
                    mTxtPauseDescription.setText("This Subscription is paused from " + modifyStartDate + " to " + modifyEndDate);
                    mTxtDelete.setVisibility(View.GONE);
//                    mTxtPause.setBackgroundResource(R.drawable.corners_app_solid_app);

                    pauseStartDate = modifyStartDate;
                    pauseEndDate = modifyEndDate;

                    PopUtils.alertDialog(this, "This subscription is paused from " + modifyStartDate + " to " + modifyEndDate, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();*/
                        }
                    });

                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Response from reactivate service call*/
    private void responseForResumeSubscription(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    mTxtPause.setText("Pause");
                    mTxtModify.setVisibility(View.VISIBLE);
                    mTxtPauseDescription.setVisibility(View.GONE);
                    mTxtDelete.setVisibility(View.VISIBLE);
//                    mTxtPause.setBackgroundResource(R.drawable.lite_corners_app_solid_app);

//                    PopUtils.alertDialog(this, "Your reactivation request is successful, the delivery will be started from " +
//                            reactivationDate, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                           /* Intent returnIntent = new Intent();
//                            setResult(Activity.RESULT_OK, returnIntent);
//                            finish();*/
//                        }
//                    });
                    PopUtils.alertDialog(this, "Your reactivation request is successful.", null);
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Response from deactivate service call*/
    private void responseForDeactivateSubscription(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
//                    mTxtPause.setText("Active");
                    PopUtils.alertDialog(this, message, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && data != null) {

            mTxtStartDate.setText(data.getStringExtra("startDate"));
            mTxtEndDate.setText(data.getStringExtra("endDate"));
            mTxtSubType.setText(data.getStringExtra("subType"));
            subType = data.getStringExtra("subType");

            if (subType.equalsIgnoreCase("Weekly schedule")) {
                scheduleWeekScheduleSelected();

                mTxtSundayQ.setText(data.getStringExtra("sun_quantity"));
                mTxtMondayQ.setText(data.getStringExtra("mon_quantity"));
                mTxtTuesdayQ.setText(data.getStringExtra("tue_quantity"));
                mTxtWednesdayQ.setText(data.getStringExtra("wed_quantity"));
                mTxtThursdayQ.setText(data.getStringExtra("thu_quantity"));
                mTxtFridayQ.setText(data.getStringExtra("fri_quantity"));
                mTxtSaturdayQ.setText(data.getStringExtra("sat_quantity"));
            } else if (subType.equalsIgnoreCase("Monthly Once")) {
                scheduleMonthlyOnceSelected();

                mTxtDayOne.setText(data.getStringExtra("day_one"));
                mTxtSelectQuantity.setText(data.getStringExtra("quantity"));
            } else if (subType.equalsIgnoreCase("Every Day")) {
                scheduleEveryDaySelected();

                mTxtSelectQuantity.setText(data.getStringExtra("quantity"));
            } else if (subType.equalsIgnoreCase("Monthly Twice")) {
                scheduleMonthlyTwiceSelected();

                mTxtDayOne.setText(data.getStringExtra("day_one") + "");
                mTxtDayTwo.setText(data.getStringExtra("day_two") + "");
                mTxtSelectQuantity.setText(data.getStringExtra("quantity"));
            } else if (subType.equalsIgnoreCase("Alternate Days")) {
                scheduleAlternateDaySelected();

                mTxtSelectQuantity.setText(data.getStringExtra("quantity"));
            }

        }
    }

    /*Called on back button press*/
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
