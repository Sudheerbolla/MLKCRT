package com.godavarisandroid.mystore.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.adapters.AlertAddProductsAdapter;
import com.godavarisandroid.mystore.adapters.CategoriesAdapter;
import com.godavarisandroid.mystore.adapters.DialogListAdapter;
import com.godavarisandroid.mystore.fragments.HomeFragment;
import com.godavarisandroid.mystore.interfaces.DatePickerInterface;
import com.godavarisandroid.mystore.interfaces.DialogListInterface;
import com.godavarisandroid.mystore.interfaces.VerifyOtp;
import com.godavarisandroid.mystore.interfaces.WrongNumberClicked;
import com.godavarisandroid.mystore.models.AlertAddProducts;
import com.godavarisandroid.mystore.models.Categories;
import com.godavarisandroid.mystore.models.DialogList;
import com.msg91.sendotp.library.Verification;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Excentd11 on 11/18/2016.
 */

public class PopUtils {
    private static ProgressDialog progressDialog;
    private static WebView mWebView;

    private static String otp = "";

    public static final String merchantKeyStaging = "MrY2LcMZ3MgiJ_kF";
    public static final String midStaging = "DuckHa49923302193796";
    public static final String appNameStaging = "APPSTAGING";

    public static final String channelIdStagingMobile = "WAP";
    public static final String channelIdStagingWeb = "WEB";

    public static final String channelNameStagingMobile = "APPSTAGING";
    public static final String channelNameStagingWeb = "WEBSTAGING";

    public static final String userPrefixStaging = "MILKCARTCUST0";

    public static final String CALL_BACK_URL_STAGING = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";
    public static final String CALL_BACK_URL_PRODUCTION = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=";

    public static final String CALL_BACK_URL = CALL_BACK_URL_STAGING;
    public static final String CHANNEL_ID = channelIdStagingMobile;
    public static final String CHANNEL_NAME = channelNameStagingMobile;

/*

test wallet details:
mob:7777777777
pw:Paytm12345
otp:489871


Merchant Panel Login

Link

Password

Username

URL

Sent to email

6302971295
*/

