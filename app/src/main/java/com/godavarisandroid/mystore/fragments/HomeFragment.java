package com.godavarisandroid.mystore.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.HomeActivity;
import com.godavarisandroid.mystore.adapters.CategoriesAdapter;
import com.godavarisandroid.mystore.adapters.SlidingImageAdapter;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.Ads;
import com.godavarisandroid.mystore.models.Categories;
import com.godavarisandroid.mystore.models.Notification;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.godavarisandroid.mystore.utils.UserDetails;
import com.godavarisandroid.mystore.views.AutoScrollViewPager;
import com.godavarisandroid.mystore.webUtils.ServerResponse;
import com.godavarisandroid.mystore.webUtils.WebServices;
import com.godavarisandroid.mystore.webUtils.WsUtils;
import com.rd.PageIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends BaseFragment implements View.OnClickListener, IParseListener {
    private View view;
    private TextView mTxtContent, mTxtNotificationOne, mTxtNotificationTwo, mTxtNotificationThree;
    private ImageView mImgAd;
    private LinearLayout mLlNotification;
    private RelativeLayout mRlAds;
    private NestedScrollView mSVContent;
    private AutoScrollViewPager mViewPager;
    private PageIndicatorView mPageIndicatorView;
    private RecyclerView mRecyclerView;
    private HomeActivity homeActivity;
    ArrayList<Categories> mCategories = new ArrayList<>();
    ArrayList<Ads> mAds = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeActivity = (HomeActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        homeActivity.mLlBottom.setVisibility(View.VISIBLE);
        homeActivity.mImgLogo.setVisibility(View.VISIBLE);
        homeActivity.mImgBack.setVisibility(View.GONE);
        homeActivity.mImgHelp.setVisibility(View.VISIBLE);
        homeActivity.mTxtTitle.setVisibility(View.GONE);
        homeActivity.mTxtTitle.setText("MilkCart");

        initComponents();
        return view;
    }

    private void initComponents() {
        setReferences();
        setClickListeners();

        if (PopUtils.checkInternetConnection(homeActivity)) {
            requestForCategoriesWS();
        } else {
            PopUtils.alertDialog(homeActivity, getString(R.string.pls_check_internet), null);
        }
    }

    /*Initializing Views*/
    private void setReferences() {
        mTxtContent = view.findViewById(R.id.txtContent);
        mTxtNotificationOne = view.findViewById(R.id.txtNotificationOne);
        mTxtNotificationTwo = view.findViewById(R.id.txtNotificationTwo);
        mTxtNotificationThree = view.findViewById(R.id.txtNotificationThree);
        mImgAd = view.findViewById(R.id.imgAd);
        mLlNotification = view.findViewById(R.id.llNotification);
        mRlAds = view.findViewById(R.id.rlAds);
        mSVContent = view.findViewById(R.id.sVContent);
        mViewPager = view.findViewById(R.id.viewPager);
        mViewPager.startAutoScroll();
        mViewPager.setInterval(3000);
        mViewPager.setCycle(true);
        mViewPager.setStopScrollWhenTouch(true);
        mPageIndicatorView = view.findViewById(R.id.pageIndicatorView);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        ViewCompat.setNestedScrollingEnabled(mSVContent, false);
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mImgAd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgAd:
                /*Navigate to offer details screen*/
//                Bundle bundle = new Bundle();
//                navigateFragment(new OfferDetailsFragment(), "OFFERDETAILSFRAGMENT", bundle, homeActivity);
//                Intent intent = new Intent(homeActivity, TermsActivity.class);
//                if (TextUtils.isEmpty(adObj.optString("url"))) {
//                    intent.putExtra("urlToLoad", adObj.optString("url"));
//                    intent.putExtra("heading", "Offer Details");
//                } else {
//                    intent.putExtra("urlToLoad", "https://www.milkcart.co.in");
//                    intent.putExtra("heading", "Offer Details");
//                }
//                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /*Requesting for categories service call*/
    private void requestForCategoriesWS() {
        showLoadingDialog(homeActivity, "Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(getContext()).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "categorys");
            mJsonObject.put("cid", "0");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(homeActivity, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_CATEGORIES);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Called when error occured from service call*/
    @Override
    public void ErrorResponse(String response, int requestCode) {
        hideLoadingDialog(homeActivity);
        PopUtils.alertDialog(homeActivity, response, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /*Called when success occured from service call*/
    @Override
    public void SuccessResponse(String response, int requestCode) {
        hideLoadingDialog(homeActivity);
        switch (requestCode) {
            case WsUtils.WS_CODE_CATEGORIES:
                responseForCategories(response);
                break;
            default:
                break;
        }
    }

    /*Response for categories service call*/
    private void responseForCategories(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");

                BaseApplication.rechargeMessage = UserDetails.getInstance(homeActivity).getPaymentType().
                        equalsIgnoreCase("PrePaid") ? message : mJsonObject.optString("post_paid_msg");

                homeActivity.setRechargeBannerData();
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    Log.d("success is , ", response);
                    mSVContent.setVisibility(View.VISIBLE);
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

                    JSONArray mAdsArray = mJsonObject.getJSONArray("ads");
                    mAds.clear();
                    if (mAdsArray.length() > 0) {
                        mRlAds.setVisibility(View.VISIBLE);
                        for (int i = 0; i < mAdsArray.length(); i++) {
                            JSONObject mAdsObject = mAdsArray.getJSONObject(i);

                            mAds.add(new Ads(mAdsObject.optString("aid"),
                                    mAdsObject.optString("image_path") + mAdsObject.optString("banner"),
                                    mAdsObject.optString("url")));
                            mAdsObject.optString("title");
                        }
                    } else {
                        mRlAds.setVisibility(View.GONE);
                    }
                    mPageIndicatorView.setCount(mAds.size());
                    setAdAdapter();

                    JSONArray mAds1Array = mJsonObject.optJSONArray("ads1");
                    if (mAds1Array.length() > 0) {
                        BaseApplication.adsObj = mAds1Array.optJSONObject(0);
                    }

                    JSONArray mNotificationArray = mJsonObject.getJSONArray("notification");
                    if (BaseApplication.mNotificationList == null)
                        BaseApplication.mNotificationList = new ArrayList<>();
                    else BaseApplication.mNotificationList.clear();

                    if (mNotificationArray.length() > 0) {
                        for (int i = 0; i < mNotificationArray.length(); i++) {
                            BaseApplication.mNotificationList.add(new Notification(mNotificationArray.getJSONObject(i)));
                        }
                    }
                } else {
                    mSVContent.setVisibility(View.GONE);
                    mTxtContent.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Set adapter for sliding images on top of the screen*/
    private void setAdAdapter() {
        mViewPager.setAdapter(new SlidingImageAdapter(homeActivity, mAds));
    }

    /*Set adapter for categories list*/
    private void setAdapter() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(homeActivity);
        CategoriesAdapter mCategoriesAdapter = new CategoriesAdapter(getContext(), mCategories, new HomeFragment(), "1");
        mRecyclerView.setAdapter(mCategoriesAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new CategoriesAdapter(getContext(), new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }
}
