package com.godavarisandroid.mystore.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.HomeActivity;
import com.godavarisandroid.mystore.adapters.CategoriesAdapter;
import com.godavarisandroid.mystore.adapters.DeliveryHistoryAdapter;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.DeliveryHistory;
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
public class DeliveryHistoryInnerFragment extends BaseFragment implements IParseListener {
    private View view;
    private RelativeLayout mRlNoData;
    private RecyclerView mRecyclerView;

    private String month = "";
    ArrayList<DeliveryHistory> mDeliveryHistory = new ArrayList<>();

    public void showData(String month) {
        this.month = month;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delivery_history_inner, container, false);

        ((HomeActivity) getActivity()).mImgLogo.setVisibility(View.GONE);
        ((HomeActivity) getActivity()).mImgBack.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgHelp.setVisibility(View.VISIBLE);

        initComponents();
        return view;
    }

    private void initComponents() {
        setReferences();
        setClickListeners();

        /*Requesting for delivery history service call*/
        if (PopUtils.checkInternetConnection(getActivity())) {
            requestForDeliveryHistoryWS();
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

    /*Requesting for delivery history service call*/
    private void requestForDeliveryHistoryWS() {
        showLoadingDialog(getActivity(), "Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(getContext()).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "delivery_history");
            mJsonObject.put("month", month);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(getActivity(), "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_DELIVERY_HISTORY);
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
            case WsUtils.WS_CODE_DELIVERY_HISTORY:
                responseForDeliveryHistory(response);
                break;
            default:
                break;
        }
    }

    /*Response for delivery history service call*/
    private void responseForDeliveryHistory(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mRlNoData.setVisibility(View.GONE);

                    JSONArray mDataArray = mJsonObject.getJSONArray("data");
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);

                        mDeliveryHistory.add(new DeliveryHistory(mDataObject.optString("p_id"),
                                mDataObject.optString("p_name"),
                                mDataObject.optString("quantity_name"),
                                mDataObject.optString("date"),
                                mDataObject.optString("tot_price"),
                                mDataObject.optString("price"),
                                mDataObject.optString("quantity"),
                                mDataObject.optString("image_path") + mDataObject.optString("image")));
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

    /*Set adapter for delivery history list*/
    private void setAdapter() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        DeliveryHistoryAdapter mDeliveryHistoryAdapter = new DeliveryHistoryAdapter(getContext(), mDeliveryHistory, new DeliveryHistoryInnerFragment());
        mRecyclerView.setAdapter(mDeliveryHistoryAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new CategoriesAdapter(getContext(), new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }
}