    public static void alertDialog(final Context mContext, String message, final View.OnClickListener okClick) {
        TextView mTxtOk, mTxtMessage;
        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(mContext).inflate(R.layout.alert_dialog, null);
        mTxtOk = v.findViewById(R.id.txtOk);
        mTxtMessage = v.findViewById(R.id.txtMessage);

        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mTxtMessage.setText(message);
        mTxtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (okClick != null) {
                    okClick.onClick(v);
                }
            }
        });

        dialog.setContentView(v);
        dialog.setCancelable(false);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.30);
        dialog.getWindow().setLayout(width, lp.height);

        dialog.show();
    }

    public static void alertDialogList(final Context mContext, final ArrayList<DialogList> mDialogList,
                                       final DialogListInterface mDialogListInterface, String textType) {
        ListView mListView;
        final DialogListAdapter adapter;
        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_list, null);
        mListView = v.findViewById(R.id.listView);

        adapter = new DialogListAdapter(mContext, mDialogList, textType);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mDialogListInterface.DialogListInterface(mDialogList.get(position).id, mDialogList.get(position).value);
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.setContentView(v);
        dialog.setCancelable(true);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.30);
        dialog.getWindow().setLayout(width, lp.height);

        dialog.show();
    }

    public static void alertAddProductCategory(final Context mContext, ArrayList<Categories> mCategories) {
        RecyclerView mRecyclerView;
        final DialogListAdapter adapter;
        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(mContext).inflate(R.layout.alert_add_product_category, null);
        mRecyclerView = v.findViewById(R.id.recyclerView);

       /* LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext);
        AddProductCategoriesAdapter mAddProductCategoriesAdapter = new AddProductCategoriesAdapter(mContext, mCategories,
                new HomeFragment(), dialog);
        mRecyclerView.setAdapter(mAddProductCategoriesAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new AddProductCategoriesAdapter(mContext, new AddProductCategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));*/

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext);
        CategoriesAdapter mCategoriesAdapter = new CategoriesAdapter(mContext, mCategories, new HomeFragment(), "2");
        mRecyclerView.setAdapter(mCategoriesAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new CategoriesAdapter(mContext, new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));


        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.setContentView(v);
        dialog.setCancelable(true);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 1.0);
        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.30);
        dialog.getWindow().setLayout(width, lp.height);

        dialog.show();
    }

    public static void alertAddProducts(final Context mContext, Dialog firstDialog) {
        RecyclerView mRecyclerView;
        final DialogListAdapter adapter;
        final Dialog secondDialog = new Dialog(mContext, R.style.AlertDialogCustom);
        secondDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(mContext).inflate(R.layout.alert_add_product, null);
        mRecyclerView = v.findViewById(R.id.recyclerView);

        /*set adapter for adding product*/
        ArrayList<AlertAddProducts> mAlertAddProducts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mAlertAddProducts.add(new AlertAddProducts("", ""));
        }
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext);
        AlertAddProductsAdapter mAlertAddProductsAdapter = new AlertAddProductsAdapter(mContext, mAlertAddProducts, firstDialog, secondDialog);
        mRecyclerView.setAdapter(mAlertAddProductsAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new AlertAddProductsAdapter(mContext, new AlertAddProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));

        secondDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = secondDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        secondDialog.setContentView(v);
        secondDialog.setCancelable(true);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 1.0);
        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.30);
        secondDialog.getWindow().setLayout(width, lp.height);

        secondDialog.show();
    }

    public static void alertAddProductsDetails(final Context mContext, final Dialog firstDialog, final Dialog secondDialog) {
        final TextView mTxtSave, mTxtQuantity;
        final DialogListAdapter adapter;
        final Dialog thirdDialog = new Dialog(mContext, R.style.AlertDialogCustom);
        thirdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(mContext).inflate(R.layout.alert_add_product_details, null);
        mTxtSave = v.findViewById(R.id.txtSave);
        mTxtQuantity = v.findViewById(R.id.txtSelectQuantity);

        mTxtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstDialog != null) {
                    firstDialog.dismiss();
                }
                if (secondDialog != null) {
                    secondDialog.dismiss();
                }
                if (thirdDialog != null) {
                    thirdDialog.dismiss();
                }
            }
        });

        mTxtQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<DialogList> mDialogList = new ArrayList<>();
                for (int i = 0; i < 100; i++) {
                    mDialogList.add(new DialogList("", (i + 1) + ""));
                }

                PopUtils.alertDialogList(mContext, mDialogList, new DialogListInterface() {
                    @Override
                    public void DialogListInterface(String id, String value) {
                        mTxtQuantity.setText(value);
                    }
                }, "left");
            }
        });

        thirdDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = thirdDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        thirdDialog.setContentView(v);
        thirdDialog.setCancelable(true);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 1.0);
        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.30);
        thirdDialog.getWindow().setLayout(width, lp.height);

        thirdDialog.show();
    }

    public static void alertActivateSubscription(final Context mContext, String message, final View.OnClickListener okClick) {
        TextView mTxtOk, mTxtMessage;
        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(mContext).inflate(R.layout.alert_activate_subscription, null);
        mTxtOk = v.findViewById(R.id.txtOk);
        mTxtMessage = v.findViewById(R.id.txtMessage);

        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mTxtMessage.setText(message);
        mTxtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (okClick != null) {
                    okClick.onClick(v);
                }
            }
        });

        dialog.setContentView(v);
        dialog.setCancelable(false);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.30);
        dialog.getWindow().setLayout(width, lp.height);

        dialog.show();
    }

    public static EditText mEdtPinOne, mEdtPinTwo, mEdtPinThree, mEdtPinFour;

    public static void alertOtpDialog(final Context mContext, final Verification verification, String mobile, final String otp1,
                                      final WrongNumberClicked wrongNumberClicked, final VerifyOtp verifyOtp) {
        otp = otp1;
        TextView mTxtVerify, mTxtWrongNumber, mTxtResend;
        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(mContext).inflate(R.layout.alert_otp_dialog, null);
        mTxtVerify = v.findViewById(R.id.txtVerify);
        mTxtWrongNumber = v.findViewById(R.id.txtWrongNumber);
        mTxtResend = v.findViewById(R.id.txtResend);
        mEdtPinOne = v.findViewById(R.id.edtPinOne);
        mEdtPinTwo = v.findViewById(R.id.edtPinTwo);
        mEdtPinThree = v.findViewById(R.id.edtPinThree);
        mEdtPinFour = v.findViewById(R.id.edtPinFour);
//        mEdtPinFour.setSelection(mEdtPinFour.getText().toString().length());

        showTimer(mContext, mobile, mTxtResend, verification);

        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mTxtWrongNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        mEdtPinOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    mEdtPinTwo.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEdtPinTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    mEdtPinThree.requestFocus();
                } else if (s.toString().length() == 0) {
                    mEdtPinOne.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEdtPinThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    mEdtPinFour.requestFocus();
                } else if (s.toString().length() == 0) {
                    mEdtPinTwo.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEdtPinFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0) {
                    mEdtPinThree.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mTxtVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEdtPinOne.getText().toString().trim())) {
                    Toast.makeText(mContext, "Please enter OTP in all fields", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mEdtPinTwo.getText().toString().trim())) {
                    Toast.makeText(mContext, "Please enter OTP in all fields", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mEdtPinThree.getText().toString().trim())) {
                    Toast.makeText(mContext, "Please enter OTP in all fields", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mEdtPinFour.getText().toString().trim())) {
                    Toast.makeText(mContext, "Please enter OTP in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (verifyOtp != null && verification != null) {
                        verifyOtp.verifyOtp(dialog, mEdtPinOne.getText().toString() +
                                mEdtPinTwo.getText().toString() +
                                mEdtPinThree.getText().toString() +
                                mEdtPinFour.getText().toString());
                    }
                }
            }
        });

        dialog.setContentView(v);
        dialog.setCancelable(false);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.30);
        dialog.getWindow().setLayout(width, lp.height);

        dialog.show();
    }

    /* public static void alertOtpDialog(final Context mContext, String mobile, final String otp1,
                                         final WrongNumberClicked wrongNumberClicked, final VerifyOtp verifyOtp) {
        otp = otp1;
        TextView mTxtVerify, mTxtWrongNumber, mTxtResend;
//        final EditText mEdtPinOne, mEdtPinTwo, mEdtPinThree, mEdtPinFour;
        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(mContext).inflate(R.layout.alert_otp_dialog, null);
        mTxtVerify = (TextView) v.findViewById(R.id.txtVerify);
        mTxtWrongNumber = (TextView) v.findViewById(R.id.txtWrongNumber);
        mTxtResend = (TextView) v.findViewById(R.id.txtResend);
        mEdtPinOne = (EditText) v.findViewById(R.id.edtPinOne);
        mEdtPinTwo = (EditText) v.findViewById(R.id.edtPinTwo);
        mEdtPinThree = (EditText) v.findViewById(R.id.edtPinThree);
        mEdtPinFour = (EditText) v.findViewById(R.id.edtPinFour);
//        mEdtPinFour.setSelection(mEdtPinFour.getText().toString().length());

        showTimer(mContext, mobile, mTxtResend);

        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mTxtWrongNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        *//*mTxtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "OTP sent successfully", Toast.LENGTH_SHORT).show();
            }
        });*//*
        mEdtPinOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    mEdtPinTwo.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEdtPinTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    mEdtPinThree.requestFocus();
                } else if (s.toString().length() == 0) {
                    mEdtPinOne.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEdtPinThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 1) {
                    mEdtPinFour.requestFocus();
                } else if (s.toString().length() == 0) {
                    mEdtPinTwo.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEdtPinFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0) {
                    mEdtPinThree.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mTxtVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEdtPinOne.getText().toString().trim())) {
                    Toast.makeText(mContext, "Please enter OTP in all fields", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mEdtPinTwo.getText().toString().trim())) {
                    Toast.makeText(mContext, "Please enter OTP in all fields", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mEdtPinThree.getText().toString().trim())) {
                    Toast.makeText(mContext, "Please enter OTP in all fields", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mEdtPinFour.getText().toString().trim())) {
                    Toast.makeText(mContext, "Please enter OTP in all fields", Toast.LENGTH_SHORT).show();
                } else if (!(mEdtPinOne.getText().toString().trim() +
                        mEdtPinTwo.getText().toString().trim() +
                        mEdtPinThree.getText().toString().trim() +
                        mEdtPinFour.getText().toString().trim()).equalsIgnoreCase(otp)) {
                    Toast.makeText(mContext, "Entered otp is not valid, try with some other", Toast.LENGTH_SHORT).show();
                } else {
                    if (verifyOtp != null) {
                        verifyOtp.verifyOtp(dialog);
                    }
                }
            }
        });

        dialog.setContentView(v);
        dialog.setCancelable(false);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.30);
        dialog.getWindow().setLayout(width, lp.height);

        dialog.show();
    }*/

    public static void showTimer(final Context mContext, final String mobile, final TextView mTxtResend,
                                 final Verification verification) {
        new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                mTxtResend.setOnClickListener(null);

                String timeInSeconds = (int) (millisUntilFinished / 1000) + "";

                if (Integer.parseInt(timeInSeconds) < 60) {
                    if ((Integer.parseInt(timeInSeconds)) < 10) {
                        mTxtResend.setText("Resend Via Call " + "(00 : " + "0" + timeInSeconds + ")");
                    } else {
                        mTxtResend.setText("Resend Via Call " + "(00 : " + timeInSeconds + ")");
                    }
                } else if (Integer.parseInt(timeInSeconds) > 60 && Integer.parseInt(timeInSeconds) < 120) {
                    if ((Integer.parseInt(timeInSeconds) - 60) < 10) {
                        mTxtResend.setText("Resend Via Call " + "(01 : " + "0" + (Integer.parseInt(timeInSeconds) - 60) + ")");
                    } else {
                        mTxtResend.setText("Resend Via Call " + "(01 : " + (Integer.parseInt(timeInSeconds) - 60) + ")");
                    }
                } else if (Integer.parseInt(timeInSeconds) > 120 && Integer.parseInt(timeInSeconds) < 180) {
                    if ((Integer.parseInt(timeInSeconds) - 120) < 10) {
                        mTxtResend.setText("Resend Via Call " + "(02 : " + "0" + (Integer.parseInt(timeInSeconds) - 120) + ")");
                    } else {
                        mTxtResend.setText("Resend Via Call " + "(02 : " + (Integer.parseInt(timeInSeconds) - 120) + ")");
                    }
                }
            }

            public void onFinish() {
                mTxtResend.setText("Resend Via Call " + "(00 : 00)");
                mTxtResend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTxtResend.setOnClickListener(null);

                        if (verification != null) {
                            verification.resend("voice");
                        }

/*//                        PopUtils.alertDialog(mContext, "A new verification code is sent to your given mobile number", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
                        showLoadingDialog(mContext, "Loading...", false);
                        HashMap<String, String> params = new HashMap<>();
                        JSONObject mJsonObject = new JSONObject();
                        try {
                            mJsonObject.put("action", "request_for_otp");
                            mJsonObject.put("phone_no", mobile);
                            ServerResponse serverResponse = new ServerResponse();
                            serverResponse.serviceRequestJSonObject(mContext, "POST", WebServices.BASE_URL, mJsonObject, params,
                                    new IParseListener() {
                                        @Override
                                        public void ErrorResponse(String response, int requestCode) {
                                            hideLoadingDialog();
                                            PopUtils.alertDialog(mContext, "", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void SuccessResponse(String response, int requestCode) {
                                            hideLoadingDialog();
                                            switch (requestCode) {
                                                case WsUtils.WS_CODE_REQUEST_FOR_OTP:
                                                    responseForRequestForOtp(mContext, response, mobile, mTxtResend);
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }, WsUtils.WS_CODE_REQUEST_FOR_OTP);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                            }
//                        });*/
                    }
                });
            }
        }.start();
    }

   /* private static void responseForRequestForOtp(final Context mContext, String response, final String mobile,
                                                 final TextView mTxtResend) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    otp = mJsonObject.optString("otp");
                    PopUtils.alertDialog(mContext, "A new verification code is sent to your given mobile number", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showTimer(mContext, mobile, mTxtResend, verification);
                        }
                    });
//                    Toast.makeText(mContext, "A new verification code is sent to your given mobile number", Toast.LENGTH_SHORT).show();
//                    showTimer(mContext, mobile, mTxtResend);
                } else {
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/

    public static void showLoadingDialog(Context mContext, final String title, final boolean isCancelable) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                return;
            }
            progressDialog = new ProgressDialog(mContext, R.style.progressDialog);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(title);
            progressDialog.setCancelable(isCancelable);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hides loading dialog if shown
     */
    public static void hideLoadingDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            progressDialog = null;
        } catch (Exception e) {
            progressDialog = null;
        }
    }

    public static void versionUpdateDialog(final Context mContext, String message, final View.OnClickListener oneClick, final View.OnClickListener twoClick, boolean isMandatory) {
        TextView mTxtOne, mTxtTwo, mTxtMessage;
        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(mContext).inflate(R.layout.alert_twobutton_dialog, null);
        mTxtOne = v.findViewById(R.id.txtYes);
        mTxtOne.setText("Update");
        mTxtTwo = v.findViewById(R.id.txtNo);
        mTxtTwo.setText("Later");
        mTxtMessage = v.findViewById(R.id.txtMessage);

        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mTxtMessage.setText(message);

        if (isMandatory) {
            mTxtTwo.setVisibility(View.GONE);
            mTxtTwo.setClickable(false);
            v.findViewById(R.id.llTwo).setVisibility(View.GONE);
        } else {
            mTxtTwo.setVisibility(View.VISIBLE);
            mTxtTwo.setClickable(true);
            v.findViewById(R.id.llTwo).setVisibility(View.VISIBLE);
            mTxtTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (twoClick != null) {
                        twoClick.onClick(v);
                    }
                }
            });
        }
        
        mTxtOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (oneClick != null) {
                    oneClick.onClick(v);
                }
            }
        });

        dialog.setContentView(v);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.30);
        dialog.getWindow().setLayout(width, lp.height);

        dialog.show();
    }

    public static void alertTwoButtonDialog(final Context mContext, String message, String firstText, String secondText,
                                            final View.OnClickListener oneClick, final View.OnClickListener twoClick) {
        TextView mTxtOne, mTxtTwo, mTxtMessage, mTxtTitle;
        LinearLayout mLlOne, mLlTwo;
        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(mContext).inflate(R.layout.alert_twobutton_dialog, null);
        mTxtOne = v.findViewById(R.id.txtYes);
        mTxtOne.setText(firstText);
        mTxtTwo = v.findViewById(R.id.txtNo);
        mTxtTwo.setText(secondText);
        mTxtMessage = v.findViewById(R.id.txtMessage);
        mTxtTitle = v.findViewById(R.id.txtTitle);
        mLlOne = v.findViewById(R.id.llOne);
        mLlTwo = v.findViewById(R.id.llTwo);

        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mTxtMessage.setText(message);

        mTxtOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (oneClick != null) {
                    oneClick.onClick(v);
                }
            }
        });

        mTxtTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (twoClick != null) {
                    twoClick.onClick(v);
                }
            }
        });

        dialog.setContentView(v);
        dialog.setCancelable(false);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.30);
        dialog.getWindow().setLayout(width, lp.height);

        dialog.show();
    }

    static int year1;
    static int month1;
    static int day1;

    public static void alertDatePicker(final Context mContext, String StartDate, String EndDate,
                                       final DatePickerInterface mDatePickerInterface,
                                       final int year, final int month, final int day) {
        year1 = year;
        month1 = month;
        day1 = day;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Date startDate, endDate;
        int startYear = 0;
        int startMonth = 0;
        int startDay = 0;
        int endYear = 0;
        int endMonth = 0;
        int endDay = 0;

        try {
            startDate = format.parse(StartDate);
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            startYear = startCalendar.get(Calendar.YEAR);
            startMonth = startCalendar.get(Calendar.MONTH);
            startDay = startCalendar.get(Calendar.DATE);

            endDate = format.parse(EndDate);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);
            endYear = endCalendar.get(Calendar.YEAR);
            endMonth = endCalendar.get(Calendar.MONTH);
            endDay = endCalendar.get(Calendar.DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        final TextView mTxtStartDate, mTxtEndDate, mTxtSave;

        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(mContext).inflate(R.layout.alert_date_picker, null);
        mTxtStartDate = v.findViewById(R.id.txtStartDate);
        mTxtEndDate = v.findViewById(R.id.txtEndDate);
        mTxtSave = v.findViewById(R.id.txtSave);

        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final int finalStartDay = startDay;
        final int finalStartMonth = startMonth;
        final int finalStartYear = startYear;
        final int finalEndYear = endYear;
        final int finalEndMonth = endMonth;
        final int finalEndDay = endDay;
        mTxtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalender(mContext, true, finalStartYear, finalStartMonth, finalStartDay,
                        finalEndYear, finalEndMonth, finalEndDay,
                        mTxtStartDate, true);
            }
        });

        mTxtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mTxtStartDate.getText().toString().trim())) {
                    PopUtils.alertDialog(mContext, "Please select start date first", null);
                } else {
                    showCalender(mContext, true, year1, month1, day1,
                            finalEndYear, finalEndMonth, finalEndDay
                            , mTxtEndDate, false);
                }
            }
        });

        mTxtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mTxtStartDate.getText().toString().trim())) {
                    Toast.makeText(mContext, "Please select start date", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mTxtEndDate.getText().toString().trim())) {
                    Toast.makeText(mContext, "Please select end date", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    if (mDatePickerInterface != null) {
                        mDatePickerInterface.datePickerInterface(mTxtStartDate.getText().toString().trim(), mTxtEndDate.getText().toString().trim());
                    }
                }
            }
        });

        dialog.setContentView(v);
        dialog.setCancelable(true);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.30);
        dialog.getWindow().setLayout(width, lp.height);

        dialog.show();
    }

    private static void showCalender(Context mContext, boolean showMinDate, int minYear, int minMonth, int minDay,
                                     int maxYear, int maxMonth, int maxDay,
                                     final TextView textview, final boolean startDate) {
        DatePickerDialog dialog = null;
        Calendar minDate = Calendar.getInstance();
        minDate.set(minYear, minMonth, minDay);

        Calendar maxDate = Calendar.getInstance();
        maxDate.set(maxYear, maxMonth, maxDay);

        dialog = new DatePickerDialog(mContext, R.style.CalenderTheme, new DatePickerDialog.OnDateSetListener() {
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

                textview.setText(calenderYear + "-" + calenderMonth + "-" + calenderDay);
            }
        }, minYear, minMonth - 1, minDay);

        dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

