package com.godavarisandroid.mystore.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.interfaces.DialogListInterface;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.DialogList;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.godavarisandroid.mystore.utils.UserDetails;
import com.godavarisandroid.mystore.views.OpensansTextView;
import com.godavarisandroid.mystore.webUtils.ServerResponse;
import com.godavarisandroid.mystore.webUtils.WebServices;
import com.godavarisandroid.mystore.webUtils.WsUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by UMA on 4/22/2018.
 */
public class SubscriptionActivity extends BaseActivity implements View.OnClickListener, IParseListener {
    private TextView mTxtTitle, mTxtProduct, mTxtSubCategory, mTxtQuantity, mTxtPrice, mTxtActivate, mTxtDescription, mSelectQuantity,
            mTxtSelectQuantity,
            mTxtSave, mTxtEveryDay, mTxtMonthlyOnce, mTxtMonthlyTwice, mTxtAlternateDay, mTxtWeekSchedule, mTxtStartDate,
            mTxtEndDate, mTxtSundayQ, mTxtMondayQ, mTxtTuesdayQ, mTxtWednesdayQ, mTxtThursdayQ, mTxtFridayQ, mTxtSaturdayQ,
            mTxtDayOne, mTxtDayTwo, mTxtDayOneText;
    private ImageView mImgProduct, mImgDelete;
    private ImageView mImgLogo, mImgBack, mImgNotification;
    private View mViewSelectQuantity;
    private LinearLayout mLDays, mLlMonthly, mLlDayOne, mLlDayTwo;
    private boolean isEveryDay = false, isMonthlyOnce = false, isMonthlyTwice = false, isAlternateDay = false, isWeekSchedule = false;
    private Calendar mCalendar;
    private int day1, month1, year1;
    private String id = "", name = "", quantityName = "", description = "", status = "", price = "", startDate = "", endDate = "",
            image = "", cName = "", from = "", subType = "", productName = "", productId = "";
    private JSONObject quantityObject;
    private ArrayList<DialogList> mDialogList = new ArrayList<>();

    private TextView mTxtScheduleStatic;
    private LinearLayout mLlSchedule, mLlDate;
    private int startDateYear, startDateMonth, startDateDate;
    private boolean isGetOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        mCalendar = Calendar.getInstance();
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
        mTxtTitle.setText("Subscription");

        mViewSelectQuantity = findViewById(R.id.viewSelectQuantity);
        mTxtProduct = findViewById(R.id.txtProduct);
        mTxtSubCategory = findViewById(R.id.txtSubCategory);
        mTxtQuantity = findViewById(R.id.txtQuantity);
        mTxtPrice = findViewById(R.id.txtPrice);
        mTxtActivate = findViewById(R.id.txtActivate);
        mTxtDescription = findViewById(R.id.txtDescription);
        mSelectQuantity = findViewById(R.id.selectQuantity);
        mTxtSelectQuantity = findViewById(R.id.txtSelectQuantity);
        mTxtSave = findViewById(R.id.txtSave);
        mTxtEveryDay = findViewById(R.id.txtEveryDay);
        mTxtMonthlyOnce = findViewById(R.id.txtMonthlyOnce);
        mTxtMonthlyTwice = findViewById(R.id.txtMonthlyTwice);
        mTxtAlternateDay = findViewById(R.id.txtAlternateDay);
        mTxtWeekSchedule = findViewById(R.id.txtWeekSchedule);
        mTxtStartDate = findViewById(R.id.txtStartDate);
        mTxtEndDate = findViewById(R.id.txtEndDate);
        mTxtSundayQ = findViewById(R.id.txtSundayQ);
        mTxtMondayQ = findViewById(R.id.txtMondayQ);
        mTxtTuesdayQ = findViewById(R.id.txtTuesdayQ);
        mTxtWednesdayQ = findViewById(R.id.txtWednesdayQ);
        mTxtThursdayQ = findViewById(R.id.txtThursdayQ);
        mTxtFridayQ = findViewById(R.id.txtFridayQ);
        mTxtSaturdayQ = findViewById(R.id.txtSaturdayQ);

        mImgProduct = findViewById(R.id.imgProduct);
        mImgDelete = findViewById(R.id.imgDelete);

        mImgLogo = findViewById(R.id.imgLogo);
        mImgLogo.setVisibility(View.GONE);

        mImgBack = findViewById(R.id.imgBack);
        mImgBack.setVisibility(View.VISIBLE);

        mImgNotification = findViewById(R.id.imgNotification);
        mImgNotification.setVisibility(View.VISIBLE);

        mLDays = findViewById(R.id.llDays);
        mLlMonthly = findViewById(R.id.llMonthly);
        mLlDayOne = findViewById(R.id.llDayOne);
        mLlDayTwo = findViewById(R.id.llDayTwo);
        mTxtDayOne = findViewById(R.id.txtDayOne);
        mTxtDayTwo = findViewById(R.id.txtDayTwo);
        mTxtDayOneText = findViewById(R.id.txtDayOneText);

