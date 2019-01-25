package com.godavarisandroid.mystore.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.HomeActivity;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.godavarisandroid.mystore.utils.UserDetails;
import com.godavarisandroid.mystore.webUtils.ServerResponse;
import com.godavarisandroid.mystore.webUtils.WebServices;
import com.godavarisandroid.mystore.webUtils.WsUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by UMA on 4/21/2018.
 */

public class OfferDetailsFragment extends BaseFragment implements IParseListener {
    private View view;
    private ScrollView mScrollView;
    private TextView mTxtOffers, mTxtTerms, mTxtContent;
    private ImageView mImgAd;
    private LinearLayout mLlOfferDetails, mLlTerms;

    private String adId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_offer_details, container, false);

        ((HomeActivity) getActivity()).mImgLogo.setVisibility(View.GONE);
        ((HomeActivity) getActivity()).mImgHelp.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mImgBack.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mTxtTitle.setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).mTxtTitle.setText("Offer Details");

        initComponents();
        return view;
    }

    private void initComponents() {
        setReferences();
        getBundleData();

        /*Requesting for offer details service call*/
        if (PopUtils.checkInternetConnection(getActivity())) {
            requestForOffersWS();
        } else {
            PopUtils.alertDialog(getActivity(), getString(R.string.pls_check_internet), null);
        }

        setOfferDetails();
        setTerms();
    }

    /*Initializing Views*/
    private void setReferences() {
        mScrollView = view.findViewById(R.id.scrollView);
        mTxtContent = view.findViewById(R.id.txtContent);
        mTxtOffers = view.findViewById(R.id.txtOffers);
        mTxtTerms = view.findViewById(R.id.txtTerms);
        mImgAd = view.findViewById(R.id.imgAd);
            mLlOfferDetails = view.findViewById(R.id.llOfferDetails);
        mLlTerms = view.findViewById(R.id.llTerms);
    }

    /*getting data through bundle*/
    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            adId = bundle.getString("AD_ID");
        }
    }

    private void setOfferDetails() {
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < 5; i++) {
            TextView textView = new TextView(getActivity());
            textView.setLayoutParams(lparams);
            textView.setTextSize(12);
            textView.setPadding(10, 5, 10, 5);
            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_text));
            textView.setText("Lorem ipsum is simply dummy text of the printing and typesetting industry.");
            mLlOfferDetails.addView(textView);
        }
    }

    private void setTerms() {
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < 5; i++) {
            TextView textView = new TextView(getActivity());
            textView.setLayoutParams(lparams);
            textView.setTextSize(12);
            textView.setPadding(10, 5, 10, 5);
            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_text));
            textView.setText("Lorem ipsum is simply dummy text of the printing and typesetting industry.");
            mLlTerms.addView(textView);
        }
    }

    /*Requesting for offer details service call*/
    private void requestForOffersWS() {
        showLoadingDialog(getActivity(), "Loading...", true);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(getContext()).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "offers");
            mJsonObject.put("aid", adId);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(getActivity(), "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_OFFERS);
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
            case WsUtils.WS_CODE_OFFERS:
                responseForOffers(response);
                break;
            default:
                break;
        }
    }

    /*Response for offer details service call*/
    private void responseForOffers(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    mScrollView.setVisibility(View.VISIBLE);
                    mTxtContent.setVisibility(View.GONE);

                    JSONArray mDataArray = mJsonObject.getJSONArray("data");
                    JSONObject mDataObject = mDataArray.getJSONObject(0);

                    mTxtOffers.setText(mDataObject.optString("offer"));
                    mTxtTerms.setText(mDataObject.optString("terms_cond"));
                    Picasso.with(getActivity()).load(mDataObject.optString("image_path") + mDataObject.optString("banner")).into(mImgAd);
                } else {
                    mScrollView.setVisibility(View.GONE);
                    mTxtContent.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