//        if (showMinDate) {
//            dialog.getDatePicker().setMinDate(c.getTimeInMillis());
//        } else {
//            dialog.getDatePicker().setMaxDate(new Date().getTime());
//        }

        dialog.show();
    }

    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager _connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean _isConnected = false;
        NetworkInfo _activeNetwork = _connManager.getActiveNetworkInfo();
        if (_activeNetwork != null) {
            _isConnected = _activeNetwork.isConnectedOrConnecting();
        }
        return _isConnected;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getBase64String(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

//    public static boolean isSevenPmCrossed(Context context) {
//        Calendar calendar = Calendar.getInstance();
//        Calendar currentCal = Calendar.getInstance();
//        int cutOffTime = 23;
//        String cutOffTimeStr = UserDetails.getInstance(context).getCutOffTime();
//        if (!TextUtils.isEmpty(cutOffTimeStr)) {
//            cutOffTime = Integer.parseInt(cutOffTimeStr);
//        }
//        calendar.set(Calendar.HOUR_OF_DAY, cutOffTime);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        return currentCal.get(Calendar.HOUR_OF_DAY) >= calendar.get(Calendar.HOUR_OF_DAY);
////        return false;
//    }
//
//    public static boolean checkDateTimeCondition(Context context, String nextDate) {
//        Calendar calendar = Calendar.getInstance();
//        boolean isReturn = false;
//        if (!nextDate.equalsIgnoreCase("")) {
//            Date todayDate = calendar.getTime();
//
//            calendar.add(Calendar.DAY_OF_YEAR, 1);
//            Date tomorrowDate = calendar.getTime();
//
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            String todayAsString = dateFormat.format(todayDate);
//            String tomorrowAsString = dateFormat.format(tomorrowDate);
//
//            isReturn = !nextDate.equalsIgnoreCase(tomorrowAsString) ||
//                    calendar.get(Calendar.AM_PM) != Calendar.PM ||
//                    calendar.get(Calendar.HOUR) < 11;
//        }
//        return !isReturn;
////        return false;
//    }
//

    public static boolean isSevenPmCrossed(Context context) {
        Calendar calendar = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();
        int cutOffTime = 23;
        String cutOffTimeStr = UserDetails.getInstance(context).getCutOffTime();
        if (!TextUtils.isEmpty(cutOffTimeStr)) {
            cutOffTime = Integer.parseInt(cutOffTimeStr);
        }
        calendar.set(Calendar.HOUR_OF_DAY, cutOffTime);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return currentCal.getTimeInMillis() >= calendar.getTimeInMillis();
    }

    public static boolean checkDateTimeCondition(Context context, String nextDate) {
        Calendar calendar = Calendar.getInstance();
        boolean isReturn = true;
        int cutOffTime = 23;
        String cutOffTimeStr = UserDetails.getInstance(context).getCutOffTime();
        if (!TextUtils.isEmpty(cutOffTimeStr)) {
            cutOffTime = Integer.parseInt(cutOffTimeStr);
        }
        if (!TextUtils.isEmpty(nextDate)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Calendar cutOffCal = Calendar.getInstance();
            cutOffCal.set(Calendar.HOUR_OF_DAY, cutOffTime);
            cutOffCal.set(Calendar.MINUTE, 0);
            cutOffCal.set(Calendar.SECOND, 0);

            isReturn = !nextDate.equalsIgnoreCase(dateFormat.format(calendar.getTime())) || calendar.getTimeInMillis() < cutOffCal.getTimeInMillis();
        }
        return !isReturn;
    }

}
