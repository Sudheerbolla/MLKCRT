package com.godavarisandroid.mystore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.godavarisandroid.mystore.utils.UserDetails;
import com.godavarisandroid.mystore.webUtils.ServerResponse;
import com.godavarisandroid.mystore.webUtils.WebServices;
import com.godavarisandroid.mystore.webUtils.WsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Excentd11 on 4/27/2018.
 */

public class ReferAndEarnActivity extends BaseActivity implements View.OnClickListener, IParseListener {

    private TextView mTxtTitle, txtReferralCode, txtInviteFriend, txtReferralPoints, txtReferralMessage;
    private ImageView mImgBack;
    private String referralAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_and_earn);
        initComponents();
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
        txtReferralCode.setText(UserDetails.getInstance(this).getReferalCode());
        if (PopUtils.checkInternetConnection(this)) {
            requestForWalletWS();
            requestForMessageService();
        } else {
            PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
        }
    }

    /*Initializing Views*/
    private void setReferences() {
        txtReferralCode = findViewById(R.id.txtReferralCode);
        txtInviteFriend = findViewById(R.id.txtInviteFriend);
        txtReferralPoints = findViewById(R.id.txtReferralPoints);
        txtReferralMessage = findViewById(R.id.txtReferralMessage);
        mTxtTitle = findViewById(R.id.txtTitle);
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText("Refer And Earn");

        mImgBack = findViewById(R.id.imgBack);
        mImgBack.setVisibility(View.VISIBLE);

        findViewById(R.id.imgLogo).setVisibility(View.GONE);
        findViewById(R.id.imgNotification).setVisibility(View.GONE);

    }

    /*Click listeners for views*/
    private void setClickListeners() {
        txtInviteFriend.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.txtInviteFriend:
                openShareIntent();
                break;
            default:
                break;
        }
    }

    private void openShareIntent() {

//        String text = "Yes, it’s true! MilkCart is giving Rs. 200 in your account on your first payment. I have been using it for milk and my daily grocery delivery and it has really made my life easy. Download MilkCart to be eligible to get 200 in your wallet.";
//        String code = txtReferralCode.getText().toString().trim();
//        if (!TextUtils.isEmpty(code)) {
//            text = "Yes, it’s true! MilkCart is giving Rs. 200 in your account on your first payment. I have been using it for milk and my daily grocery delivery and it has really made my life easy. Download MilkCart and use referral code " + code + " to be eligible to get 200 in your wallet.";
//        }
        String text = txtReferralMessage.getText().toString().trim();
//        String code = txtReferralCode.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(shareIntent, "Share Referral Code using"));
        } else {
            text = "Yes, it’s true! MilkCart is giving Rs. 200 in your account on your first payment. I have been using it for milk and my daily grocery delivery and it has really made my life easy. Download MilkCart to be eligible to get 200 in your wallet.";
            String code = txtReferralCode.getText().toString().trim();
            if (!TextUtils.isEmpty(code)) {
                text = "Yes, it’s true! MilkCart is giving Rs. 200 in your account on your first payment. I have been using it for milk and my daily grocery delivery and it has really made my life easy. Download MilkCart and use referral code " + code + " to be eligible to get 200 in your wallet.";
            }
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(shareIntent, "Share Referral Code using"));
        }
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
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_WALLET);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void requestForMessageService() {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("Token", UserDetails.getInstance(this).getUserToken());

//        JSONObject mJsonObject = new JSONObject();
        try {
//            mJsonObject.put("action", "refaral_message");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("?action=refaral_message");

            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestStringBuilder(this, WebServices.BASE_URL, stringBuilder, this, WsUtils.WS_CODE_REFERRAL_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Called on back button press*/
    @Override
    public void onBackPressed() {
        finish();
    }


    /*Called when error occured from service call*/
    @Override
    public void ErrorResponse(String response, int requestCode) {
        hideLoadingDialog();
        PopUtils.alertDialog(this, response, null);
    }

    /*Called when success occured from service call*/
    @Override
    public void SuccessResponse(String response, int requestCode) {
        hideLoadingDialog();
        switch (requestCode) {
            case WsUtils.WS_CODE_WALLET:
                responseForWallet(response);
                break;
            case WsUtils.WS_CODE_REFERRAL_MESSAGE:
                responseForReferralMessage(response);
                break;
            default:
                break;
        }
    }

    //"data":[{"id":"1","type":"MSG","mssage":"message","updated_on":"2018-10-06 00:00:00"}]

    private void responseForReferralMessage(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    txtReferralMessage.setText(mJsonObject.getJSONArray("data").getJSONObject(0).getString("mssage"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Response for wallet service call
    //{"status":200,"last_four_months":[],"last_6_paments":[],"vallet_value":{"amount":0},"recharge_amount":0,"referal_amount":0}
    */

    private void responseForWallet(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    referralAmount = mJsonObject.optString("referal_amount");
//                    txtReferralPoints.setText("₹ " + referralAmount);
                    txtReferralPoints.setText(referralAmount);

                    JSONObject mWalletValueObject = mJsonObject.getJSONObject("vallet_value");
                    String walletAmount = mWalletValueObject.optString("amount");

                    BaseApplication.rechargeAmount = mJsonObject.optString("recharge_amount");
                    BaseApplication.referralAmount = referralAmount;
                    BaseApplication.walletAmount = walletAmount;

//                    JSONArray lastMonthsArray = mJsonObject.optJSONArray("last_four_months");
//                    if (lastMonthsArray.length() > 0) {
//                        JSONObject lastMonthObject = lastMonthsArray.optJSONObject(lastMonthsArray.length() - 1);
//                        if (lastMonthObject != null)
//                            BaseApplication.lastMonthDue = lastMonthObject.optString("amount");
//                        else BaseApplication.lastMonthDue = "0";
//                    } else BaseApplication.lastMonthDue = "0";

//                    BaseApplication.totalAmountDue = mJsonObject.getJSONObject("tot_due_amount").optString("amount");
                    BaseApplication.totalAmountDue = mJsonObject.optString("tot_due_amount");
                    BaseApplication.lastMonthDue = mJsonObject.optString("last_month_due");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
