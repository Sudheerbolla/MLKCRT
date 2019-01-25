package com.godavarisandroid.mystore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.adapters.CategoriesAdapter;
import com.godavarisandroid.mystore.fragments.HomeFragment;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.Categories;
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
 * Created by UMA on 5/26/2018.
 */
public class CategoriesActivity extends BaseActivity implements IParseListener, View.OnClickListener {
    private RecyclerView mRecyclerView;
    private TextView mTxtContent, mTxtTitle;
    private ImageView mImgBack, mImgLogo, mImgNotification;
    private static String currentDate = "", from = "";

    private ArrayList<Categories> mCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        initComponents();
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
        getIntentData();

        if (PopUtils.checkInternetConnection(this)) {
            requestForCategoriesWS();
        } else {
            PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
        }
    }

    /*getting data through intent*/
    private void getIntentData() {
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        currentDate = intent.getStringExtra("DATE");
        from = intent.getStringExtra("from");
    }

    /*Requesting service call for categories*/
    private void requestForCategoriesWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "categorys");
            mJsonObject.put("cid", "0");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_CATEGORIES);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Initializing Views*/
    private void setReferences() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mTxtContent = (TextView) findViewById(R.id.txtContent);
        mTxtTitle = (TextView) findViewById(R.id.txtTitle);
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText("Categories");
        mImgBack = (ImageView) findViewById(R.id.imgBack);
        mImgBack.setVisibility(View.VISIBLE);
        mImgLogo = (ImageView) findViewById(R.id.imgLogo);
        mImgLogo.setVisibility(View.GONE);
        mImgNotification = (ImageView) findViewById(R.id.imgNotification);
        mImgNotification.setVisibility(View.VISIBLE);
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mImgBack.setOnClickListener(this);
        mImgNotification.setOnClickListener(this);
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
            case WsUtils.WS_CODE_CATEGORIES:
                responseForCategories(response);
                break;
            default:
                break;
        }
    }

    /*Response from categories service call*/
    private void responseForCategories(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mTxtContent.setVisibility(View.GONE);

                    JSONArray mDataArray = mJsonObject.getJSONArray("data");
                    mCategories.clear();
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);

                        mCategories.add(new Categories(mDataObject.optString("cid"),
                                mDataObject.optString("cat_name"),
                                mDataObject.optString("cat_image"),
                                mDataObject.optString("image_path"), null));
                    }
                    setAdapter();
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    mTxtContent.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Set categories adapter*/
    private void setAdapter() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        CategoriesAdapter mCategoriesAdapter = new CategoriesAdapter(this, mCategories, new HomeFragment(), from);
        mRecyclerView.setAdapter(mCategoriesAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new CategoriesAdapter(this, new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
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
        Intent returnIntent = new Intent();
        returnIntent.putExtra("DATE", currentDate);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
