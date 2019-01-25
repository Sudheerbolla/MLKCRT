package com.godavarisandroid.mystore.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.godavarisandroid.mystore.R;

/**
 * Created by Excentd11 on 11/18/2016.
 */

public class BaseFragment extends Fragment {
    private ProgressDialog mProgressDialog;

    public void showLoadingDialog(Context context, final String title, final boolean isCancelable) {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = new ProgressDialog(context, R.style.progressDialog);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage(title);
//            mProgressDialog.setCancelable(isCancelable);
            mProgressDialog.setCancelable(true);
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
        fragmentTransaction.replace(R.id.container, fragment, tag);
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        if (tag != null) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
    }
}