        mTxtScheduleStatic = findViewById(R.id.txtScheduleStatic);
        mLlSchedule = findViewById(R.id.llSchedule);
        mLlDate = findViewById(R.id.llDate);
    }

    /*getting data through intent*/
    private void getBundleData() {
        Intent intent = getIntent();
        if (intent != null) {

            if (intent.hasExtra("isGetOnce")) {
                isGetOnce = intent.getBooleanExtra("isGetOnce", false);
                if (isGetOnce) {
                    mLlSchedule.setVisibility(View.GONE);
                    ((OpensansTextView) findViewById(R.id.txtStartDateStatic)).setText("Select a date");
                } else {
                    mLlSchedule.setVisibility(View.VISIBLE);
                    ((OpensansTextView) findViewById(R.id.txtStartDateStatic)).setText("Start Date");
                }
            }

            if (intent.hasExtra("from")) {
                from = intent.getStringExtra("from");
                if (from.equalsIgnoreCase("2")) {
                    mTxtTitle.setText("Subscription Modification");
                    mTxtSave.setText("Save");
                } else if (from.equalsIgnoreCase("1")) {
                    scheduleEveryDaySelected();
                    mTxtTitle.setText("Subscription");
                    mTxtSave.setText("Add");
                } else if (from.equalsIgnoreCase("3")) {
                    mTxtTitle.setText("Subscription");
                    mTxtSave.setText("Add");
                    mTxtScheduleStatic.setVisibility(View.GONE);
                    mLlSchedule.setVisibility(View.GONE);
                    mLlDate.setVisibility(View.GONE);
                } else if (from.equalsIgnoreCase("4")) {
                    mTxtTitle.setText("Subscription");
                    mTxtSave.setText("Add");
                    mTxtScheduleStatic.setVisibility(View.GONE);
                    mLlSchedule.setVisibility(View.GONE);
                    mLlDate.setVisibility(View.GONE);
                }
            }

            if (from.equalsIgnoreCase("1") || from.equalsIgnoreCase("3") || from.equalsIgnoreCase("4")) {
                scheduleEveryDaySelected();

                id = intent.getStringExtra("id");

                productName = intent.getStringExtra("name");
                mTxtProduct.setText(productName);

                quantityName = intent.getStringExtra("quantityName");
                mTxtQuantity.setText(quantityName);

                description = intent.getStringExtra("description");
                mTxtDescription.setText(description);

                status = intent.getStringExtra("status");

                price = intent.getStringExtra("price");
                mTxtPrice.setText("₹" + price);

                image = intent.getStringExtra("image");
                Picasso.with(this).load(image).into(mImgProduct);

            } else if (from.equalsIgnoreCase("2")) {
                id = intent.getStringExtra("s_id");
                productId = intent.getStringExtra("product_id");

                startDate = intent.getStringExtra("start_date");
                mTxtStartDate.setText(startDate);

                endDate = intent.getStringExtra("end_date");
                mTxtEndDate.setText(endDate);

                status = intent.getStringExtra("status");

                productName = intent.getStringExtra("product_name");
                mTxtProduct.setText(productName);

                quantityName = intent.getStringExtra("quantity_name");
                mTxtQuantity.setText(quantityName);

                description = intent.getStringExtra("description");
                mTxtDescription.setText(description);

                price = intent.getStringExtra("price");
                mTxtPrice.setText("₹" + price);

                subType = intent.getStringExtra("sub_type");

                image = intent.getStringExtra("image");
                Picasso.with(this).load(image).into(mImgProduct);

                if (subType.equalsIgnoreCase("Weekly schedule")) {
                    scheduleWeekScheduleSelected();

                    mTxtSundayQ.setText(intent.getStringExtra("sun_quantity"));
                    mTxtMondayQ.setText(intent.getStringExtra("mon_quantity"));
                    mTxtTuesdayQ.setText(intent.getStringExtra("tue_quantity"));
                    mTxtWednesdayQ.setText(intent.getStringExtra("wed_quantity"));
                    mTxtThursdayQ.setText(intent.getStringExtra("thu_quantity"));
                    mTxtFridayQ.setText(intent.getStringExtra("fri_quantity"));
                    mTxtSaturdayQ.setText(intent.getStringExtra("sat_quantity"));
                } else if (subType.equalsIgnoreCase("Monthly Once")) {
                    scheduleMonthlyOnceSelected();

                    mTxtDayOne.setText(intent.getStringExtra("day_one"));
                    mTxtSelectQuantity.setText(intent.getStringExtra("quantity"));
                } else if (subType.equalsIgnoreCase("Every Day")) {
                    scheduleEveryDaySelected();

                    mTxtSelectQuantity.setText(intent.getStringExtra("quantity"));
                } else if (subType.equalsIgnoreCase("Monthly Twice")) {
                    scheduleMonthlyTwiceSelected();

                    mTxtDayOne.setText(intent.getStringExtra("day_one") + "");
                    mTxtDayTwo.setText(intent.getStringExtra("day_two") + "");
                    mTxtSelectQuantity.setText(intent.getStringExtra("quantity"));
                } else if (subType.equalsIgnoreCase("Alternate Days")) {
                    scheduleAlternateDaySelected();

                    mTxtSelectQuantity.setText(intent.getStringExtra("quantity"));
                }
            }
            if(!TextUtils.isEmpty(endDate)&&!TextUtils.isEmpty(startDate)){
                if(endDate.equalsIgnoreCase(startDate)){
                    mTxtScheduleStatic.setVisibility(View.GONE);
                    mLlSchedule.setVisibility(View.GONE);
                }
            }
        }
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mTxtActivate.setOnClickListener(this);
        mTxtSave.setOnClickListener(this);
        mImgDelete.setOnClickListener(this);
        mTxtEveryDay.setOnClickListener(this);
        mTxtMonthlyOnce.setOnClickListener(this);
        mTxtMonthlyTwice.setOnClickListener(this);
        mTxtAlternateDay.setOnClickListener(this);
        mTxtWeekSchedule.setOnClickListener(this);
        mImgLogo.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mTxtStartDate.setOnClickListener(this);
        mTxtEndDate.setOnClickListener(this);
        mTxtSelectQuantity.setOnClickListener(this);
        mTxtSundayQ.setOnClickListener(this);
        mTxtMondayQ.setOnClickListener(this);
        mTxtTuesdayQ.setOnClickListener(this);
        mTxtWednesdayQ.setOnClickListener(this);
        mTxtThursdayQ.setOnClickListener(this);
        mTxtFridayQ.setOnClickListener(this);
        mTxtSaturdayQ.setOnClickListener(this);
        mTxtDayOne.setOnClickListener(this);
        mTxtDayTwo.setOnClickListener(this);
        mImgNotification.setOnClickListener(this);
    }

    private SimpleDateFormat mSimpleDateFormat;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtActivate:
                /*Show popup when click on re active button*/
                PopUtils.alertActivateSubscription(this, "You have subscribed for Tropicana juice of 1 litre.", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                break;
            case R.id.txtSave:
                /*checking for 7pm logic*/
                mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                final String todayDate = mSimpleDateFormat.format(Calendar.getInstance().getTime());
                final Calendar todayCalendar = Calendar.getInstance();
                final Calendar startDateCalendar = Calendar.getInstance();
                try {
                    todayCalendar.setTime(mSimpleDateFormat.parse(todayDate));
                    startDateCalendar.setTime(mSimpleDateFormat.parse(mTxtStartDate.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (PopUtils.isSevenPmCrossed(this)) {
                    todayCalendar.roll(Calendar.DATE, 1);
                    if (startDateCalendar.getTimeInMillis() > todayCalendar.getTimeInMillis()) {
                        /*Allow and Proceed*/
                        proceedForSubscription();
                    } else {
                        /*Dont Allow*/
                        PopUtils.alertDialog(this, "Oops!! You have crossed the cut off time for raising a request for " + mTxtStartDate.getText().toString() + ". Please try for a future date", null);
                    }
                } else {
                    if (startDateCalendar.getTimeInMillis() > todayCalendar.getTimeInMillis()) {
                        /*Allow and Proceed*/
                        proceedForSubscription();
                    } else {
                        /*Dont Allow*/
                        String cutOffTime = UserDetails.getInstance(this).getCutOffTime();
                        PopUtils.alertDialog(this, "Oops! You have crossed the cut off time (" + cutOffTime + ":00 of " +
                                todayDate + ") for requesting a new" +
                                " subscription starting from " + mTxtStartDate.getText().toString() +
                                ". Please try for a future date", null);
                    }
                }


//                if (!PopUtils.checkDateTimeCondition(mTxtStartDate.getText().toString().trim())) {
////                    PopUtils.alertDialog(this, "Oops!! Your request for tomorrow as start date cannot be accepted as cut off time for tomorrow (which is 7PM of current date) is already crossed. Please try start date with a future date.", null);
//                } else {
//                }
                break;
            case R.id.imgDelete:
                break;
            case R.id.txtEveryDay:
//                mTxtStartDate.setText("");
                mTxtEndDate.setText("");
//                mTxtDayOne.setText("");
//                mTxtDayTwo.setText("");

                scheduleEveryDaySelected();
                break;
            case R.id.txtMonthlyOnce:
//                mTxtStartDate.setText("");
                mTxtEndDate.setText("");
//                mTxtDayOne.setText("");
//                mTxtDayTwo.setText("");

                scheduleMonthlyOnceSelected();
                break;
            case R.id.txtMonthlyTwice:
//                mTxtStartDate.setText("");
                mTxtEndDate.setText("");
//                mTxtDayOne.setText("");
//                mTxtDayTwo.setText("");

                scheduleMonthlyTwiceSelected();
                break;
            case R.id.txtAlternateDay:
//                mTxtStartDate.setText("");
                mTxtEndDate.setText("");
//                mTxtDayOne.setText("");
//                mTxtDayTwo.setText("");

                scheduleAlternateDaySelected();
                break;
            case R.id.txtWeekSchedule:
//                mTxtStartDate.setText("");
                mTxtEndDate.setText("");
//                mTxtDayOne.setText("");
//                mTxtDayTwo.setText("");

                mTxtSundayQ.setText("0");
                mTxtMondayQ.setText("0");
                mTxtTuesdayQ.setText("0");
                mTxtWednesdayQ.setText("0");
                mTxtThursdayQ.setText("0");
                mTxtFridayQ.setText("0");
                mTxtSaturdayQ.setText("0");

                scheduleWeekScheduleSelected();
                break;
            case R.id.imgBack:
//                navigateActivity(new Intent(this, ActiveSubscriptionInnerActivity.class), true);
                onBackPressed();
                break;
            case R.id.txtStartDate:
                year1 = mCalendar.get(Calendar.YEAR);
                month1 = mCalendar.get(Calendar.MONTH);
                day1 = mCalendar.get(Calendar.DAY_OF_MONTH);

                if (TextUtils.isEmpty(mTxtStartDate.getText().toString())) {
                    startDateYear = mCalendar.get(Calendar.YEAR);
                    startDateMonth = mCalendar.get(Calendar.MONTH);
                    startDateDate = mCalendar.get(Calendar.DAY_OF_MONTH) + 1;
                }

                showCalender(true, year1, month1, day1 + 1, mTxtStartDate, true, true);
                break;
            case R.id.txtEndDate:
                if (TextUtils.isEmpty(mTxtStartDate.getText().toString().trim())) {
                    PopUtils.alertDialog(this, "Please select start date first", null);
                } else {
                    if (isMonthlyOnce) {
                        showCalender(true, year1, month1 + 1, day1, mTxtEndDate, false, false);
                    } else if (isMonthlyTwice) {
                        showCalender(true, year1, month1 + 1, day1, mTxtEndDate, false, false);
                    } else if (isWeekSchedule) {
                        showCalender(true, year1, month1, day1 + 7, mTxtEndDate, false, false);
                    } else if (isAlternateDay) {
                        showCalender(true, year1, month1, day1 + 2, mTxtEndDate, false, false);
                    } else if (isEveryDay) {
                        showCalender(true, year1, month1, day1 + 1, mTxtEndDate, false, false);
                    }
                }
                break;
            case R.id.txtSelectQuantity:
                mDialogList.clear();
                for (int i = 0; i < 99; i++) {
                    mDialogList.add(new DialogList("", i + 1 + ""));
                }
                /*Show popup dialog for quantity*/
                PopUtils.alertDialogList(this, mDialogList, new DialogListInterface() {
                    @Override
                    public void DialogListInterface(String id, String value) {
                        mTxtSelectQuantity.setText(value);
                    }
                }, "left");
                break;
            case R.id.txtSundayQ:
                mDialogList.clear();
                for (int i = 0; i < 100; i++) {
                    mDialogList.add(new DialogList("", i + ""));
                }

                /*Show popup dialog for sunday quantities*/
                PopUtils.alertDialogList(this, mDialogList, new DialogListInterface() {
                    @Override
                    public void DialogListInterface(String id, String value) {
                        mTxtSundayQ.setText(value);
                    }
                }, "left");
                break;
            case R.id.txtMondayQ:
                mDialogList.clear();
                for (int i = 0; i < 100; i++) {
                    mDialogList.add(new DialogList("", i + ""));
                }

                /*Show popup dialog for monday quantities*/
                PopUtils.alertDialogList(this, mDialogList, new DialogListInterface() {
                    @Override
                    public void DialogListInterface(String id, String value) {
                        mTxtMondayQ.setText(value);
                    }
                }, "left");
                break;
            case R.id.txtTuesdayQ:
                mDialogList.clear();
                for (int i = 0; i < 100; i++) {
                    mDialogList.add(new DialogList("", i + ""));
                }

                /*Show popup dialog for tuesday quantities*/
                PopUtils.alertDialogList(this, mDialogList, new DialogListInterface() {
                    @Override
                    public void DialogListInterface(String id, String value) {
                        mTxtTuesdayQ.setText(value);
                    }
                }, "left");
                break;
            case R.id.txtWednesdayQ:
                mDialogList.clear();
                for (int i = 0; i < 100; i++) {
                    mDialogList.add(new DialogList("", i + ""));
                }

                /*Show popup dialog for wednesday quantities*/
                PopUtils.alertDialogList(this, mDialogList, new DialogListInterface() {
                    @Override
                    public void DialogListInterface(String id, String value) {
                        mTxtWednesdayQ.setText(value);
                    }
                }, "left");
                break;
            case R.id.txtThursdayQ:
                mDialogList.clear();
                for (int i = 0; i < 100; i++) {
                    mDialogList.add(new DialogList("", i + ""));
                }

                /*Show popup dialog for thursday quantities*/
                PopUtils.alertDialogList(this, mDialogList, new DialogListInterface() {
                    @Override
                    public void DialogListInterface(String id, String value) {
                        mTxtThursdayQ.setText(value);
                    }
                }, "left");
                break;
            case R.id.txtFridayQ:
                mDialogList.clear();
                for (int i = 0; i < 100; i++) {
                    mDialogList.add(new DialogList("", i + ""));
                }

                /*Show popup dialog for friday quantities*/
                PopUtils.alertDialogList(this, mDialogList, new DialogListInterface() {
                    @Override
                    public void DialogListInterface(String id, String value) {
                        mTxtFridayQ.setText(value);
                    }
                }, "left");
                break;
            case R.id.txtSaturdayQ:
                mDialogList.clear();
                for (int i = 0; i < 100; i++) {
                    mDialogList.add(new DialogList("", i + ""));
                }

                /*Show popup dialog for saturday quantities*/
                PopUtils.alertDialogList(this, mDialogList, new DialogListInterface() {
                    @Override
                    public void DialogListInterface(String id, String value) {
                        mTxtSaturdayQ.setText(value);
                    }
                }, "left");
                break;
            case R.id.txtDayOne:
                mDialogList.clear();
                for (int i = 0; i < 28; i++) {
                    if (i + 1 == 1) {
                        mDialogList.add(new DialogList("", i + 1 + " st of every month"));
                    } else if (i + 1 == 2) {
                        mDialogList.add(new DialogList("", i + 1 + " nd of every month"));
                    } else if (i + 1 == 3) {
                        mDialogList.add(new DialogList("", i + 1 + " rd of every month"));
                    } else {
                        mDialogList.add(new DialogList("", i + 1 + " th of every month"));
                    }
                }

                /*Show popup dialog for day one quantities*/
                PopUtils.alertDialogList(this, mDialogList, new DialogListInterface() {
                    @Override
                    public void DialogListInterface(String id, String value) {
                        mTxtDayOne.setText(value);
                        mTxtDayTwo.setText("");
                    }
                }, "left");
                break;
            case R.id.txtDayTwo:
                if (TextUtils.isEmpty(mTxtDayOne.getText().toString().trim())) {
                    PopUtils.alertDialog(this, "Please select day one date first", null);
                } else {
                    int firstDate = Integer.parseInt(mTxtDayOne.getText().toString().trim());
                    mDialogList.clear();
                    for (int i = firstDate; i < 28; i++) {
                        if (i + 1 == 1) {
                            mDialogList.add(new DialogList("", i + 1 + " st of every month"));
                        } else if (i + 1 == 2) {
                            mDialogList.add(new DialogList("", i + 1 + " nd of every month"));
                        } else if (i + 1 == 3) {
                            mDialogList.add(new DialogList("", i + 1 + " rd of every month"));
                        } else {
                            mDialogList.add(new DialogList("", i + 1 + " th of every month"));
                        }
                    }

                    /*Show popup dialog for day two quantities*/
                    PopUtils.alertDialogList(this, mDialogList, new DialogListInterface() {
                        @Override
                        public void DialogListInterface(String id, String value) {
                            mTxtDayTwo.setText(value);
                        }
                    }, "left");
                }
                break;
            case R.id.imgNotification:
                navigateActivity(new Intent(this, HelpActivity.class), false);
                break;
            default:
                break;
        }
    }

    private void proceedForSubscription() {
        if (!checkValidation().equalsIgnoreCase("")) {
            PopUtils.alertDialog(this, checkValidation(), null);
        } else if (UserDetails.getInstance(this).getCity().equalsIgnoreCase("") ||
                UserDetails.getInstance(this).getArea().equalsIgnoreCase("") ||
                UserDetails.getInstance(this).getApartment().equalsIgnoreCase("") ||
                UserDetails.getInstance(this).getFlat().equalsIgnoreCase("") ||
                UserDetails.getInstance(this).getStreet().equalsIgnoreCase("")) {
            PopUtils.alertDialog(this, "There is no address configured for your profile. Please configure the address where you need this product.", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigateActivity(new Intent(SubscriptionActivity.this, EditAddressActivity.class), false);
                }
            });
        } else {
            if (from.equalsIgnoreCase("2")) {
                if (PopUtils.checkInternetConnection(this)) {
                    requestForModifySubscriptionWS();
                } else {
                    PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                }
            } else {
                if (PopUtils.checkInternetConnection(this)) {
                    requestForSubscriptionWS();
                } else {
                    PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                }
            }
        }
    }

    /*Called when clik on button*/
    private String checkValidation() {
        String message = "";
        if (!from.equalsIgnoreCase("3") && !from.equalsIgnoreCase("4") && TextUtils.isEmpty(mTxtStartDate.getText().toString().trim())) {
            message = "Please select start date";
//        } else if (!from.equalsIgnoreCase("3") && !from.equalsIgnoreCase("4") && TextUtils.isEmpty(mTxtEndDate.getText().toString().trim())) {
//            message = "Please select end date";
        } else if (!from.equalsIgnoreCase("3") && !from.equalsIgnoreCase("4") && isMonthlyOnce && TextUtils.isEmpty(mTxtDayOne.getText().toString().trim())) {
            message = "Please select a month date";
        } else if (!from.equalsIgnoreCase("3") && !from.equalsIgnoreCase("4") && isMonthlyTwice && TextUtils.isEmpty(mTxtDayOne.getText().toString().trim())) {
            message = "Please select a day one date";
        } else if (!from.equalsIgnoreCase("3") && !from.equalsIgnoreCase("4") && isMonthlyTwice && TextUtils.isEmpty(mTxtDayTwo.getText().toString().trim())) {
            message = "Please select a day two date";
        } else if (!from.equalsIgnoreCase("3") && !from.equalsIgnoreCase("4") && isWeekSchedule && (mTxtSundayQ.getText().toString().trim().equalsIgnoreCase("0") &&
                mTxtMondayQ.getText().toString().trim().equalsIgnoreCase("0") &&
                mTxtTuesdayQ.getText().toString().trim().equalsIgnoreCase("0") &&
                mTxtWednesdayQ.getText().toString().trim().equalsIgnoreCase("0") &&
                mTxtThursdayQ.getText().toString().trim().equalsIgnoreCase("0") &&
                mTxtFridayQ.getText().toString().trim().equalsIgnoreCase("0") &&
                mTxtSaturdayQ.getText().toString().trim().equalsIgnoreCase("0"))) {
            message = "Please select the product quantity for atleast one of the day";
        }
        return message;
    }

    Calendar endDateCal;

    /*Called when showing calender*/
    private void showCalender(boolean showMinDate, int year, int month, int day, final TextView textview, final boolean startDate, final boolean updateDate) {
        DatePickerDialog dialog;
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        dialog = new DatePickerDialog(this, R.style.CalenderTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mCurrentYear, int monthOfYear, int dayOfMonth) {
                if (startDate) {
                    startDateYear = mCurrentYear;
                    startDateMonth = monthOfYear;
                    startDateDate = dayOfMonth;

                    year1 = mCurrentYear;
                    month1 = monthOfYear;
                    day1 = dayOfMonth;

                    mTxtEndDate.setText("");
                    endDateCal = Calendar.getInstance();
                    endDateCal.set(mCurrentYear, monthOfYear, dayOfMonth);
                    endDateCal.add(Calendar.MONTH, +1);
                    endDateCal.add(Calendar.MONTH, +1);
                    endDateCal.add(Calendar.MONTH, +1);
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

                textview.setText(calenderYear + "-" + calenderMonth + "-" + calenderDay);
            }
        }, year, month - 1, day);
        if (showMinDate) {
            dialog.getDatePicker().setMinDate(c.getTimeInMillis());
        } else {
            dialog.getDatePicker().setMaxDate(new Date().getTime());
        }

        if (updateDate) {
            dialog.updateDate(startDateYear, startDateMonth, startDateDate);
        }

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        dialog.show();
    }

    /*Called when every day schedule selected*/
    public void scheduleEveryDaySelected() {
        if (isEveryDay) {
        } else {
            mTxtEveryDay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_select, 0, 0, 0);
            mTxtMonthlyOnce.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtMonthlyTwice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtAlternateDay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtWeekSchedule.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
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

    /*Called when monthly once schedule selected*/
    public void scheduleMonthlyOnceSelected() {
        if (isMonthlyOnce) {
        } else {
            mTxtEveryDay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtMonthlyOnce.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_select, 0, 0, 0);
            mTxtMonthlyTwice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtAlternateDay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtWeekSchedule.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mLDays.setVisibility(View.GONE);
            mLlMonthly.setVisibility(View.VISIBLE);
            mLlDayTwo.setVisibility(View.GONE);
            mTxtDayOneText.setText("Select a Date");
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

    /*Called when monthly twice schedule selected*/
    public void scheduleMonthlyTwiceSelected() {
        if (isMonthlyTwice) {
        } else {
            mTxtEveryDay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtMonthlyOnce.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtMonthlyTwice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_select, 0, 0, 0);
            mTxtAlternateDay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtWeekSchedule.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mLDays.setVisibility(View.GONE);
            mLlMonthly.setVisibility(View.VISIBLE);
            mLlDayTwo.setVisibility(View.VISIBLE);
            mTxtDayOneText.setText("Day One");
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

    /*Called when alternate day schedule selected*/
    public void scheduleAlternateDaySelected() {
        if (isAlternateDay) {
        } else {
            mTxtEveryDay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtMonthlyOnce.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtMonthlyTwice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtAlternateDay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_select, 0, 0, 0);
            mTxtWeekSchedule.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
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

    /*Called when week schedule selected*/
    public void scheduleWeekScheduleSelected() {
        if (isWeekSchedule) {
        } else {
            mTxtEveryDay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtMonthlyOnce.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtMonthlyTwice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtAlternateDay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_unselect, 0, 0, 0);
            mTxtWeekSchedule.setCompoundDrawablesWithIntrinsicBounds(R.drawable.img_checkbox_select, 0, 0, 0);
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

    private String getEndDate() {
        if (endDateCal == null) {
            String[] times = mTxtStartDate.getText().toString().trim().split("-");
            endDateCal = Calendar.getInstance();
            endDateCal.set(Integer.parseInt(times[0]), Integer.parseInt(times[1]), Integer.parseInt(times[2]));
            endDateCal.add(Calendar.MONTH, +1);
            endDateCal.add(Calendar.MONTH, +1);
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd").format(endDateCal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /*Called when requesting for modify subscription service call*/
    private void requestForModifySubscriptionWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "modify_subscription");
            mJsonObject.put("s_id", id);
            mJsonObject.put("start_date", mTxtStartDate.getText().toString().trim());
            mJsonObject.put("end_date", getEndDate());

            if (isEveryDay) {
                mJsonObject.put("sub_type", "E");
                mJsonObject.put("quantity", mTxtSelectQuantity.getText().toString());
            } else if (isMonthlyOnce) {
                mJsonObject.put("sub_type", "M1");
                mJsonObject.put("date_of_mon", mTxtDayOne.getText().toString().trim());
                mJsonObject.put("quantity", mTxtSelectQuantity.getText().toString());
            } else if (isMonthlyTwice) {
                mJsonObject.put("sub_type", "M2");
                mJsonObject.put("date_one_of_mon", mTxtDayOne.getText().toString().trim());
                mJsonObject.put("date_two_of_mon", mTxtDayTwo.getText().toString().trim());
                mJsonObject.put("quantity", mTxtSelectQuantity.getText().toString());
            } else if (isAlternateDay) {
                mJsonObject.put("sub_type", "A");
                mJsonObject.put("quantity", mTxtSelectQuantity.getText().toString());
            } else if (isWeekSchedule) {
                mJsonObject.put("sub_type", "W");

                JSONArray mWeekArray = new JSONArray();
                JSONArray mQuantityArray = new JSONArray();
                if (!mTxtSundayQ.getText().toString().equalsIgnoreCase("0")) {
                    mWeekArray.put("Sun");
                    mQuantityArray.put(mTxtSundayQ.getText().toString().trim());
                }
                if (!mTxtMondayQ.getText().toString().equalsIgnoreCase("0")) {
                    mWeekArray.put("Mon");
                    mQuantityArray.put(mTxtMondayQ.getText().toString().trim());
                }
                if (!mTxtTuesdayQ.getText().toString().equalsIgnoreCase("0")) {
                    mWeekArray.put("Tue");
                    mQuantityArray.put(mTxtTuesdayQ.getText().toString().trim());
                }
                if (!mTxtWednesdayQ.getText().toString().equalsIgnoreCase("0")) {
                    mWeekArray.put("Wed");
                    mQuantityArray.put(mTxtWednesdayQ.getText().toString().trim());
                }
                if (!mTxtThursdayQ.getText().toString().equalsIgnoreCase("0")) {
                    mWeekArray.put("Thu");
                    mQuantityArray.put(mTxtThursdayQ.getText().toString().trim());
                }
                if (!mTxtFridayQ.getText().toString().equalsIgnoreCase("0")) {
                    mWeekArray.put("Fri");
                    mQuantityArray.put(mTxtFridayQ.getText().toString().trim());
                }
                if (!mTxtSaturdayQ.getText().toString().equalsIgnoreCase("0")) {
                    mWeekArray.put("Sat");
                    mQuantityArray.put(mTxtSaturdayQ.getText().toString().trim());
                }

                mJsonObject.put("week_day", mWeekArray);
                mJsonObject.put("quantity", mQuantityArray);
            }

            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_MODIFY_SUBSCRIPTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Called when requesting for subscription service call*/
    private void requestForSubscriptionWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "subscription");
            mJsonObject.put("product_id", id);
            if (from.equalsIgnoreCase("3") || from.equalsIgnoreCase("4")) {
                mJsonObject.put("start_date", BaseApplication.nextDeliveryDate);
                mJsonObject.put("end_date", BaseApplication.nextDeliveryDate);
            } else {
                mJsonObject.put("start_date", mTxtStartDate.getText().toString().trim());
                mJsonObject.put("end_date", isGetOnce ? mTxtStartDate.getText().toString().trim() : getEndDate());
            }

            if (isEveryDay) {
                mJsonObject.put("sub_type", "E");
                mJsonObject.put("quantity", mTxtSelectQuantity.getText().toString());
            } else if (isMonthlyOnce) {
                mJsonObject.put("sub_type", "M1");
                mJsonObject.put("date_of_mon", mTxtDayOne.getText().toString().trim());
                mJsonObject.put("quantity", mTxtSelectQuantity.getText().toString());
            } else if (isMonthlyTwice) {
                mJsonObject.put("sub_type", "M2");
                mJsonObject.put("date_one_of_mon", mTxtDayOne.getText().toString().trim());
                mJsonObject.put("date_two_of_mon", mTxtDayTwo.getText().toString().trim());
                mJsonObject.put("quantity", mTxtSelectQuantity.getText().toString());
            } else if (isAlternateDay) {
                mJsonObject.put("sub_type", "A");
                mJsonObject.put("quantity", mTxtSelectQuantity.getText().toString());
            } else if (isWeekSchedule) {
                mJsonObject.put("sub_type", "W");

                JSONArray mWeekArray = new JSONArray();
                JSONArray mQuantityArray = new JSONArray();
                if (!mTxtSundayQ.getText().toString().equalsIgnoreCase("0")) {
                    mWeekArray.put("Sun");
                    mQuantityArray.put(mTxtSundayQ.getText().toString().trim());
                }
                if (!mTxtMondayQ.getText().toString().equalsIgnoreCase("0")) {
                    mWeekArray.put("Mon");
                    mQuantityArray.put(mTxtMondayQ.getText().toString().trim());
                }
                if (!mTxtTuesdayQ.getText().toString().equalsIgnoreCase("0")) {
                    mWeekArray.put("Tue");
                    mQuantityArray.put(mTxtTuesdayQ.getText().toString().trim());
                }
                if (!mTxtWednesdayQ.getText().toString().equalsIgnoreCase("0")) {
                    mWeekArray.put("Wed");
                    mQuantityArray.put(mTxtWednesdayQ.getText().toString().trim());
                }
                if (!mTxtThursdayQ.getText().toString().equalsIgnoreCase("0")) {
                    mWeekArray.put("Thu");
                    mQuantityArray.put(mTxtThursdayQ.getText().toString().trim());
                }
                if (!mTxtFridayQ.getText().toString().equalsIgnoreCase("0")) {
                    mWeekArray.put("Fri");
                    mQuantityArray.put(mTxtFridayQ.getText().toString().trim());
                }
                if (!mTxtSaturdayQ.getText().toString().equalsIgnoreCase("0")) {
                    mWeekArray.put("Sat");
                    mQuantityArray.put(mTxtSaturdayQ.getText().toString().trim());
                }

//                String weekDay = mWeekArray.toString().replace("\\", "");
//                String quantity = mQuantityArray.toString().replace("\\", "");
                mJsonObject.put("week_day", mWeekArray);
                mJsonObject.put("quantity", mQuantityArray);

            }
            mJsonObject.put("sub_day_type", isGetOnce ? "ONE" : "NORMAL");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_SUBSCRIPTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Called on back button press*/
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
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
            case WsUtils.WS_CODE_SUBSCRIPTION:
                responseForSubscription(response);
                break;
            case WsUtils.WS_CODE_MODIFY_SUBSCRIPTION:
                responseForModifySubscription(response);
                break;
            default:
                break;
        }
    }

    /*Called when response came for modify subscription*/
    private void responseForModifySubscription(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {

                    PopUtils.alertDialog(this, "Your Subscription request is successful. You will shortly receive an email with details of your subscription", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (from.equalsIgnoreCase("2")) {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("startDate", mTxtStartDate.getText().toString());
                                resultIntent.putExtra("endDate", getEndDate());
                                if (isEveryDay) {
                                    resultIntent.putExtra("subType", "Every Day");
                                    resultIntent.putExtra("quantity", mTxtSelectQuantity.getText().toString());
                                } else if (isMonthlyOnce) {
                                    resultIntent.putExtra("subType", "Monthly Once");
                                    resultIntent.putExtra("day_one", mTxtDayOne.getText().toString().trim());
                                    resultIntent.putExtra("quantity", mTxtSelectQuantity.getText().toString());
                                } else if (isMonthlyTwice) {
                                    resultIntent.putExtra("subType", "Monthly Twice");
                                    resultIntent.putExtra("day_one", mTxtDayOne.getText().toString().trim());
                                    resultIntent.putExtra("day_two", mTxtDayTwo.getText().toString().trim());
                                    resultIntent.putExtra("quantity", mTxtSelectQuantity.getText().toString());
                                } else if (isAlternateDay) {
                                    resultIntent.putExtra("subType", "Alternate Days");
                                    resultIntent.putExtra("quantity", mTxtSelectQuantity.getText().toString());
                                } else if (isWeekSchedule) {
                                    resultIntent.putExtra("subType", "Weekly schedule");
                                    resultIntent.putExtra("sun_quantity", mTxtSundayQ.getText().toString());
                                    resultIntent.putExtra("mon_quantity", mTxtMondayQ.getText().toString());
                                    resultIntent.putExtra("tue_quantity", mTxtTuesdayQ.getText().toString());
                                    resultIntent.putExtra("wed_quantity", mTxtWednesdayQ.getText().toString());
                                    resultIntent.putExtra("thu_quantity", mTxtThursdayQ.getText().toString());
                                    resultIntent.putExtra("fri_quantity", mTxtFridayQ.getText().toString());
                                    resultIntent.putExtra("sat_quantity", mTxtSaturdayQ.getText().toString());
                                }
                                setResult(101, resultIntent);
                                finish();
                            } else {
                                finish();
                                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                            }
                        }
                    });

                } else {
                    PopUtils.alertDialog(this, message, null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Called when respons ecame for subscription*/
    private void responseForSubscription(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {

                    PopUtils.alertDialog(this, "Your Subscription request is successful. You will shortly receive an email with details of your subscription", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (from.equalsIgnoreCase("2")) {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("startDate", mTxtStartDate.getText().toString());
                                resultIntent.putExtra("endDate", getEndDate());
                                resultIntent.putExtra("quantity", mTxtQuantity.getText().toString());
                                setResult(101, resultIntent);
                                finish();
                            } else if (from.equalsIgnoreCase("3")) {
                                Intent intent = new Intent(SubscriptionActivity.this, HomeActivity.class);
                                intent.putExtra("TAB", from);
                                startActivity(intent);
                                finish();
                            } else if (from.equalsIgnoreCase("4")) {
                                Intent intent = new Intent(SubscriptionActivity.this, HomeActivity.class);
                                intent.putExtra("TAB", from);
                                intent.putExtra("selectedDate", BaseApplication.nextDeliveryDate);
                                startActivity(intent);
                                finish();

//                                finish();
//                                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                            } else {
                                finish();
                                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                            }
                        }
                    });

                } else {
                    PopUtils.alertDialog(this, message, null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
