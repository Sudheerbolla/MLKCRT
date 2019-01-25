package com.godavarisandroid.mystore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.adapters.PaymentHistoryAdapter;
import com.godavarisandroid.mystore.adapters.UsageHistoryAdapter;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.PaymentHistory;
import com.godavarisandroid.mystore.models.UsageHistory;
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

/**
 * Created by Excentd11 on 4/27/2018.
 */

public class WalletActivity extends BaseActivity implements View.OnClickListener, IParseListener {
    private NestedScrollView mSVContent;
    private RecyclerView mRVUsageHistory, mRVPaymentHistory;
    private TextView mTxtTitle, mTxtUsageHistory, mTxtPaymentHistory, mTxtWalletValue, mTxtUsageContent,
            mTxtPaymentContent;
    private ImageView mImgLogo, mImgBack, mImgNotification;
    private RelativeLayout mRlNoData;

    ArrayList<PaymentHistory> mPaymentHistory = new ArrayList<>();
    ArrayList<UsageHistory> mUsageHistory = new ArrayList<>();

    private boolean isUsageClicked = false, isPaymentSelected = false;
    private String walletAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        initComponents();
    }

    private void initComponents() {
        setReferences();
        setClickListeners();

        /*Requesting for wallet service call*/
        if (PopUtils.checkInternetConnection(this)) {
            requestForWalletWS();
        } else {
            PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
        }
    }

    /*Initializing Views*/
    private void setReferences() {
        mSVContent = (NestedScrollView) findViewById(R.id.sVContent);
        mRVUsageHistory = (RecyclerView) findViewById(R.id.rVUsageHistory);
        mRVPaymentHistory = (RecyclerView) findViewById(R.id.rVPaymentHistory);
        mTxtTitle = (TextView) findViewById(R.id.txtTitle);
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText("Usage and Payment");

        mTxtUsageHistory = (TextView) findViewById(R.id.txtUsageHistory);
        mTxtPaymentHistory = (TextView) findViewById(R.id.txtPaymentHistory);
        mTxtWalletValue = (TextView) findViewById(R.id.txtWalletValue);
        mTxtUsageContent = (TextView) findViewById(R.id.txtUsageContent);
        mTxtPaymentContent = (TextView) findViewById(R.id.txtPaymentContent);

        mImgLogo = (ImageView) findViewById(R.id.imgLogo);
        mImgLogo.setVisibility(View.GONE);

        mImgBack = (ImageView) findViewById(R.id.imgBack);
        mImgBack.setVisibility(View.VISIBLE);

        mImgNotification = (ImageView) findViewById(R.id.imgNotification);
        mImgNotification.setVisibility(View.VISIBLE);

        mRlNoData = (RelativeLayout) findViewById(R.id.rlNoData);
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mImgLogo.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mTxtUsageHistory.setOnClickListener(this);
        mTxtPaymentHistory.setOnClickListener(this);
        mImgNotification.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.txtUsageHistory:
                if (isUsageClicked) {
                    mTxtUsageHistory.setCompoundDrawablesWithIntrinsicBounds(null, null, ActivityCompat.getDrawable(this, R.drawable.img_drop_down), null);
                    mRVUsageHistory.setVisibility(View.GONE);
                    mTxtUsageContent.setVisibility(View.GONE);
                    isUsageClicked = false;
                } else {
                    mTxtUsageHistory.setCompoundDrawablesWithIntrinsicBounds(null, null, ActivityCompat.getDrawable(this, R.drawable.img_drop_up), null);
                    mTxtPaymentHistory.setCompoundDrawablesWithIntrinsicBounds(null, null, ActivityCompat.getDrawable(this, R.drawable.img_drop_down), null);
                    if (mUsageHistory.size() > 0) {
                        mTxtUsageContent.setVisibility(View.GONE);
                        mRVUsageHistory.setVisibility(View.VISIBLE);
                    } else {
                        mTxtUsageContent.setVisibility(View.VISIBLE);
                        mRVUsageHistory.setVisibility(View.GONE);
                    }
                    mRVPaymentHistory.setVisibility(View.GONE);
                    mTxtPaymentContent.setVisibility(View.GONE);
                    isUsageClicked = true;
                    isPaymentSelected = false;
                }
                break;
            case R.id.txtPaymentHistory:
                if (isPaymentSelected) {
                    mTxtPaymentHistory.setCompoundDrawablesWithIntrinsicBounds(null, null, ActivityCompat.getDrawable(this, R.drawable.img_drop_down), null);
                    mRVPaymentHistory.setVisibility(View.GONE);
                    mTxtPaymentContent.setVisibility(View.GONE);
                    isPaymentSelected = false;
                } else {
                    mTxtPaymentHistory.setCompoundDrawablesWithIntrinsicBounds(null, null, ActivityCompat.getDrawable(this, R.drawable.img_drop_up), null);
                    mTxtUsageHistory.setCompoundDrawablesWithIntrinsicBounds(null, null, ActivityCompat.getDrawable(this, R.drawable.img_drop_down), null);
                    if (mPaymentHistory.size() > 0) {
                        mRVPaymentHistory.setVisibility(View.VISIBLE);
                        mTxtPaymentContent.setVisibility(View.GONE);
                    } else {
                        mRVPaymentHistory.setVisibility(View.GONE);
                        mTxtPaymentContent.setVisibility(View.VISIBLE);
                    }
                    mRVUsageHistory.setVisibility(View.GONE);
                    mTxtUsageContent.setVisibility(View.GONE);
                    isPaymentSelected = true;
                    isUsageClicked = false;
                }
                break;
            case R.id.imgNotification:
                /*Navigate to help screen*/
                navigateActivity(new Intent(this, HelpActivity.class), false);
                break;
            default:
                break;
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

    /*Called on back button press*/
    @Override
    public void onBackPressed() {
        finish();
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
            case WsUtils.WS_CODE_WALLET:
                responseForWallet(response);
                break;
            default:
                break;
        }
    }

    /*Respons for wallet service call*/
    private void responseForWallet(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    mSVContent.setVisibility(View.VISIBLE);
                    mRlNoData.setVisibility(View.GONE);

                    JSONObject mWalletValueObject = mJsonObject.getJSONObject("vallet_value");
                    walletAmount = mWalletValueObject.optString("amount");
                    mTxtWalletValue.setText("₹ " + walletAmount);

                    BaseApplication.rechargeAmount = mJsonObject.optString("recharge_amount");
                    BaseApplication.referralAmount = mJsonObject.optString("referal_amount");
                    BaseApplication.walletAmount = walletAmount;

                    JSONArray mSixPaymentsArray = mJsonObject.getJSONArray("last_6_paments");
                    mPaymentHistory.clear();
                    for (int i = 0; i < mSixPaymentsArray.length(); i++) {
                        JSONObject mSixPaymentObject = mSixPaymentsArray.getJSONObject(i);
                        mPaymentHistory.add(new PaymentHistory(
                                mSixPaymentObject.optString("u_pay_id"),
                                mSixPaymentObject.optString("amount"),
                                mSixPaymentObject.optString("date"),
                                mSixPaymentObject.optString("payment_type"),
                                mSixPaymentObject.optString("recept_number")));
                    }
                    setPaymentHistoryAdapter();

                    JSONArray mFourPaymentsArray = mJsonObject.getJSONArray("last_four_months");
//                    if (mFourPaymentsArray.length() > 0) {
//                        JSONObject lastMonthObject = mFourPaymentsArray.optJSONObject(mFourPaymentsArray.length() - 1);
//                        if (lastMonthObject != null)
//                            BaseApplication.lastMonthDue = lastMonthObject.optString("amount");
//                        else BaseApplication.lastMonthDue = "0";
//                    } else BaseApplication.lastMonthDue = "0";

//                    BaseApplication.totalAmountDue = mJsonObject.getJSONObject("tot_due_amount").optString("amount");
                    BaseApplication.totalAmountDue = mJsonObject.optString("tot_due_amount");
                    BaseApplication.lastMonthDue = mJsonObject.optString("last_month_due");

                    mUsageHistory.clear();
                    for (int i = 0; i < mFourPaymentsArray.length(); i++) {
                        JSONObject mFourPaymentObject = mFourPaymentsArray.getJSONObject(i);
                        mUsageHistory.add(new UsageHistory(mFourPaymentObject.optString("amount"),
                                mFourPaymentObject.optString("month"),
                                mFourPaymentObject.optString("year"),
                                mFourPaymentObject.optString("date")));
                    }
                    setUsageHistoryAdapter();

                } else {
                    mSVContent.setVisibility(View.GONE);
                    mRlNoData.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Set payment adapter adapter*/
    private void setPaymentHistoryAdapter() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        PaymentHistoryAdapter mPaymentHistoryAdapter = new PaymentHistoryAdapter(this, mPaymentHistory);
        mRVPaymentHistory.setAdapter(mPaymentHistoryAdapter);
        mRVPaymentHistory.setLayoutManager(mLinearLayoutManager);
        mRVPaymentHistory.addOnItemTouchListener(new PaymentHistoryAdapter(this, new PaymentHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }

    /*Set usage history adapter*/
    private void setUsageHistoryAdapter() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        UsageHistoryAdapter mUsageHistoryAdapter = new UsageHistoryAdapter(this, mUsageHistory);
        mRVUsageHistory.setAdapter(mUsageHistoryAdapter);
        mRVUsageHistory.setLayoutManager(mLinearLayoutManager);
        mRVUsageHistory.addOnItemTouchListener(new UsageHistoryAdapter(this, new UsageHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }
}
