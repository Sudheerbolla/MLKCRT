package com.godavarisandroid.mystore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.CategoriesActivity;
import com.godavarisandroid.mystore.activities.HomeActivity;
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
 * Created by UMA on 4/21/2018.
 */

public class NextDeliveryFragment extends BaseFragment implements View.OnClickListener, IParseListener {
    private View view;

    private TextView mTxtDate, mTxtModify, mTxtAddProduct;
    private LinearLayout mLlData;
    private RelativeLayout mRlNoData;
    private RecyclerView mRecyclerView;
    private NestedScrollView mNestedScrollView;

    private ArrayList<NextDelivery> mNextDelivery = new ArrayList<>();
    private String date = "";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_next_delivery, container, false);

        ((HomeActivity) getActivity()).mImgLogo.setVisibility(View.GONE);
        ((HomeActivity) getActivity()).mImgBack.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgHelp.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mTxtTitle.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mTxtTitle.setText("Next Delivery");

        initComponents();
        return view;
    }

    private void initComponents() {
        setReferences();
        setClickListeners();

        /*Requesting for next delivery service call*/
        if (PopUtils.checkInternetConnection(getActivity())) {
            requestForNextDeliveryWS();
        } else {
            PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
        }
    }

    /*Initializing Views*/
    private void setReferences() {
        mTxtDate = (TextView) view.findViewById(R.id.txtDate);
        mTxtModify = (TextView) view.findViewById(R.id.txtModify);
        mTxtAddProduct = (TextView) view.findViewById(R.id.txtAddProduct);
        mLlData = (LinearLayout) view.findViewById(R.id.llData);
        mRlNoData = (RelativeLayout) view.findViewById(R.id.rlNoData);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mNestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mTxtModify.setOnClickListener(this);
        mTxtAddProduct.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtModify:
                if (PopUtils.checkDateTimeCondition(getContext(), date)) {
                    PopUtils.alertDialog(getActivity(), "Oops!! Your request for tomorrow cant be proccessed as, cut off time is crossed. Please try with a future date.", null);
                } else {
                    /*Navigating to modify next delivery screen*/
                    Bundle bundle = new Bundle();
                    navigateFragment(new ModifyNextDeliveryFragment().newInstance(mNextDelivery, date), "MODIFYNEXTDELIVERYFRAGMENT", bundle, getActivity());
                }
                break;
            case R.id.txtAddProduct:
                if (PopUtils.checkDateTimeCondition(getContext(), date)) {
                    PopUtils.alertDialog(getActivity(), "Oops!! Your request for tomorrow cant be proccessed as, cut off time is crossed. Please try with a future date.", null);
                } else {
                    /*Navigating to categories screen*/
                    Intent mIntent1 = new Intent(getActivity(), CategoriesActivity.class);
                    mIntent1.putExtra("from", "3");
                    mIntent1.putExtra("DATE", date);
                    startActivityForResult(mIntent1, 103);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 103 && data != null) {
            date = data.getStringExtra("DATE");

            if (PopUtils.checkInternetConnection(getActivity())) {
                requestForNextDeliveryWS();
            } else {
                PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
            }
        }
    }

    /*Requesting for next delivery service call*/
    private void requestForNextDeliveryWS() {
        showLoadingDialog(getActivity(), "Loading...", true);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(getContext()).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "next_delivery");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(getActivity(), "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_NEXT_DAY_DELIVERY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Called when error occured from service call*/
    @Override
    public void ErrorResponse(String response, int requestCode) {
        hideLoadingDialog(getActivity());
        PopUtils.alertDialog(getActivity(), response.toString(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /*Called when success occured from service call*/
    @Override
    public void SuccessResponse(String response, int requestCode) {
        hideLoadingDialog(getActivity());
        switch (requestCode) {
            case WsUtils.WS_CODE_NEXT_DAY_DELIVERY:
                responseForNextDayDelivery(response);
                break;
            default:
                break;
        }
    }

    /*Response for next day delivery service call*/
    private void responseForNextDayDelivery(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    mNestedScrollView.setVisibility(View.VISIBLE);
                    mRlNoData.setVisibility(View.GONE);

                    JSONArray mDataArray = mJsonObject.getJSONArray("data");
                    mNextDelivery.clear();
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);

                        BaseApplication.nextDeliveryDate = mDataObject.optString("date");
                        date = mDataObject.optString("date");

                        mNextDelivery.add(new NextDelivery(mDataObject.optString("s_id"),
                                mDataObject.optString("user_id"),
                                mDataObject.optString("product_id"),
                                mDataObject.optString("start_date"),
                                mDataObject.optString("end_date"),
                                mDataObject.optString("sub_type"),
                                mDataObject.optString("sub_quantity"),
                                mDataObject.optString("sd_status"),
                                mDataObject.optString("subscription_pause_exp_date"),
                                mDataObject.optString("is_paused"),
                                mDataObject.optString("sr_number"),
                                mDataObject.optString("sd_id"),
                                mDataObject.optString("date"),
                                mDataObject.optInt("quantity"),
                                mDataObject.optString("delivery_status"),
                                mDataObject.optString("p_name"),
                                mDataObject.optString("image_path") + mDataObject.optString("image"),
                                mDataObject.optString("quantity_name"),
                                mDataObject.optString("description"),
                                mDataObject.optString("cat_id"),
                                mDataObject.optString("is_all_cities"),
                                mDataObject.optString("brand_id"),
                                mDataObject.optString("price")));
                    }

                    mTxtDate.setText(date);
                    setAdapter();
                } else {
                    mRlNoData.setVisibility(View.VISIBLE);
                    mNestedScrollView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Set adapter for next day delivery list service call*/
    private void setAdapter() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        NextDeliveryAdapter mNextDeliveryAdapter = new NextDeliveryAdapter(getContext(), mNextDelivery, "Next");
        mRecyclerView.setAdapter(mNextDeliveryAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new CategoriesAdapter(getContext(), new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }
}
