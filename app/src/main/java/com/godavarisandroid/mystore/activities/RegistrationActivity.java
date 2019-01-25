package com.godavarisandroid.mystore.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.interfaces.DialogListInterface;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.DialogList;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.godavarisandroid.mystore.utils.UserDetails;
import com.godavarisandroid.mystore.webUtils.ServerResponse;
import com.godavarisandroid.mystore.webUtils.WebServices;
import com.godavarisandroid.mystore.webUtils.WsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener, IParseListener {
    private TextView mTxtGetSetGo, mTxtTerms;
    private EditText mEdtName, mEdtEmailId, mEdtCity, mEdtArea, edtReferalCode;
    private LinearLayout linReferalCode;
    private ImageView mImgBack;

    private boolean IS_ENABLE_REFERAL_CODE = false;

    private String mobile = "", cityId = "", areaId = "";
    private ArrayList<DialogList> mDialogList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initComponents();
    }

    private void initComponents() {
        setReferences();
        getBundleData();
        setClickListeners();

        if (PopUtils.checkInternetConnection(this)) {
            requestForCityWS();
        } else {
            PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
        }
    }

    private void setReferences() {
        mTxtGetSetGo = findViewById(R.id.txtGetSetGo);
        mTxtTerms = findViewById(R.id.txtTerms);
        setSpannableString(mTxtTerms);

        mEdtName = findViewById(R.id.edtName);
        mEdtEmailId = findViewById(R.id.edtEmailId);
        mEdtCity = findViewById(R.id.edtCity);
        mEdtArea = findViewById(R.id.edtArea);
        mImgBack = findViewById(R.id.imgBack);

        edtReferalCode = findViewById(R.id.edtReferalCode);
        linReferalCode = findViewById(R.id.linReferalCode);

    }

    private void getBundleData() {
        Intent intent = getIntent();
        try {
            if (intent != null && intent.getExtras() != null) {
                mobile = intent.getExtras().getString("MOBILE");
                IS_ENABLE_REFERAL_CODE = intent.getExtras().getString("IS_ENABLE_REFERAL_CODE").equalsIgnoreCase("True");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            linReferalCode.setVisibility(IS_ENABLE_REFERAL_CODE ? View.VISIBLE : View.GONE);
        }
    }

    private void setSpannableString(TextView mTxtTerms) {
        SpannableString spannableString = new SpannableString("By submitting the information, you agree with our\nTerms and Conditions");
//        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 50, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                navigateActivity(new Intent(RegistrationActivity.this, TermsActivity.class), false);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan, 50, 70, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(1.2f), 50, 70, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 50, 70, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTxtTerms.setText(spannableString);
        mTxtTerms.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setClickListeners() {
        mTxtGetSetGo.setOnClickListener(this);
        mEdtCity.setOnClickListener(this);
        mEdtArea.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtGetSetGo:
                String message = checkValidation();
                if (!message.equalsIgnoreCase("")) {
                    PopUtils.alertDialog(RegistrationActivity.this, message, null);
                } else {
                    if (PopUtils.checkInternetConnection(this)) {
                        requestForRegistrationWS();
                    } else {
                        PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                    }
                }
                break;
            case R.id.imgBack:
                finish();
                break;
            case R.id.edtCity:
                if (PopUtils.checkInternetConnection(this)) {
                    requestForCityWS();
                } else {
                    PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                }
                break;
            case R.id.edtArea:
                if (!TextUtils.isEmpty(mEdtCity.getText().toString())) {
                    if (PopUtils.checkInternetConnection(this)) {
                        requestForAreaWS();
                    } else {
                        PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                    }
                } else {
                    Toast.makeText(this, "Please select city first", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private String checkValidation() {
        String message = "";
        if (TextUtils.isEmpty(mEdtName.getText().toString().trim())) {
            message = "Please enter your name";
        } else if (TextUtils.isEmpty(mEdtEmailId.getText().toString().trim())) {
            message = "Please enter your Email ID";
        } else if (!PopUtils.emailValidator(mEdtEmailId.getText().toString().trim())) {
            message = "Please enter valid email ID";
        } else if (TextUtils.isEmpty(mEdtCity.getText().toString().trim())) {
            message = "Please select your City";
        } else if (TextUtils.isEmpty(mEdtArea.getText().toString().trim())) {
            message = "Please select your Area";
        }
        return message;
    }

    private void requestForCityWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "cities");
            mJsonObject.put("loc_id", "");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_CITIES);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestForAreaWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "cities");
            mJsonObject.put("loc_id", cityId);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_AREAS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestForRegistrationWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "registration");
            mJsonObject.put("name", mEdtName.getText().toString().trim());
            mJsonObject.put("email", mEdtEmailId.getText().toString().trim());
            mJsonObject.put("phone_no", mobile);
            mJsonObject.put("city", cityId);
            mJsonObject.put("referal_code", IS_ENABLE_REFERAL_CODE ? edtReferalCode.getText().toString().trim() : "");
            mJsonObject.put("area", areaId);
            mJsonObject.put("token_id", UserDetails.getInstance(this).getPushToken());
            mJsonObject.put("device_id", Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
            mJsonObject.put("device_type", "Android");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_REGISTRATION);
        } catch (JSONException e) {
            e.printStackTrace();
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

    @Override
    public void SuccessResponse(String response, int requestCode) {
        hideLoadingDialog();
        switch (requestCode) {
            case WsUtils.WS_CODE_CITIES:
                responseForRequestForCities(response);
                break;
            case WsUtils.WS_CODE_AREAS:
                responseForRequestForAreas(response);
                break;
            case WsUtils.WS_CODE_REGISTRATION:
                responseForRegistration(response);
                break;
            default:
                break;
        }
    }

    private void responseForRequestForCities(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray mDataArray = mJsonObject.getJSONArray("data");

                    mDialogList.clear();
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);

                        mDialogList.add(new DialogList(mDataObject.optString("loc_id"), mDataObject.optString("location_name")));
                    }
                    PopUtils.alertDialogList(this, mDialogList, new DialogListInterface() {
                        @Override
                        public void DialogListInterface(String id, String value) {
                            cityId = id;
                            mEdtCity.setText(value);

                            if (PopUtils.checkInternetConnection(RegistrationActivity.this)) {
                                requestForAreaWS();
                            } else {
                                PopUtils.alertDialog(RegistrationActivity.this, getString(R.string.pls_check_internet), null);
                            }
                        }
                    }, "left");
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void responseForRequestForAreas(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray mDataArray = mJsonObject.getJSONArray("data");

                    mDialogList.clear();
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);

                        mDialogList.add(new DialogList(mDataObject.optString("loc_id"), mDataObject.optString("location_name")));
                    }

                    PopUtils.alertDialogList(this, mDialogList, new DialogListInterface() {
                        @Override
                        public void DialogListInterface(String id, String value) {
                            areaId = id;
                            mEdtArea.setText(value);
                        }
                    }, "left");
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void responseForRegistration(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
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
                    UserDetails.getInstance(this).setReferralCode(mDataObject.optString("referal_code"));
                    UserDetails.getInstance(this).setPaymentType(mDataObject.optString("payment_type"));
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

                    navigateActivity(new Intent(RegistrationActivity.this, HomeActivity.class), true);
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
