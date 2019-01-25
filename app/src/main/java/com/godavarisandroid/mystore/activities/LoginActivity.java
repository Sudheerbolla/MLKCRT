package com.godavarisandroid.mystore.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.interfaces.SmsListener;
import com.godavarisandroid.mystore.interfaces.VerifyOtp;
import com.godavarisandroid.mystore.interfaces.WrongNumberClicked;
import com.godavarisandroid.mystore.receiver.IncomingSms;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.godavarisandroid.mystore.utils.UserDetails;
import com.godavarisandroid.mystore.webUtils.ServerResponse;
import com.godavarisandroid.mystore.webUtils.WebServices;
import com.godavarisandroid.mystore.webUtils.WsUtils;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by UMA on 4/21/2018.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, IParseListener {
    private TextView mTxtValidate;
    private EditText mEdtMobile;
    private ImageView mImgBack;

    private Dialog otpDialog;
    private String messageValue;

    private static final String[] PERMISSIONS = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS};
    public static final int PERMISSION_ALL = 1;

    private Verification verification;

    /*Called when push token refreshed*/
    BroadcastReceiver mPushInstanceIdReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("the token is::::::", intent.getStringExtra("token"));
            // checking for type intent filter
            if (intent.getAction().equals("REGISTRATION_COMPLETE")) {
                // gcm successfully registered
                Log.e("the token is::::::", intent.getStringExtra("token"));
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mPushInstanceIdReceiver, new IntentFilter("REGISTRATION_COMPLETE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPushInstanceIdReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!PopUtils.hasPermissions(LoginActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(LoginActivity.this, PERMISSIONS, PERMISSION_ALL);
        }

        initComponents();

        Log.d("the devic id is, ", Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));

        /*This is important because this will be called every time you receive any sms */
        IncomingSms.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                messageValue = messageText;
                PopUtils.mEdtPinOne.setText(messageText.substring(0, 1));
                PopUtils.mEdtPinTwo.setText(messageText.substring(1, 2));
                PopUtils.mEdtPinThree.setText(messageText.substring(2, 3));
                PopUtils.mEdtPinFour.setText(messageText.substring(3, 4));
                PopUtils.mEdtPinFour.setSelection(1);
                Toast.makeText(getApplication(), messageValue, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
    }

    /*Initializing Views*/
    private void setReferences() {
        mTxtValidate = findViewById(R.id.txtValidate);
        mEdtMobile = findViewById(R.id.edtMobile);
        mEdtMobile.setTransformationMethod(null);
        mImgBack = findViewById(R.id.imgBack);
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mTxtValidate.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtValidate:
                /*Checking validations on validate button click*/
                if (!TextUtils.isEmpty(checkValidation())) {
                    PopUtils.alertDialog(this, checkValidation(), null);
                } else {
                    initiateOtpService();
//                    if (PopUtils.checkInternetConnection(LoginActivity.this)) {
//                        requestForUserValidationWS();
//                    } else {
//                        PopUtils.alertDialog(LoginActivity.this, getString(R.string.pls_check_internet), null);
//                    }
                }
                break;
            case R.id.imgBack:
                /*Called on back button press*/
                finish();
                break;
            default:
                break;
        }
    }

    private void initiateOtpService() {
        try {
            showLoadingDialog("Loading...", false);
            verification = SendOtpVerification.createSmsVerification(SendOtpVerification.
                    config("91" + mEdtMobile.getText().toString()).
                    senderId("MLKCRT").
                    context(LoginActivity.this).
                    autoVerification(true).
                    build(), new VerificationListener() {
                @Override
                public void onInitiated(String response) {
                    hideLoadingDialog();
                    PopUtils.alertOtpDialog(LoginActivity.this, verification, mEdtMobile.getText().toString(), "",
                            new WrongNumberClicked() {
                                @Override
                                public void wrongNumberClicked() {

                                }
                            }, new VerifyOtp() {
                                @Override
                                public void verifyOtp(Dialog dialog, String code) {
                                    otpDialog = dialog;
                                    if (verification != null) {
                                        verification.verify(code);
                                    }
                                }
                            });
                }

                @Override
                public void onInitiationFailed(Exception paramException) {
                    hideLoadingDialog();
                }

                @Override
                public void onVerified(String response) {
                    hideLoadingDialog();
                    //                            Requesting for user validation service call
                    if (PopUtils.checkInternetConnection(LoginActivity.this)) {
                        requestForUserValidationWS();
                    } else {
                        PopUtils.alertDialog(LoginActivity.this, getString(R.string.pls_check_internet), null);
                    }
                }

                @Override
                public void onVerificationFailed(Exception paramException) {
                    hideLoadingDialog();
                    //                            Toast.makeText(LoginActivity.this, "Verification failed. Please try again with valid code", Toast.LENGTH_SHORT).show();
                    PopUtils.alertDialog(LoginActivity.this, "Invalid OTP. Please enter correct OTP and retry.", null);
                }

            });
            verification.initiate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 010) initiateOtpService();
    }

    private String checkValidation() {
        String message = "";
        if (TextUtils.isEmpty(mEdtMobile.getText().toString().trim())) {
            message = "Please enter mobile number";
        } else if (mEdtMobile.getText().toString().trim().length() != 10) {
            message = "Please enter a valid 10 digit mobile number";
        }
        return message;
    }

    private void requestForUserValidationWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "user_validation");
            mJsonObject.put("phone_no", mEdtMobile.getText().toString().trim());
            mJsonObject.put("token_id", UserDetails.getInstance(this).getPushToken());
            mJsonObject.put("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
            mJsonObject.put("device_type", "Android");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_USER_VALIDATION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Called when error occured from service call*/
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
            case WsUtils.WS_CODE_USER_VALIDATION:
                responseFoUserValidation(response);
                break;
            default:
                break;
        }
    }

    /*Response for user validation service call*/
    private void responseFoUserValidation(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    if (otpDialog != null) {
                        otpDialog.dismiss();
                    }

                    UserDetails.getInstance(this).setUserToken(mJsonObject.optString("token"));
                    UserDetails.getInstance(this).setCutOffTime(mJsonObject.optString("cut_of_time"));

                    JSONArray mDataArray = mJsonObject.getJSONArray("data");
                    JSONObject mDataObject = mDataArray.getJSONObject(0);
                    UserDetails.getInstance(this).setUserId(mDataObject.optString("u_id"));
                    UserDetails.getInstance(this).setName(mDataObject.optString("name"));
                    UserDetails.getInstance(this).setEmailId(mDataObject.optString("email"));
                    UserDetails.getInstance(this).setPhoneNo(mDataObject.optString("phone_no"));
                    UserDetails.getInstance(this).setCityId(mDataObject.optString("city"));
                    UserDetails.getInstance(this).setAreaId(mDataObject.optString("area"));
                    UserDetails.getInstance(this).setApartmentId(mDataObject.optString("apartment"));
                    UserDetails.getInstance(this).setBlock(mDataObject.optString("block"));
                    UserDetails.getInstance(this).setFlat(mDataObject.optString("flat"));
                    UserDetails.getInstance(this).setStreet(mDataObject.optString("street"));
                    UserDetails.getInstance(this).setLandmark(mDataObject.optString("landmark"));
                    UserDetails.getInstance(this).setPincode(mDataObject.optString("pincode"));
                    UserDetails.getInstance(this).setCity(mDataObject.optString("city_name"));
                    UserDetails.getInstance(this).setArea(mDataObject.optString("area_name"));
                    UserDetails.getInstance(this).setPaymentType(mDataObject.optString("payment_type"));
                    UserDetails.getInstance(this).setReferralCode(mDataObject.optString("referal_code"));
                    if (mJsonObject.has("is_enable_referal_code")) {
                        UserDetails.getInstance(this).setIsReferralCodeEnabled(Boolean.parseBoolean(mJsonObject.optString("is_enable_referal_code")));
                    }
                    if (mDataObject.optString("apartment_name").equalsIgnoreCase("null")) {
                        UserDetails.getInstance(this).setApartment("");
                    } else {
                        UserDetails.getInstance(this).setApartment(mDataObject.optString("apartment_name"));
                    }

                    try {
                        boolean needToShowRechargeBtn = mJsonObject.optBoolean("enable_recharge_btn");
                        UserDetails.getInstance(this).setIsEnableRechargeButton(needToShowRechargeBtn);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    navigateActivity(new Intent(this, HomeActivity.class), true);

                } else if (mJsonObject.getString("status").equalsIgnoreCase("401")) {
                    if (otpDialog != null) {
                        otpDialog.dismiss();
                    }
                    Intent intent = new Intent(this, RegistrationActivity.class);
                    intent.putExtra("MOBILE", mEdtMobile.getText().toString().trim());
                    if (mJsonObject.has("is_enable_referal_code")) {
                        intent.putExtra("IS_ENABLE_REFERAL_CODE", mJsonObject.getString("is_enable_referal_code"));
                        UserDetails.getInstance(this).setIsReferralCodeEnabled(Boolean.parseBoolean(mJsonObject.getString("is_enable_referal_code")));
                    }
                    startActivityForResult(intent, 010);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

                } else if (mJsonObject.getString("status").equalsIgnoreCase("400")) {
                    if (otpDialog != null) {
                        otpDialog.dismiss();
                    }
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

/* "Wallet_amount": {
    "amount": -140
  },*/