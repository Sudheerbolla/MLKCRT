package com.godavarisandroid.mystore.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Excentd11 on 4/27/2018.
 */

public class EditProfileActivity extends BaseActivity implements View.OnClickListener, IParseListener {
    private TextView mTxtSave, mTxtTitle;
    private EditText mEdtName, mEdtEmail, mEdtMobile;
    private ImageView mImgLogo, mImgBack, mImgNotification;

    private Dialog otpDialog;
    private Verification verification;
    private String messageValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initComponents();

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
        mTxtTitle = (TextView) findViewById(R.id.txtTitle);
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText("Edit Profile");

        mTxtSave = (TextView) findViewById(R.id.txtSave);
        mImgLogo = (ImageView) findViewById(R.id.imgLogo);
        mImgLogo.setVisibility(View.GONE);

        mImgBack = (ImageView) findViewById(R.id.imgBack);
        mImgBack.setVisibility(View.VISIBLE);
        mImgNotification = (ImageView) findViewById(R.id.imgNotification);
        mImgNotification.setVisibility(View.VISIBLE);

        mEdtName = (EditText) findViewById(R.id.edtName);
        mEdtName.setText(UserDetails.getInstance(this).getName());
        mEdtEmail = (EditText) findViewById(R.id.edtEmail);
        mEdtEmail.setText(UserDetails.getInstance(this).getEmailId());
        mEdtMobile = (EditText) findViewById(R.id.edtMobile);
        mEdtMobile.setTransformationMethod(null);
        mEdtMobile.setText(UserDetails.getInstance(this).getPhoneNo());
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mTxtSave.setOnClickListener(this);
        mImgLogo.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mImgNotification.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSave:
                if (!checkValidation().equalsIgnoreCase("")) {
                    PopUtils.alertDialog(this, checkValidation(), null);
                } else if (!UserDetails.getInstance(this).getPhoneNo().equalsIgnoreCase(mEdtMobile.getText().toString().trim())) {
                    showLoadingDialog("Loading...", false);
                    verification = SendOtpVerification.createSmsVerification(SendOtpVerification.
                            config("91" + mEdtMobile.getText().toString()).
                            senderId("MLKCRT").
                            context(EditProfileActivity.this).
                            autoVerification(true).
                            build(), new VerificationListener() {
                        @Override
                        public void onInitiated(String response) {
                            hideLoadingDialog();
                            PopUtils.alertOtpDialog(EditProfileActivity.this, verification, mEdtMobile.getText().toString(), "",
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
                            Toast.makeText(EditProfileActivity.this, "Initiation failed. Please try again", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onVerified(String response) {
                            hideLoadingDialog();
                            if (PopUtils.checkInternetConnection(EditProfileActivity.this)) {
                                requestForUpdateProfileWS();
                            } else {
                                PopUtils.alertDialog(EditProfileActivity.this, getString(R.string.pls_check_internet), null);
                            }
                        }

                        @Override
                        public void onVerificationFailed(Exception paramException) {
                            hideLoadingDialog();
                            PopUtils.alertDialog(EditProfileActivity.this, "Invalid OTP. Please enter correct OTP and retry", null);
//                            Toast.makeText(EditProfileActivity.this, "Verification failed. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                    verification.initiate();
                } else {
                    if (PopUtils.checkInternetConnection(this)) {
                        requestForUpdateProfileWS();
                    } else {
                        PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                    }
                }
                break;
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.imgNotification:
                navigateActivity(new Intent(this, HelpActivity.class), false);
                break;
            default:
                break;
        }
    }

    /*Called when clicking on save button*/
    private String checkValidation() {
        String message = "";
        if (TextUtils.isEmpty(mEdtName.getText().toString().trim())) {
            message = "Please enter your name";
        } else if (TextUtils.isEmpty(mEdtEmail.getText().toString().trim())) {
            message = "Please enter your Email ID";
        } else if (!PopUtils.emailValidator(mEdtEmail.getText().toString().trim())) {
            message = "Please enter valid Email ID";
        } else if (TextUtils.isEmpty(mEdtMobile.getText().toString().trim())) {
            message = "Please enter your mobile number";
        } else if (mEdtMobile.getText().toString().trim().length() < 10) {
            message = "Entered mobile number should be minimum 10 numbers";
        }
        return message;
    }

    private void requestForOtpWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "request_for_otp");
            mJsonObject.put("phone_no", mEdtMobile.getText().toString().trim());
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_REQUEST_FOR_OTP);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Requesting for update profile service call*/
    private void requestForUpdateProfileWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "edit_personal_det");
            mJsonObject.put("name", mEdtName.getText().toString().trim());
            mJsonObject.put("email", mEdtEmail.getText().toString().trim());
            mJsonObject.put("phone_no", mEdtMobile.getText().toString().trim());
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_UPDATE_PROFILE);
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
            case WsUtils.WS_CODE_UPDATE_PROFILE:
                responseForUpdateProfile(response);
                break;
            case WsUtils.WS_CODE_REQUEST_FOR_OTP:
                responseForRequestForOtp(response);
                break;
            default:
                break;
        }
    }

    private void responseForRequestForOtp(String response) {
       /* if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {

                    PopUtils.alertOtpDialog(this,null,  mEdtMobile.getText().toString(), mJsonObject.optString("otp"),
                            new WrongNumberClicked() {
                                @Override
                                public void wrongNumberClicked() {

                                }
                            }, new VerifyOtp() {
                                @Override
                                public void verifyOtp(Dialog dialog, String code) {
                                    otpDialog = dialog;
                                    if (PopUtils.checkInternetConnection(EditProfileActivity.this)) {
                                        requestForUpdateProfileWS();
                                    } else {
                                        PopUtils.alertDialog(EditProfileActivity.this, getString(R.string.pls_check_internet), null);
                                    }
                                }
                            });
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
    }

    /*Response for update profile service call*/
    private void responseForUpdateProfile(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    if (otpDialog != null) {
                        otpDialog.dismiss();
                    }

                    JSONObject mDataObject = mJsonObject.getJSONObject("data");
                    UserDetails.getInstance(this).setName(mDataObject.optString("name"));
                    UserDetails.getInstance(this).setEmailId(mDataObject.optString("email"));
                    UserDetails.getInstance(this).setPhoneNo(mDataObject.optString("phone_no"));

                    PopUtils.alertDialog(this, "Your profile information is updated successfully", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("name", mEdtName.getText().toString().trim());
                            returnIntent.putExtra("email", mEdtEmail.getText().toString().trim());
                            returnIntent.putExtra("phone_no", mEdtMobile.getText().toString().trim());
                            setResult(RESULT_OK, returnIntent);
                            finish();
                            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                        }
                    });

                } else {
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

    /*Called on back button press*/
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
}
