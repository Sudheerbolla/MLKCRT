package com.godavarisandroid.mystore.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.godavarisandroid.mystore.utils.UserDetails;
import com.godavarisandroid.mystore.webUtils.ServerResponse;
import com.godavarisandroid.mystore.webUtils.WebServices;
import com.godavarisandroid.mystore.webUtils.WsUtils;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SchedulePaymentPickUpActivityOld extends BaseActivity implements View.OnClickListener, IParseListener, RadioGroup.OnCheckedChangeListener {

    private TextView mTxtTitle, txtWalletCredits, txt250, txt500, txt1000, txt1500, txtRequestCollection,
            txtPayOnline, txtAmountToBePaid, txtSchedulePickUp, txtScheduleDate, txtScheduleTime, txtCancelSchedulePickUp,
            txtRechargeAmount, txtReferralAmount, txtLastMonthDue, txtTotalDue;
    private RadioGroup rgDate, rgTime;
    private RadioButton radioTomorrow, radioDayAfter, radio7to8, radio8to9;
    private View viewTomorrow, viewDayAfter;
    private LinearLayout linWallet1, linWallet2, linCancelSchedule, linSchedule, linPrepaid, linPostPaid;
    private EditText edtTopUp;
    private ImageView mImgBack, mImgNotification;
    private PaytmPGService paytmPGService;
    private PaytmOrder paytmOrder;
    private String referralAmount, paymentScheduleId, orderId;
    private boolean isPrepaid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_payment_pickup);
        isPrepaid = UserDetails.getInstance(this).getPaymentType().equalsIgnoreCase("PrePaid");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }
        initComponents();
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
        if (PopUtils.checkInternetConnection(this)) {
            requestForGetScheduleWS();
        } else {
            PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
        }
        setUpAd();
    }

    private void setUpAd() {
        if (BaseApplication.adsObj != null) {
            final JSONObject adObj = BaseApplication.adsObj;
            final ImageView mImgAd = findViewById(R.id.imgAd);
            try {
                if (!TextUtils.isEmpty(adObj.optString("imagePath")))
                    Picasso.with(this).load(adObj.optString("imagePath")).into(mImgAd);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mImgAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SchedulePaymentPickUpActivityOld.this, TermsActivity.class);
                    if (TextUtils.isEmpty(adObj.optString("url"))) {
                        intent.putExtra("urlToLoad", adObj.optString("url"));
                        intent.putExtra("heading", "Offer Details");
                    } else {
                        intent.putExtra("urlToLoad", "https://www.milkcart.co.in");
                        intent.putExtra("heading", "Offer Details");
                    }
                    startActivity(intent);
                }
            });
        }
        if (!TextUtils.isEmpty(BaseApplication.walletAmount)) {
            txtWalletCredits.setText("" + BaseApplication.walletAmount);
            txtRechargeAmount.setText(BaseApplication.rechargeAmount);
            txtReferralAmount.setText(BaseApplication.referralAmount);
        }
    }

    private void setReferences() {
        txtLastMonthDue = findViewById(R.id.txtLastMonthDue);
        txtTotalDue = findViewById(R.id.txtTotalDue);
        linPrepaid = findViewById(R.id.linPrepaid);
        linPostPaid = findViewById(R.id.linPostPaid);

        mTxtTitle = findViewById(R.id.txtTitle);
        mImgNotification = findViewById(R.id.imgNotification);
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText(isPrepaid ? "Recharge" : "PayNow");

        mImgBack = findViewById(R.id.imgBack);
        mImgBack.setVisibility(View.VISIBLE);

        findViewById(R.id.imgLogo).setVisibility(View.GONE);
        mImgNotification.setVisibility(View.VISIBLE);

        txtWalletCredits = findViewById(R.id.txtWalletCredits);
        txtAmountToBePaid = findViewById(R.id.txtAmountToBePaid);
        txtSchedulePickUp = findViewById(R.id.txtSchedulePickUp);
        rgDate = findViewById(R.id.rgDate);
        rgTime = findViewById(R.id.rgTime);
        radioTomorrow = findViewById(R.id.radioTomorrow);
        radioDayAfter = findViewById(R.id.radioDayAfter);
        radio7to8 = findViewById(R.id.radio7to8);
        radio8to9 = findViewById(R.id.radio8to9);
        viewTomorrow = findViewById(R.id.viewTomorrow);
        viewDayAfter = findViewById(R.id.viewDayAfter);
        linWallet1 = findViewById(R.id.linWallet1);
        linWallet2 = findViewById(R.id.linWallet2);
        txt250 = findViewById(R.id.txt250);
        txt500 = findViewById(R.id.txt500);
        txt1000 = findViewById(R.id.txt1000);
        txt1500 = findViewById(R.id.txt1500);
        txtRequestCollection = findViewById(R.id.txtRequestCollection);
        txtPayOnline = findViewById(R.id.txtPayOnline);
        edtTopUp = findViewById(R.id.edtTopUp);

        linSchedule = findViewById(R.id.linSchedule);
        linCancelSchedule = findViewById(R.id.linCancelSchedule);
        txtScheduleDate = findViewById(R.id.txtScheduleDate);
        txtScheduleTime = findViewById(R.id.txtScheduleTime);
        txtCancelSchedulePickUp = findViewById(R.id.txtCancelSchedulePickUp);

        txtReferralAmount = findViewById(R.id.txtReferralAmount);
        txtRechargeAmount = findViewById(R.id.txtRechargeAmount);
    }

    private void showLayout1() {
        linWallet1.setVisibility(View.VISIBLE);
        linWallet2.setVisibility(View.GONE);
        if (isPrepaid) {
            linPostPaid.setVisibility(View.GONE);
            linPrepaid.setVisibility(View.VISIBLE);
        } else {
            linPostPaid.setVisibility(View.VISIBLE);
            linPrepaid.setVisibility(View.GONE);
            txtLastMonthDue.setText("Last Month's Due: " + BaseApplication.lastMonthDue);
            txtTotalDue.setText("Total Amount Due: " + BaseApplication.walletAmount);
            if (!isPrepaid && (TextUtils.isEmpty(BaseApplication.lastMonthDue) || BaseApplication.lastMonthDue.equalsIgnoreCase("0"))) {
                txtPayOnline.setVisibility(View.GONE);
                txtSchedulePickUp.setVisibility(View.GONE);
            } else {
                txtPayOnline.setVisibility(View.VISIBLE);
                txtSchedulePickUp.setVisibility(View.VISIBLE);
            }
            txtAmountToBePaid.setText(BaseApplication.walletAmount);
        }
    }

    private void showLayout2() {
        linWallet1.setVisibility(View.GONE);
        linWallet2.setVisibility(View.VISIBLE);
        linSchedule.setVisibility(View.VISIBLE);
    }

    private void setClickListeners() {
        mImgBack.setOnClickListener(this);
        mImgNotification.setOnClickListener(this);

        txt250.setOnClickListener(this);
        txt500.setOnClickListener(this);
        txt1000.setOnClickListener(this);
        txt1500.setOnClickListener(this);
        txtRequestCollection.setOnClickListener(this);
        txtPayOnline.setOnClickListener(this);
        txtSchedulePickUp.setOnClickListener(this);
        txtCancelSchedulePickUp.setOnClickListener(this);

        rgDate.setOnCheckedChangeListener(this);
        rgTime.setOnCheckedChangeListener(this);

        edtTopUp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int amount = s.length() > 0 ? Integer.parseInt(s.toString()) : 0;
                if (amount == 250) {
                    makeViewSelected(txt250);
                    txtAmountToBePaid.setText(String.valueOf(amount));
                } else if (amount == 500) {
                    makeViewSelected(txt500);
                    txtAmountToBePaid.setText(String.valueOf(amount));
                } else if (amount == 1000) {
                    makeViewSelected(txt1000);
                    txtAmountToBePaid.setText(String.valueOf(amount));
                } else if (amount == 1500) {
                    makeViewSelected(txt1500);
                    txtAmountToBePaid.setText(String.valueOf(amount));
                } else {
                    resetButtons();
                    txtAmountToBePaid.setText(String.valueOf(amount));
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.imgNotification:
                navigateActivity(new Intent(this, HelpActivity.class), false);
                break;
            case R.id.txt250:
                makeViewSelected(txt250);
                setAmountToEditText(250);
                break;
            case R.id.txt500:
                makeViewSelected(txt500);
                setAmountToEditText(500);
                break;
            case R.id.txt1000:
                makeViewSelected(txt1000);
                setAmountToEditText(1000);
                break;
            case R.id.txt1500:
                makeViewSelected(txt1500);
                setAmountToEditText(1500);
                break;
            case R.id.txtRequestCollection:
                if (isPrepaid) {
                    if (checkValidations()) {
                        showLayout2();
                    }
                } else {
                    if (TextUtils.isEmpty(BaseApplication.lastMonthDue) || BaseApplication.lastMonthDue.equalsIgnoreCase("0")) {
                        PopUtils.alertDialog(this, "You don't have any outstanding due to pay.", null);
                    } else {
                        showLayout2();
                    }
                }
                break;
            case R.id.txtPayOnline:
                if (isPrepaid) {
                    if (checkValidations()) {
                        proceedForPayment();
                    }
                } else {
                    if (TextUtils.isEmpty(BaseApplication.lastMonthDue) || BaseApplication.lastMonthDue.equalsIgnoreCase("0")) {
                        PopUtils.alertDialog(this, "You don't have any outstanding due to pay.", null);
                    } else {
                        proceedForPayment();
                    }
                }
                break;
            case R.id.txtSchedulePickUp:
                if (PopUtils.checkInternetConnection(this)) {
                    requestForSchedulePickUpWS();
                } else {
                    PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                }
                break;
            case R.id.txtCancelSchedulePickUp:
                if (PopUtils.checkInternetConnection(this)) {
                    PopUtils.alertTwoButtonDialog(this, "Are you sure, you want cancel the payment pickup", "Yes", "No", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestForCancelSchedulerWS();
                        }
                    }, null);
                } else {
                    PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                }
                break;
            default:
                break;
        }
    }

    //{"action":"cancle_Pickup_shedule","ps_id":"1"}
    private void requestForGetScheduleWS() {
        showLoadingDialog("Loading...", false);
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("?action=user_pickshedule");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestStringBuilder(this, WebServices.BASE_URL, stringBuilder, this, WsUtils.WS_CODE_GET_PICK_UP_SCHEDULE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestForCancelSchedulerWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "cancle_Pickup_shedule");
            mJsonObject.put("ps_id", paymentScheduleId);

            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_DELETE_PICK_UP_SCHEDULE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestForConfirmPaymentSuccessWS(String id, String amount) {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "online_payment_service");
            mJsonObject.put("transaction_id", id);
            mJsonObject.put("amount", amount);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_CONFIRM_PAYTM_PAYMENT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestForSchedulePickUpWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            Calendar calendar = Calendar.getInstance();
            if (radioTomorrow.isChecked()) {
                calendar.add(Calendar.DAY_OF_YEAR, +1);
            } else if (radioDayAfter.isChecked()) {
                calendar.add(Calendar.DAY_OF_YEAR, +2);
            }

            String time = "";
            if (radio7to8.isChecked()) {
                time = "7am to 8am";
            } else if (radio8to9.isChecked()) {
                time = "8am to 9am";
            }

            mJsonObject.put("action", "pickup_shedule_date");
            mJsonObject.put("amount", isPrepaid ? edtTopUp.getText().toString().trim() : BaseApplication.lastMonthDue);
            mJsonObject.put("date", new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
            mJsonObject.put("time", time);

            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_PICK_UP_SCHEDULE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void makeViewSelected(TextView txtView) {
        resetButtons();
        txtView.setBackground(ContextCompat.getDrawable(this, R.drawable.lite_corners_app_solid_app));
        txtView.setTextColor(ContextCompat.getColor(this, R.color.color_white));
    }

    private void resetButtons() {
        txt250.setBackground(ContextCompat.getDrawable(this, R.drawable.border_app_color));
        txt500.setBackground(ContextCompat.getDrawable(this, R.drawable.border_app_color));
        txt1000.setBackground(ContextCompat.getDrawable(this, R.drawable.border_app_color));
        txt1500.setBackground(ContextCompat.getDrawable(this, R.drawable.border_app_color));

        txt250.setTextColor(ContextCompat.getColor(this, R.color.app_color));
        txt500.setTextColor(ContextCompat.getColor(this, R.color.app_color));
        txt1000.setTextColor(ContextCompat.getColor(this, R.color.app_color));
        txt1500.setTextColor(ContextCompat.getColor(this, R.color.app_color));
    }

    private boolean checkValidations() {
        String amount = edtTopUp.getText().toString().trim();
        if (TextUtils.isEmpty(amount)) {
            PopUtils.alertDialog(this, "please enter amount", null);
            return false;
        } else if (Integer.parseInt(amount) < 100) {
            PopUtils.alertDialog(this, "Oops.!!please enter amount greater than 100 ", null);
            return false;
        }
        return true;
    }

    private void proceedForPayment() {
        initPaytm();
        orderId = "order" + System.currentTimeMillis();
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        try {
            params.put("MID", PopUtils.midStaging);
            params.put("ORDER_ID", orderId);
            params.put("CUST_ID", PopUtils.userPrefixStaging + UserDetails.getInstance(this).getName().trim().split("\\s+")[0]);
            params.put("INDUSTRY_TYPE_ID", "Retail");
            params.put("MOBILE_NO", "7777777777");
            params.put("CHANNEL_ID", PopUtils.CHANNEL_ID);
            params.put("EMAIL", "username@emailprovider.com");
            params.put("TXN_AMOUNT", isPrepaid ? edtTopUp.getText().toString().trim() : BaseApplication.lastMonthDue);
            params.put("WEBSITE", PopUtils.channelNameStagingMobile);
            params.put("CALLBACK_URL", PopUtils.CALL_BACK_URL + orderId);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestString(this, WebServices.PAYTM_CHECKHASH_BASE_URL, params, this, WsUtils.WS_CODE_PAYTM_CHECKSUM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPaytm() {
        paytmPGService = PaytmPGService.getStagingService();
    }

    private void createOrder(String checkSum) {
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", PopUtils.midStaging);
        paramMap.put("ORDER_ID", orderId);
        paramMap.put("CUST_ID", PopUtils.userPrefixStaging + UserDetails.getInstance(this).getName().trim().split("\\s+")[0]);
        paramMap.put("MOBILE_NO", "7777777777");
        paramMap.put("CHANNEL_ID", PopUtils.CHANNEL_ID);
        paramMap.put("EMAIL", "username@emailprovider.com");
        paramMap.put("TXN_AMOUNT", isPrepaid ? edtTopUp.getText().toString().trim() : BaseApplication.lastMonthDue);
        paramMap.put("WEBSITE", PopUtils.channelNameStagingMobile);
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        paramMap.put("CALLBACK_URL", PopUtils.CALL_BACK_URL + orderId);
        paramMap.put("CHECKSUMHASH", checkSum);

        paytmOrder = new PaytmOrder(paramMap);
        paytmPGService.initialize(paytmOrder, null);
        paytmPGService.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {

            public void someUIErrorOccurred(String inErrorMessage) {
                Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage, Toast.LENGTH_LONG).show();
            }

            public void onTransactionResponse(Bundle inResponse) {
                try {
                    if (!TextUtils.isEmpty(inResponse.getString("TXNID")) && inResponse.containsKey("BANKTXNID") && !TextUtils.isEmpty(inResponse.getString("BANKTXNID"))) {
                        requestForConfirmPaymentSuccessWS(inResponse.getString("BANKTXNID"), inResponse.getString("TXNAMOUNT"));
                    } else
                        Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void networkNotAvailable() {
                Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
            }

            public void clientAuthenticationFailed(String inErrorMessage) {
                Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage, Toast.LENGTH_LONG).show();
            }

            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                Toast.makeText(getApplicationContext(), "Unable to load webpage " + inFailingUrl + " err code: " + iniErrorCode + " err msg: " + inErrorMessage, Toast.LENGTH_LONG).show();
            }

            public void onBackPressedCancelTransaction() {
                Toast.makeText(getApplicationContext(), "Transaction cancelled", Toast.LENGTH_LONG).show();
            }

            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                Toast.makeText(getApplicationContext(), "Transaction Cancelled" + inResponse.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setAmountToEditText(int amount) {
        edtTopUp.setText(String.valueOf(amount));
        txtAmountToBePaid.setText(String.valueOf(amount));
    }

    /*Called when requesting fro wallet service call*/
    private void requestForWalletWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "wallet_amount");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params, this, WsUtils.WS_CODE_WALLET);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (linWallet1.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else {
            if (TextUtils.isEmpty(paymentScheduleId)) {
                showLayout1();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void ErrorResponse(String response, int requestCode) {
        hideLoadingDialog();
        PopUtils.alertDialog(this, response, new View.OnClickListener() {
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
            case WsUtils.WS_CODE_WALLET:
                responseForWallet(response);
                break;
            case WsUtils.WS_CODE_PICK_UP_SCHEDULE:
                parsePickUpScheduleResponse(response);
                break;
            case WsUtils.WS_CODE_GET_PICK_UP_SCHEDULE:
                parseGetScheduleResponse(response);
                break;
            case WsUtils.WS_CODE_DELETE_PICK_UP_SCHEDULE:
                parseCancelScheduleResponse(response);
                break;
            case WsUtils.WS_CODE_PAYTM_CHECKSUM:
                parsePaytmCheckSum(response);
                break;
            case WsUtils.WS_CODE_CONFIRM_PAYTM_PAYMENT:
                parseConfirmPaymentResponse(response);
                break;
            default:
                break;
        }
    }

    private void parseConfirmPaymentResponse(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    PopUtils.alertDialog(this, message, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //    {"CHECKSUMHASH":"SY\/73\/2r37e\/A+hRUstcvKZ88be+prLfIfRORIvR+css9fwBraSBBYa2fRFl3zUgsGD3VV7OxWd551W\/35WG3QTZLw1BRP5kpz4gC5f4r3U=","ORDER_ID":"5be922e4db06d","payt_STATUS":"1"}
    private void parsePaytmCheckSum(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                createOrder(mJsonObject.getString("CHECKSUMHASH"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseCancelScheduleResponse(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    PopUtils.alertDialog(this, "Your Payment pickup is cancelled successfully.", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseGetScheduleResponse(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.optString("status").equalsIgnoreCase("200")) {
                    JSONArray dataArray = mJsonObject.optJSONArray("data");
                    if (dataArray.length() > 0) {
                        JSONObject dataObject = dataArray.getJSONObject(0);
                        showLayout2();
                        linSchedule.setVisibility(View.GONE);
                        linCancelSchedule.setVisibility(View.VISIBLE);
                        txtAmountToBePaid.setText(dataObject.optString("amount"));
                        paymentScheduleId = dataObject.optString("ps_id");
                        String date = dataObject.optString("date");
                        String[] dates = date.split("-");
                        Calendar serverDate = Calendar.getInstance();
                        Calendar today = Calendar.getInstance();
                        today.add(Calendar.MONTH, +1);
                        serverDate.set(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));
                        if (today.get(Calendar.DAY_OF_YEAR) == serverDate.get(Calendar.DAY_OF_YEAR)) {
                            txtScheduleDate.setText("Today");
                        } else if (today.get(Calendar.DAY_OF_YEAR) + 1 == serverDate.get(Calendar.DAY_OF_YEAR)) {
                            txtScheduleDate.setText("Tomorrow");
                        } else if (today.get(Calendar.DAY_OF_YEAR) + 2 == serverDate.get(Calendar.DAY_OF_YEAR)) {
                            txtScheduleDate.setText("Day After Tomorrow");
                        } else
                            txtScheduleDate.setText(dataObject.optString("date"));

                        txtScheduleTime.setText(dataObject.optString("time"));
                    } else {
                        showLayout1();
                    }
                } else {
                    PopUtils.alertDialog(this, message, null);
                    showLayout1();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parsePickUpScheduleResponse(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    PopUtils.alertDialog(this, message, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void responseForWallet(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    referralAmount = mJsonObject.optString("referal_amount");

                    JSONObject mWalletValueObject = mJsonObject.getJSONObject("vallet_value");
                    String walletAmount = mWalletValueObject.optString("amount");

                    BaseApplication.rechargeAmount = mJsonObject.optString("recharge_amount");
                    BaseApplication.referralAmount = referralAmount;
                    BaseApplication.walletAmount = walletAmount;

                    if (isPrepaid) {
                        JSONArray lastMonthsArray = mJsonObject.optJSONArray("last_four_months");
                        if (lastMonthsArray.length() > 0) {
                            JSONObject lastMonthObject = lastMonthsArray.optJSONObject(lastMonthsArray.length() - 1);
                            if (lastMonthObject != null)
                                BaseApplication.lastMonthDue = lastMonthObject.optString("amount");
                            else BaseApplication.lastMonthDue = "0";
                        } else BaseApplication.lastMonthDue = "0";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.rgDate:
                switch (checkedId) {
                    case R.id.radioTomorrow:
                        viewTomorrow.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                        viewDayAfter.setBackgroundColor(ContextCompat.getColor(this, R.color.color_light_grey));
                        break;
                    case R.id.radioDayAfter:
                        viewDayAfter.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                        viewTomorrow.setBackgroundColor(ContextCompat.getColor(this, R.color.color_light_grey));
                        break;
                }
                break;
            case R.id.rgTime:
                switch (checkedId) {
                    case R.id.radio7to8:
                        break;
                    case R.id.radio8to9:
                        break;
                }
                break;
        }
    }
}
