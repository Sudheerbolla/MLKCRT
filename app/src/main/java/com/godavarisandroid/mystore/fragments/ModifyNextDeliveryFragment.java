package com.godavarisandroid.mystore.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.godavarisandroid.mystore.R;
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
 * Created by Excentd11 on 4/27/2018.
 */

public class ModifyNextDeliveryFragment extends BaseFragment implements View.OnClickListener, IParseListener {
    private View view;

    private TextView mTxtDate, mTxtSave, mTxtAddProduct;
    private RecyclerView mRecyclerView;

    private static ArrayList<NextDelivery> mNextDelivery;
    private static String date;

    public static ModifyNextDeliveryFragment newInstance(ArrayList<NextDelivery> mNextDelivery1, String date1) {
        ModifyNextDeliveryFragment fragment = new ModifyNextDeliveryFragment();
        mNextDelivery = mNextDelivery1;
        date = date1;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_modify_next_delivery, container, false);

        ((HomeActivity) getActivity()).mImgLogo.setVisibility(View.GONE);
        ((HomeActivity) getActivity()).mImgHelp.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgBack.setVisibility(View.VISIBLE);

        initComponents();
        return view;
    }

    private void initComponents() {
        setReferences();
        setClickListeners();

        setAdapter();
    }

    /*Initializing Views*/
    private void setReferences() {
        mTxtDate = (TextView) view.findViewById(R.id.txtDate);
        mTxtDate.setText(date);

        mTxtSave = (TextView) view.findViewById(R.id.txtSave);
        mTxtAddProduct = (TextView) view.findViewById(R.id.txtAddProduct);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mTxtAddProduct.setOnClickListener(this);
        mTxtSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSave:
                /*Requesting for modify next delivery service call*/
                if (PopUtils.checkInternetConnection(getActivity())) {
                    requestForModifyNextDeliveryWS();
                } else {
                    PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
                }

           /*     PopUtils.alertDialog(getActivity(), "Your modification request for delivery on " + date + " is successful",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (PopUtils.checkInternetConnection(getActivity())) {
                                    requestForModifyNextDeliveryWS();
                                } else {
                                    PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
                                }

//                                ((HomeActivity) getActivity()).onBackPressed();
                            }
                        });*/
                break;
            case R.id.txtAddProduct:
                PopUtils.alertAddProductCategory(getActivity(), null);
                break;
            default:
                break;
        }
    }

    /*Requesting for modify next delivery service call*/
    private void requestForModifyNextDeliveryWS() {
        showLoadingDialog(getActivity(), "Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(getContext()).getUserToken());

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
            serverResponse.serviceRequestJSonObject(getActivity(), "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_MODIFY_NEXT_DELIVERY);
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
            case WsUtils.WS_CODE_MODIFY_NEXT_DELIVERY:
                responseForModifyNextDelivery(response);
                break;
            default:
                break;
        }
    }

    /*Respose for modify next delivery service call*/
    private void responseForModifyNextDelivery(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
//                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    PopUtils.alertDialog(getActivity(), "Your modification request for delivery on " + date + " is successful", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((HomeActivity) getActivity()).onBackPressed();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Set adapter for next delivery*/
    private void setAdapter() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        NextDeliveryAdapter mNextDeliveryAdapter = new NextDeliveryAdapter(getContext(), mNextDelivery, "Modify");
        mRecyclerView.setAdapter(mNextDeliveryAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new CategoriesAdapter(getContext(), new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }


}
