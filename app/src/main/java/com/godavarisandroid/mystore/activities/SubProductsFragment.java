package com.godavarisandroid.mystore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.adapters.SubProductsTabAdapter;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.Brands;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.godavarisandroid.mystore.utils.UserDetails;
import com.godavarisandroid.mystore.views.SlidingTabLayout;
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

public class SubProductsFragment extends BaseActivity implements IParseListener, View.OnClickListener {
    private View view;
    private ImageView mImgLogo, mImgBack, mImgNotification;
    private TextView mTxtTitle;
    private SlidingTabLayout mSlidingLayoutFragment;
    private ViewPager mViewPager;

    private String cId = "", cName = "", from = "";
    private ArrayList<Brands> mBrandList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sub_products);

        initComponents();
    }

    private void initComponents() {
        setReferences();
        getBundleData();
        setClickListeners();

        if (PopUtils.checkInternetConnection(this)) {
            requestForBrandsWS();
        } else {
            PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
        }
    }

    private void setReferences() {
        mImgLogo = (ImageView) findViewById(R.id.imgLogo);
        mImgLogo.setVisibility(View.GONE);
        mImgBack = (ImageView) findViewById(R.id.imgBack);
        mImgBack.setVisibility(View.VISIBLE);
        mTxtTitle = (TextView) findViewById(R.id.txtTitle);
        mTxtTitle.setVisibility(View.VISIBLE);
        mImgNotification = (ImageView) findViewById(R.id.imgNotification);
        mImgNotification.setVisibility(View.VISIBLE);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mSlidingLayoutFragment = (SlidingTabLayout) findViewById(R.id.tabLayout);
    }

    private void getBundleData() {
        Intent intent = getIntent();
        if (intent != null) {
            cId = intent.getStringExtra("c_id");
            cName = intent.getStringExtra("c_name");
            from = intent.getStringExtra("from");
            mTxtTitle.setText(cName);
        }
    }

    private void setClickListeners() {
        mImgBack.setOnClickListener(this);
        mImgNotification.setOnClickListener(this);
    }

    private void requestForBrandsWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "brands");
            mJsonObject.put("cid", cId);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_BRANDS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ErrorResponse(String response, int requestCode) {
        hideLoadingDialog();
        PopUtils.alertDialog(this, response.toString(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void SuccessResponse(String response, int requestCode) {
        hideLoadingDialog();
        switch (requestCode) {
            case WsUtils.WS_CODE_BRANDS:
                responseForBrands(response);
                break;
            default:
                break;
        }
    }

    private void responseForBrands(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray mDataArray = mJsonObject.getJSONArray("data");

                    mBrandList.add(new Brands("", "All", cId, cName));
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);

                        mBrandList.add(new Brands(mDataObject.optString("b_id"),
                                mDataObject.optString("brand_name"), cId, cName));
                    }

                    setAdapter();
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setAdapter() {
        SubProductsTabAdapter deliveryAddressTabAdapter = new SubProductsTabAdapter(this, getSupportFragmentManager(),
                mBrandList.size(), mBrandList, cId, from);
        mViewPager.setAdapter(deliveryAddressTabAdapter);
        mSlidingLayoutFragment.setViewPager(mViewPager);
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
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
}
