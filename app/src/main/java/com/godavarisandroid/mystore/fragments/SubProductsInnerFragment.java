package com.godavarisandroid.mystore.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.adapters.CategoriesAdapter;
import com.godavarisandroid.mystore.adapters.SubProductsInnerAdapter;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.SubProductsInner;
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

public class SubProductsInnerFragment extends BaseFragment implements IParseListener {
    private View view;
    private TextView mTxtContent;
    private RecyclerView mRecyclerView;

    private String brandId = "", cId = "", cName = "", from = "";
    ArrayList<SubProductsInner> mSubProductsInner = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sub_products_inner, container, false);
        initComponents();
        return view;
    }

    public void showData(String brandId, String cId, String cName, String from) {
        this.brandId = brandId;
        this.cId = cId;
        this.cName = cName;
        this.from = from;
    }

    private void initComponents() {
        setReferences();
        getBundleData();
        setClickListeners();

        /*Requesting for sub products service call*/
        if (PopUtils.checkInternetConnection(getActivity())) {
            requestForProductsWS();
        } else {
            PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
        }
    }

    /*Initializing Views*/
    private void setReferences() {
        mTxtContent = (TextView) view.findViewById(R.id.txtContent);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    }

    /*getting data through bundle*/
    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            brandId = bundle.getString("brand_id");
        }
    }

    private void setClickListeners() {

    }

    /*Requesting for sub products service call*/
    private void requestForProductsWS() {
        showLoadingDialog(getActivity(), "Loading...", true);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(getContext()).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "products");
            mJsonObject.put("cid", cId);
            mJsonObject.put("b_id", brandId);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(getActivity(), "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_PRODUCTS);
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
            case WsUtils.WS_CODE_PRODUCTS:
                responseForProducts(response);
                break;
            default:
                break;
        }
    }

    /*Respons efor sub products service call*/
    private void responseForProducts(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mTxtContent.setVisibility(View.GONE);

                    JSONArray mDataArray = mJsonObject.getJSONArray("data");
                    mSubProductsInner.clear();
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);

                        mSubProductsInner.add(new SubProductsInner(mDataObject.optString("p_id"),
                                mDataObject.optString("p_name"),
                                mDataObject.optString("quantity_name"),
                                mDataObject.optString("description"),
                                mDataObject.optString("status"),
                                mDataObject.optString("price"),
                                mDataObject.optString("start_date"),
                                mDataObject.optString("end_date"),
                                mDataObject.optString("image_path") + mDataObject.optString("image"), cName));
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

    private void setAdapter() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        SubProductsInnerAdapter mSubProductsInnerAdapter = new SubProductsInnerAdapter(getContext(), mSubProductsInner,
                new SubProductsInnerFragment(), from);
        mRecyclerView.setAdapter(mSubProductsInnerAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new CategoriesAdapter(getContext(), new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }
}
