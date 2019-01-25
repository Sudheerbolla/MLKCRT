package com.godavarisandroid.mystore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.ActiveSubscriptionInnerActivity;
import com.godavarisandroid.mystore.activities.HomeActivity;
import com.godavarisandroid.mystore.adapters.ActiveSubscriptionAdapter;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.ActiveSubscriptions;
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
 * Created by Excentd11 on 5/3/2018.
 */

public class InActiveSubscriptionFragment extends BaseFragment implements IParseListener {
    private View view;
    private RelativeLayout mRlNoData;
    private RecyclerView mRecyclerView;

    private ArrayList<ActiveSubscriptions> mInActiveSubscriptions = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_active_subscriptions, container, false);

//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(InActiveSubscriptionFragment.this).attach(InActiveSubscriptionFragment.this).commit();

        ((HomeActivity) getActivity()).mLlBottom.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgLogo.setVisibility(View.GONE);
        ((HomeActivity) getActivity()).mImgBack.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgHelp.setVisibility(View.VISIBLE);

        initComponents();
        return view;
    }

    /*Refresh the screen on every time*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    private void initComponents() {
        setReferences();
        setClickListeners();

        /*Requesting for inactive subscrtiptions service call*/
        if (PopUtils.checkInternetConnection(getActivity())) {
            requestForInActiveSubscriptionsWS();
        } else {
            PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
        }
    }

    /*Initializing Views*/
    private void setReferences() {
        mRlNoData = (RelativeLayout) view.findViewById(R.id.rlNoData);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    }

    private void setClickListeners() {

    }

    /*Requesting for Inactive subscriptions service call*/
    private void requestForInActiveSubscriptionsWS() {
        showLoadingDialog(getActivity(), "Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(getContext()).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "inactive_subsctiptions");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(getActivity(), "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_INACTIVE_SUBSCRIPTION);
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
            case WsUtils.WS_CODE_INACTIVE_SUBSCRIPTION:
                responseForInActiveSubscriptions(response);
                break;
            default:
                break;
        }
    }

    /*Response for inactive subscriptions servoce call*/
    private void responseForInActiveSubscriptions(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mRlNoData.setVisibility(View.GONE);

                    JSONArray mDataArray = mJsonObject.getJSONArray("data");
                    mInActiveSubscriptions.clear();

                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);
                        JSONObject mQuantityObject = new JSONObject(mDataObject.optString("sub_quantity"));
                        mInActiveSubscriptions.add(new ActiveSubscriptions(mDataObject.optString("s_id"),
                                mDataObject.optString("user_id"),
                                mDataObject.optString("product_id"),
                                mDataObject.optString("start_date"),
                                mDataObject.optString("end_date"),
                                mDataObject.optString("status"),
                                mDataObject.optString("p_name"),
                                mDataObject.optString("quantity_name"),
                                mDataObject.optString("description"),
                                mDataObject.optString("price"),
                                mDataObject.optString("sub_type"),
                                mDataObject.optString("image_path") + mDataObject.optString("image"),
                                mQuantityObject,
                                mDataObject.optString("pause_start_date"),
                                mDataObject.optString("pause_end_date"),
                                mDataObject.optString("date_of_status")));
                    }

                    setAdapter();
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    mRlNoData.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Set adapter for inactive subscription list*/
    private void setAdapter() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        ActiveSubscriptionAdapter mActiveSubscriptionAdapter = new ActiveSubscriptionAdapter(getContext(), mInActiveSubscriptions, new ActiveSubscriptionFragment(), "2");
        mRecyclerView.setAdapter(mActiveSubscriptionAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new ActiveSubscriptionAdapter(getContext(), new ActiveSubscriptionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent mIntent = new Intent(getActivity(), ActiveSubscriptionInnerActivity.class);
                mIntent.putExtra("from", "2");
                mIntent.putExtra("s_id", mInActiveSubscriptions.get(position).sId);
                mIntent.putExtra("product_id", mInActiveSubscriptions.get(position).productId);
                mIntent.putExtra("start_date", mInActiveSubscriptions.get(position).startDate);
                mIntent.putExtra("end_date", mInActiveSubscriptions.get(position).endDate);
                mIntent.putExtra("status", mInActiveSubscriptions.get(position).status);
                mIntent.putExtra("product_name", mInActiveSubscriptions.get(position).pName);
                mIntent.putExtra("quantity_name", mInActiveSubscriptions.get(position).quantityName);
                mIntent.putExtra("description", mInActiveSubscriptions.get(position).description);
                mIntent.putExtra("price", mInActiveSubscriptions.get(position).price);
                mIntent.putExtra("sub_type", mInActiveSubscriptions.get(position).subType);
                mIntent.putExtra("image", mInActiveSubscriptions.get(position).image);
                mIntent.putExtra("sub_quantity", mInActiveSubscriptions.get(position).quantityObject.toString());
                mIntent.putExtra("date_of_status", mInActiveSubscriptions.get(position).dateOfStatus);
                startActivityForResult(mIntent, 101);
            }
        }));
    }
}
