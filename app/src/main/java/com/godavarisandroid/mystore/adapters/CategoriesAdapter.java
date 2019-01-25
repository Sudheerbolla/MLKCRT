package com.godavarisandroid.mystore.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.fragments.HomeFragment;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.Categories;
import com.godavarisandroid.mystore.models.SubCategories;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.godavarisandroid.mystore.utils.UserDetails;
import com.godavarisandroid.mystore.webUtils.ServerResponse;
import com.godavarisandroid.mystore.webUtils.WebServices;
import com.godavarisandroid.mystore.webUtils.WsUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by UMA on 4/21/2018.
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.VideoHolder> implements
        RecyclerView.OnItemTouchListener {
    private Context mContext;
    private CategoriesAdapter.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private ArrayList<Categories> mCategories;
    private HomeFragment mHomeFragment;
    private int selectedPosition = -1;
    private String from = "";
    private ProgressDialog mProgressDialog;

    public CategoriesAdapter(Context context, ArrayList<Categories> mCategories, HomeFragment mHomeFragment, String from) {
        this.mContext = context;
        this.from = from;
        this.mCategories = mCategories;
        this.mHomeFragment = mHomeFragment;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public CategoriesAdapter(Context context, CategoriesAdapter.OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public CategoriesAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories, parent, false);
        return new CategoriesAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoriesAdapter.VideoHolder holder, final int position) {
        final Categories mCategories = this.mCategories.get(position);

        if (selectedPosition == position) {
            holder.mRecyclerView.setVisibility(View.VISIBLE);
            holder.mTxtCategory.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.img_drop_up, null), null);

            if (PopUtils.checkInternetConnection(mContext)) {
                requestForSubCategoriesWS(mCategories.id, holder.mRecyclerView, holder.mTxtContent, holder.mTxtCategory);
            } else {
                PopUtils.alertDialog(mContext, mContext.getString(R.string.pls_check_internet), null);
            }
        } else {
            holder.mRecyclerView.setVisibility(View.GONE);
            holder.mTxtContent.setVisibility(View.GONE);
            holder.mTxtCategory.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.img_drop_down, null), null);
        }

        holder.mTxtCategory.setText(mCategories.name);
        Picasso.with(mContext).load(mCategories.imagePath + mCategories.image).into(holder.mImgCategory);

        holder.mRlHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition == position) {
                    selectedPosition = -1;
                    holder.mRecyclerView.setVisibility(View.GONE);
                    holder.mTxtContent.setVisibility(View.GONE);
                    holder.mTxtCategory.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.img_drop_down, null), null);
                } else {
                    selectedPosition = position;
                    notifyDataSetChanged();
                }
            }
        });
    }

    /*Requesting for sub categories service call*/
    private void requestForSubCategoriesWS(String categoryId, final RecyclerView mRecyclerView, final TextView txtContent,
                                           final TextView mTxtCategory) {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(mContext).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "categorys");
            mJsonObject.put("cid", categoryId);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(mContext, "POST", WebServices.BASE_URL, mJsonObject, params,
                    new IParseListener() {
                        @Override
                        public void ErrorResponse(String response, int requestCode) {
                            hideLoadingDialog(mContext);
                            PopUtils.alertDialog(mContext, "", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });
                        }

                        @Override
                        public void SuccessResponse(String response, int requestCode) {
                            hideLoadingDialog(mContext);
                            switch (requestCode) {
                                case WsUtils.WS_CODE_CATEGORIES:
                                    responseForSubCategories(response, mRecyclerView, txtContent);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }, WsUtils.WS_CODE_CATEGORIES);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Response for sub categories service call*/
    private void responseForSubCategories(String response, RecyclerView mRecyclerView, TextView mTxtContent) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mTxtContent.setVisibility(View.GONE);

                    JSONArray mDataArray = mJsonObject.getJSONArray("data");
                    ArrayList<SubCategories> mSubCategories = new ArrayList<>();
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);

                        mSubCategories.add(new SubCategories(mDataObject.optString("cid"),
                                mDataObject.optString("cat_name"),
                                mDataObject.optString("image_path") + mDataObject.optString("cat_image")));
                    }
                    setAdapter(mRecyclerView, mSubCategories);

                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    mTxtContent.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Set adapter for sub categories*/
    private void setAdapter(RecyclerView mRecyclerView, ArrayList<SubCategories> mSubCategories) {
        GridLayoutManager mLinearLayoutManager = new GridLayoutManager(mContext, 3);
        SubCategoriesAdapter mSubCategoriesAdapter = new SubCategoriesAdapter(mContext, mSubCategories,
                new HomeFragment(), from);
        mRecyclerView.setAdapter(mSubCategoriesAdapter);
        mRecyclerView.scrollToPosition(0); //use to focus the item with index
        mSubCategoriesAdapter.notifyDataSetChanged();
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new SubCategoriesAdapter(mContext, new SubCategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }

    public void showLoadingDialog(final String title, final boolean isCancelable) {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = new ProgressDialog(mContext, R.style.progressDialog);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage(title);
            mProgressDialog.setCancelable(isCancelable);
            mProgressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideLoadingDialog(Context mContext) {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            mProgressDialog = null;
        } catch (Exception e) {
            mProgressDialog = null;
        }
    }

    public static void navigateFragment(Fragment fragment, String tag, Bundle bundle, FragmentActivity fragmentActivity) {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        fragmentTransaction.replace(R.id.container, fragment, tag);
        if (tag != null) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        ImageView mImgCategory;
        TextView mTxtCategory, mTxtContent;
        RecyclerView mRecyclerView;
        RelativeLayout mRlHeader;

        public VideoHolder(View view) {
            super(view);

            mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//            ViewCompat.setNestedScrollingEnabled(mSVContent, false);
            mTxtCategory = (TextView) view.findViewById(R.id.txtCategory);
            mTxtContent = (TextView) view.findViewById(R.id.txtContent);
            mImgCategory = (ImageView) view.findViewById(R.id.imgCategory);
            mRlHeader = (RelativeLayout) view.findViewById(R.id.rlHeader);
            mRlHeader.setTag(0);
        }
    }
}