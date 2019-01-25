package com.godavarisandroid.mystore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.adapters.CategoriesAdapter;
import com.godavarisandroid.mystore.adapters.NextDeliveryAdapter;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.NextDelivery;
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

public class ModifyNextDeliveryActivity extends BaseActivity implements View.OnClickListener, IParseListener {
    private TextView mTxtSave, mTxtAddProduct;
    private RecyclerView mRecyclerView;
    private ImageView mImgLogo, mImgBack, mImgNotification;
    private TextView mTxtTitle;

    private static ArrayList<NextDelivery> mNextDelivery;
    private static String currentDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_next_delivery);

        initComponents();
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
        getIntentData();

        setAdapter();
    }

    /*getting data through intent*/
    private void getIntentData() {
        Intent intent = getIntent();
        currentDate = intent.getStringExtra("DATE");
        mNextDelivery = (ArrayList<NextDelivery>) getIntent().getSerializableExtra("ARRAYLIST");
    }

    /*Initializing Views*/
    private void setReferences() {
        mTxtSave = (TextView) findViewById(R.id.txtSave);
        mTxtAddProduct = (TextView) findViewById(R.id.txtAddProduct);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mImgLogo = (ImageView) findViewById(R.id.imgLogo);
        mImgLogo.setVisibility(View.GONE);

        mImgBack = (ImageView) findViewById(R.id.imgBack);
        mImgBack.setVisibility(View.VISIBLE);
        mImgNotification = (ImageView) findViewById(R.id.imgNotification);
        mImgNotification.setVisibility(View.VISIBLE);

        mTxtTitle = (TextView) findViewById(R.id.txtTitle);
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText("Modify Products");
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mTxtAddProduct.setOnClickListener(this);
        mTxtSave.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mImgNotification.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSave:
//                PopUtils.alertDialog(this, "Your modification request for delivery on " + currentDate + " is successful",
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
                /*Requesting for modify next delivery service call*/
                if (PopUtils.checkInternetConnection(ModifyNextDeliveryActivity.this)) {
                    requestForModifyNextDeliveryWS();
                } else {
                    PopUtils.alertDialog(ModifyNextDeliveryActivity.this, getString(R.string.pls_check_internet), null);
                }

//                                ((HomeActivity) getActivity()).onBackPressed();
//                            }
//                        });
                break;
            case R.id.txtAddProduct:
                /*Showing popup dialog for add product category*/
                PopUtils.alertAddProductCategory(ModifyNextDeliveryActivity.this, null);
                break;
            case R.id.imgBack:/*Called on back button press*/
                finish();
                break;
            case R.id.imgNotification:
                /*Navigating to help screen*/
                navigateActivity(new Intent(this, HelpActivity.class), false);
                break;
            default:
                break;
        }
    }

    /*Requesting for modify next delivery service call*/
    private void requestForModifyNextDeliveryWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(ModifyNextDeliveryActivity.this).getUserToken());

        JSONArray mIdsArray = new JSONArray();
        JSONArray mStatusArray = new JSONArray();
        JSONArray mQuantityArray = new JSONArray();
        for (int i = 0; i < mNextDelivery.size(); i++) {
            mIdsArray.put(mNextDelivery.get(i).sdId);
            mStatusArray.put(mNextDelivery.get(i).status);
            mQuantityArray.put(mNextDelivery.get(i).quantity);
        }

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "next_delivery_modify");
            mJsonObject.put("ids", mIdsArray);
            mJsonObject.put("status", mStatusArray);
            mJsonObject.put("quantity", mQuantityArray);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(ModifyNextDeliveryActivity.this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_MODIFY_NEXT_DELIVERY);
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
            case WsUtils.WS_CODE_MODIFY_NEXT_DELIVERY:
                responseForModifyNextDelivery(response);
                break;
            default:
                break;
        }
    }

    /*Response for modify next deivery service call*/
    private void responseForModifyNextDelivery(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                final String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    PopUtils.alertDialog(this, "Your modification request for delivery on " + currentDate + " is successful",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(ModifyNextDeliveryActivity.this, message, Toast.LENGTH_SHORT).show();
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("DATE", currentDate);
                                    setResult(Activity.RESULT_OK, returnIntent);
                                    finish();
                                }
                            });
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Set adapter for next delivery products*/
    private void setAdapter() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        NextDeliveryAdapter mNextDeliveryAdapter = new NextDeliveryAdapter(this, mNextDelivery, "Modify");
        mRecyclerView.setAdapter(mNextDeliveryAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new CategoriesAdapter(this, new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }
}
