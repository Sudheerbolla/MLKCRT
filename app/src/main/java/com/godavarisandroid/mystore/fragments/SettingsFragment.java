package com.godavarisandroid.mystore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.FeedbackAcivity;
import com.godavarisandroid.mystore.activities.HomeActivity;
import com.godavarisandroid.mystore.activities.LoginActivity;
import com.godavarisandroid.mystore.activities.ReferAndEarnActivity;
import com.godavarisandroid.mystore.activities.SchedulePaymentPickUpActivity;
import com.godavarisandroid.mystore.activities.WalletActivity;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.godavarisandroid.mystore.utils.UserDetails;
import com.godavarisandroid.mystore.webUtils.ServerResponse;
import com.godavarisandroid.mystore.webUtils.WebServices;
import com.godavarisandroid.mystore.webUtils.WsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SettingsFragment extends BaseFragment implements View.OnClickListener, IParseListener {
    private View view;
    private TextView mTxtProfileInformation, mTxtDeliveryHistory, mTxtWallet, mTxtFeedback, mTxtLogout, txtReferAndEarn,
            txtRechargeAmount, txtReferralAmount, txtWalletCredits, txtRecharge;
    private LinearLayout linWalletCredits, linWalletDetails;
    private ImageView imgDropDown;
    private Bundle bundle;
    private Intent mIntent;
    private HomeActivity homeActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeActivity = (HomeActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        homeActivity.mImgLogo.setVisibility(View.GONE);
        homeActivity.mImgBack.setVisibility(View.VISIBLE);
        homeActivity.mImgHelp.setVisibility(View.VISIBLE);
        homeActivity.mTxtTitle.setVisibility(View.VISIBLE);
        homeActivity.mTxtTitle.setText("My Details");

        initComponents();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PopUtils.checkInternetConnection(getActivity())) {
                    requestForWalletWS();
                } else {
                    PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
                }
            }
        }, 50);
    }

    private void initComponents() {
        setReferences();
        setClickListeners();

        if (UserDetails.getInstance(getActivity()).getIsReferralCodeEnabled()) {
            txtReferAndEarn.setVisibility(View.VISIBLE);
            view.findViewById(R.id.viewRef).setVisibility(View.VISIBLE);
        } else {
            txtReferAndEarn.setVisibility(View.GONE);
            view.findViewById(R.id.viewRef).setVisibility(View.GONE);
        }
    }

    private void setWalletValues() {
        try {
            txtRecharge.setText(UserDetails.getInstance(getActivity()).getPaymentType().equalsIgnoreCase("PrePaid") ? "Recharge" : "PayNow");
            txtRechargeAmount.setText(BaseApplication.rechargeAmount);
            txtReferralAmount.setText(BaseApplication.referralAmount);
            txtWalletCredits.setText("" + BaseApplication.walletAmount);
            homeActivity.setRechargeBannerData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Called when requesting fro wallet service call*/
    private void requestForWalletWS() {
        homeActivity.showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(getActivity()).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "wallet_amount");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(getActivity(), "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_WALLET);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Initializing Views*/
    private void setReferences() {
        mTxtProfileInformation = view.findViewById(R.id.txtProfileInformation);
        mTxtDeliveryHistory = view.findViewById(R.id.txtDeliveryHistory);
        mTxtWallet = view.findViewById(R.id.txtWallet);
        mTxtFeedback = view.findViewById(R.id.txtFeedback);
        mTxtLogout = view.findViewById(R.id.txtLogout);
        txtReferAndEarn = view.findViewById(R.id.txtReferAndEarn);
        txtRecharge = view.findViewById(R.id.txtRecharge);

        txtRechargeAmount = view.findViewById(R.id.txtRechargeAmount);
        txtReferralAmount = view.findViewById(R.id.txtReferralAmount);
        txtWalletCredits = view.findViewById(R.id.txtWalletCredits);
        linWalletCredits = view.findViewById(R.id.linWalletCredits);
        linWalletDetails = view.findViewById(R.id.linWalletDetails);

        imgDropDown = view.findViewById(R.id.imgDropDown);

    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mTxtProfileInformation.setOnClickListener(this);
        mTxtDeliveryHistory.setOnClickListener(this);
        mTxtWallet.setOnClickListener(this);
        mTxtFeedback.setOnClickListener(this);
        mTxtLogout.setOnClickListener(this);
        txtReferAndEarn.setOnClickListener(this);

//        linWalletCredits.setOnClickListener(this);
        txtRecharge.setOnClickListener(this);
//        txtWalletCredits.setOnClickListener(this);
//        imgDropDown.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtProfileInformation:
                /*Navigating to profile information screen*/
                bundle = new Bundle();
                navigateFragment(new ProfileInformationFragment(), "PROFILEINFORMATIONFRAGMENT", bundle, getActivity());
                break;
            case R.id.txtDeliveryHistory:
                /*Navigating to delivery history screen*/
                bundle = new Bundle();
                navigateFragment(new DeliveryHistoryFragment(), "DELIVERYHISTORYFRAGMENT", bundle, getActivity());
                break;
            case R.id.txtReferAndEarn:
                mIntent = new Intent(getActivity(), ReferAndEarnActivity.class);
                startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case R.id.txtWallet:
                /*Navigating to wallet screen*/
                mIntent = new Intent(getActivity(), WalletActivity.class);
                startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case R.id.txtFeedback:
                /*Navigating to feedback screen*/
                mIntent = new Intent(getActivity(), FeedbackAcivity.class);
                startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case R.id.txtLogout:
                /*Called when click on logout button*/
                PopUtils.alertTwoButtonDialog(getActivity(), "Are you sure you want to logout",
                        "YES", "NO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*Navigating to login screen*/
                                UserDetails.getInstance(getActivity()).setUserId("");
                                Intent intnet = new Intent(getActivity(), LoginActivity.class);
                                BaseApplication.lastMonthDue = "0";
                                BaseApplication.walletAmount = "0";
                                BaseApplication.referralAmount = "0";
                                BaseApplication.rechargeAmount = "0";
                                BaseApplication.totalAmountDue = "0";
                                startActivity(intnet);
                                getActivity().finish();
                                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
//                                homeActivity.finishApplication();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                break;
            case R.id.linWalletCredits:
            case R.id.imgDropDown:
            case R.id.txtWalletCredits:
//                if (linWalletDetails.getVisibility() == View.VISIBLE) {
//                    imgDropDown.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.img_drop_down));
//                    linWalletDetails.setVisibility(View.GONE);
//                } else {
//                    imgDropDown.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.img_drop_up));
//                    linWalletDetails.setVisibility(View.VISIBLE);
//                }
                break;
            case R.id.txtRecharge:
                mIntent = new Intent(getActivity(), SchedulePaymentPickUpActivity.class);
                startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            default:
                break;
        }
    }

    @Override
    public void ErrorResponse(String response, int requestCode) {
        homeActivity.hideLoadingDialog();
        PopUtils.alertDialog(getActivity(), response, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void SuccessResponse(String response, int requestCode) {
        homeActivity.hideLoadingDialog();
        switch (requestCode) {
            case WsUtils.WS_CODE_WALLET:
                responseForWallet(response);
                break;
            default:
                break;
        }
    }

    private void responseForWallet(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    String referralAmount = mJsonObject.optString("referal_amount");
                    String rechargeAmount = mJsonObject.optString("recharge_amount");

                    JSONObject mWalletValueObject = mJsonObject.optJSONObject("vallet_value");
                    String walletAmount = mWalletValueObject.optString("amount");

                    BaseApplication.rechargeAmount = rechargeAmount;
                    BaseApplication.referralAmount = referralAmount;
                    BaseApplication.walletAmount = walletAmount;
//                    BaseApplication.totalAmountDue = mJsonObject.getJSONObject("tot_due_amount").optString("amount");
                    BaseApplication.totalAmountDue = mJsonObject.optString("tot_due_amount");
                    BaseApplication.lastMonthDue = mJsonObject.optString("last_month_due");
//                    JSONArray lastMonthsArray = mJsonObject.optJSONArray("last_four_months");
//                    if (lastMonthsArray.length() > 0) {
//                        JSONObject lastMonthObject = lastMonthsArray.optJSONObject(lastMonthsArray.length() - 1);
//                        if (lastMonthObject != null)
//                            BaseApplication.lastMonthDue = lastMonthObject.optString("amount");
//                        else BaseApplication.lastMonthDue = "0";
//                    } else BaseApplication.lastMonthDue = "0";
                    setWalletValues();
                } else Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
