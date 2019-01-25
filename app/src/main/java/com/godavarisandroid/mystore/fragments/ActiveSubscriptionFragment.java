package com.godavarisandroid.mystore.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
 * Created by UMA on 4/22/2018.
 */
public class ActiveSubscriptionFragment extends BaseFragment implements IParseListener {
    private View view;
    private RelativeLayout mRlNoData;
    private RecyclerView mRecyclerView;

    ArrayList<ActiveSubscriptions> mActiveSubscriptions = new ArrayList<>();

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter("KEY"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

   /* @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_active_subscriptions, container, false);

        ((HomeActivity) getActivity()).mLlBottom.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgLogo.setVisibility(View.GONE);
        ((HomeActivity) getActivity()).mImgBack.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgHelp.setVisibility(View.VISIBLE);

        initComponents();
        return view;
    }

    private void initComponents() {
        setReferences();
        setClickListeners();

        /*Requesting for active subscriptions service call*/
        if (PopUtils.checkInternetConnection(getActivity())) {
            requestForActiveSubscriptionsWS();
        } else {
            PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
        }
    }

    /*Initializing Views*/
    private void setReferences() {
        mRlNoData = view.findViewById(R.id.rlNoData);
        mRecyclerView = view.findViewById(R.id.recyclerView);
    }

    private void setClickListeners() {

    }

    /*Requesting for active subscriptions service call*/
    private void requestForActiveSubscriptionsWS() {
        showLoadingDialog(getActivity(), "Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(getContext()).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "active_subsctiptions");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(getActivity(), "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_ACTIVE_SUBSCRIPTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Called when error occured from service call*/
    @Override
    public void ErrorResponse(String response, int requestCode) {
        hideLoadingDialog(getActivity());
        PopUtils.alertDialog(getActivity(), response, new View.OnClickListener() {
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
            case WsUtils.WS_CODE_ACTIVE_SUBSCRIPTION:
                responseForActiveSubscriptions(response);
                break;
            default:
                break;
        }
    }

    /*Response for active subscription service call*/
    private void responseForActiveSubscriptions(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mRlNoData.setVisibility(View.GONE);

                    JSONArray mDataArray = mJsonObject.getJSONArray("data");
                    mActiveSubscriptions.clear();
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);
                        JSONObject mQuantityObject = new JSONObject(mDataObject.optString("sub_quantity"));
                        mActiveSubscriptions.add(new ActiveSubscriptions(mDataObject.optString("s_id"),
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

    /*Set adapter for active subscriptions*/
    private void setAdapter() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        ActiveSubscriptionAdapter mActiveSubscriptionAdapter = new ActiveSubscriptionAdapter(getContext(), mActiveSubscriptions, new ActiveSubscriptionFragment(), "1");
        mRecyclerView.setAdapter(mActiveSubscriptionAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new ActiveSubscriptionAdapter(getContext(), new ActiveSubscriptionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
//                TextView mTxtViewDetails = (TextView) view.findViewById(R.id.txtViewDetails);
//                mTxtViewDetails.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), ActiveSubscriptionInnerActivity.class);
                mIntent.putExtra("from", "1");
                mIntent.putExtra("s_id", mActiveSubscriptions.get(position).sId);
                mIntent.putExtra("product_id", mActiveSubscriptions.get(position).productId);
                mIntent.putExtra("status", mActiveSubscriptions.get(position).status);
//                        if (mActiveSubscriptions.get(position).status.equalsIgnoreCase("A")) {
                mIntent.putExtra("start_date", mActiveSubscriptions.get(position).startDate);
                mIntent.putExtra("end_date", mActiveSubscriptions.get(position).endDate);
//                        } else if (mActiveSubscriptions.get(position).status.equalsIgnoreCase("P")) {
                mIntent.putExtra("pause_start_date", mActiveSubscriptions.get(position).pauseStartDate);
                mIntent.putExtra("pause_end_date", mActiveSubscriptions.get(position).pauseEndDate);
//                        }
                mIntent.putExtra("product_name", mActiveSubscriptions.get(position).pName);
                mIntent.putExtra("quantity_name", mActiveSubscriptions.get(position).quantityName);
                mIntent.putExtra("description", mActiveSubscriptions.get(position).description);
                mIntent.putExtra("price", mActiveSubscriptions.get(position).price);
                mIntent.putExtra("sub_type", mActiveSubscriptions.get(position).subType);
                mIntent.putExtra("image", mActiveSubscriptions.get(position).image);
                mIntent.putExtra("sub_quantity", mActiveSubscriptions.get(position).quantityObject.toString());
                mIntent.putExtra("date_of_status", mActiveSubscriptions.get(position).dateOfStatus);
                startActivityForResult(mIntent, 101);
//                    }
//                });
            }
        }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(ActiveSubscriptionFragment.this).attach(ActiveSubscriptionFragment.this).commit();
            }
        }
    }
}
