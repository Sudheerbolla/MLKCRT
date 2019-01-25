package com.godavarisandroid.mystore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.fragments.CalenderFragmentNew;
import com.godavarisandroid.mystore.fragments.HomeFragment;
import com.godavarisandroid.mystore.fragments.SettingsFragment;
import com.godavarisandroid.mystore.fragments.SubscriptionsFragment;
import com.godavarisandroid.mystore.utils.UserDetails;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    public LinearLayout mLlBottom;
    private RelativeLayout mRlProducts, mRlMyCalender, mRlMe, mRlSubscriptions, relRecharge;
    public ImageView mImgLogo, mImgBack, mImgHelp, imgNotification;
    public TextView mTxtTitle, txtRecharge, txtRechargeMessage;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initComponents();
    }

    private void initComponents() {
        setReferences();
        setClickListeners();

        mRlProducts.setSelected(true);
        mRlSubscriptions.setSelected(false);
        mRlMyCalender.setSelected(false);
        mRlMe.setSelected(false);

        getIntentData();
    }

    private void setReferences() {
        mLlBottom = findViewById(R.id.llBottom);
        mRlProducts = findViewById(R.id.rlProducts);
        mRlSubscriptions = findViewById(R.id.rlSubscriptions);
        mRlMyCalender = findViewById(R.id.rlMyCalender);
        mRlMe = findViewById(R.id.rlMe);

        mImgLogo = findViewById(R.id.imgLogo);
        mImgBack = findViewById(R.id.imgBack);
        mImgHelp = findViewById(R.id.imgHelp);
        imgNotification = findViewById(R.id.imgNotification);
        mImgHelp.setVisibility(View.VISIBLE);

        mTxtTitle = findViewById(R.id.txtTitle);
        txtRecharge = findViewById(R.id.txtRecharge);
        txtRechargeMessage = findViewById(R.id.txtRechargeMessage);
        relRecharge = findViewById(R.id.relRecharge);

        setRechargeBannerData();
    }

    public void setRechargeBannerData() {
        try {
            txtRecharge.setText(UserDetails.getInstance(this).getPaymentType().equalsIgnoreCase("PrePaid") ? "Recharge" : "PayNow");
            if (!TextUtils.isEmpty(BaseApplication.rechargeMessage))
                txtRechargeMessage.setText(BaseApplication.rechargeMessage);
            if (!TextUtils.isEmpty(BaseApplication.walletAmount)) {
                if (Double.parseDouble(BaseApplication.walletAmount) <= 0.0 || UserDetails.getInstance(this).getIsEnableRechargeButton()) {
                    relRecharge.setVisibility(View.VISIBLE);
                } else relRecharge.setVisibility(View.GONE);
            } else
                relRecharge.setVisibility(UserDetails.getInstance(this).getIsEnableRechargeButton() ? View.VISIBLE : View.GONE);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void setClickListeners() {
        mRlProducts.setOnClickListener(this);
        mRlSubscriptions.setOnClickListener(this);
        mRlMyCalender.setOnClickListener(this);
        mRlMe.setOnClickListener(this);
        mImgLogo.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mImgHelp.setOnClickListener(this);
        imgNotification.setOnClickListener(this);
        txtRecharge.setOnClickListener(this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("TAB")) {
            if (intent.getStringExtra("TAB").equalsIgnoreCase("3")) {
                mRlProducts.setSelected(false);
                mRlSubscriptions.setSelected(true);
                mRlMyCalender.setSelected(false);
                mRlMe.setSelected(false);
                Bundle bundle = new Bundle();
//                navigateFragment(new NextDeliveryFragment(), "NEXTDELIVERYFRAGMENT", bundle, this);
                navigateFragment(new SubscriptionsFragment(), "SUBSCRIPTIONSFRAGMENT", bundle, this);
            } else if (intent.getStringExtra("TAB").equalsIgnoreCase("4")) {
                mRlProducts.setSelected(false);
                mRlSubscriptions.setSelected(false);
                mRlMyCalender.setSelected(true);
                mRlMe.setSelected(false);
                Bundle bundle1 = new Bundle();
                bundle1.putString("selectedDate", intent.getStringExtra("selectedDate"));
                CalenderFragmentNew fragment = new CalenderFragmentNew();
                fragment.setArguments(bundle1);
                navigateFragment(fragment, "CALENDERFRAGMENT", bundle1, this);
            }
        } else {
            setHomeFragment();
        }
    }

    private void setHomeFragment() {
        deSelectAll();
        mRlProducts.setSelected(true);
        navigateFragment(new HomeFragment(), "HOMEFRAGMENT", new Bundle(), this);
    }

    private void deSelectAll() {
        mRlProducts.setSelected(false);
        mRlSubscriptions.setSelected(false);
        mRlMyCalender.setSelected(false);
        mRlMe.setSelected(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlProducts:
                setHomeFragment();
                break;
            case R.id.rlSubscriptions:
                deSelectAll();
                mRlSubscriptions.setSelected(true);
                navigateFragment(new SubscriptionsFragment(), "SUBSCRIPTIONSFRAGMENT", new Bundle(), this);
                break;
            case R.id.rlMyCalender:
                deSelectAll();
                mRlMyCalender.setSelected(true);
                Bundle bundle1 = new Bundle();
                CalenderFragmentNew calenderFragmentNew = new CalenderFragmentNew();
                calenderFragmentNew.setArguments(bundle1);
                navigateFragment(calenderFragmentNew, "CALENDERFRAGMENT", bundle1, this);
                break;
            case R.id.rlMe:
                deSelectAll();
                mRlMe.setSelected(true);
                navigateFragment(new SettingsFragment(), "SETTINGSRFRAGMENT", new Bundle(), this);
                break;
            case R.id.imgLogo:
                setHomeFragment();
                break;
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.imgHelp:
                navigateActivity(new Intent(this, HelpActivity.class), false);
                break;
            case R.id.imgNotification:
                navigateActivity(new Intent(this, NotificationsActivity.class), false);
                break;
            case R.id.txtRecharge:
                navigateActivity(new Intent(this, SchedulePaymentPickUpActivity.class), false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
            String tagName = backEntry.getName();
            switch (tagName) {
                case "HOMEFRAGMENT":
                    mRlProducts.setSelected(true);
                    mRlSubscriptions.setSelected(false);
                    mRlMyCalender.setSelected(false);
                    mRlMe.setSelected(false);
                    exitHomeScreen();
                    break;
                case "SUBSCRIPTIONSFRAGMENT":
                case "CALENDERFRAGMENT":
                case "SETTINGSRFRAGMENT":
                    setHomeFragment();
                    break;
                default:
                    super.onBackPressed();
                    break;
            }
        }
    }

    public void exitHomeScreen() {
        //*to exit the screen*//*
        if (doubleBackToExitPressedOnce) {
            HomeActivity.this.finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to close MilkCart", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void finishApplication() {
        HomeActivity.this.finishAffinity();
    }
}
