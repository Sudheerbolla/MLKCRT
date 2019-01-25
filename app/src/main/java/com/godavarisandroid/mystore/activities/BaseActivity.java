package com.godavarisandroid.mystore.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.godavarisandroid.mystore.R;

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void navigateActivity(Intent intent, boolean finish) {
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        if (finish) {
            finish();
        }
    }

    /**
     * Method to show dialog with given message
     *
     * @param title        dialog heading
     * @param isCancelable whether dialog is cancellable or not
     */
    public void showLoadingDialog(final String title, final boolean isCancelable) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                return;
            }
            progressDialog = new ProgressDialog(this, R.style.progressDialog);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(title);
//            progressDialog.setCancelable(isCancelable);
            progressDialog.setCancelable(true);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hides loading dialog if shown
     */
    public void hideLoadingDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            progressDialog = null;
        } catch (Exception e) {
            progressDialog = null;
        }
    }

    public void clearStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack(fm.getClass().getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

   /* @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }*/

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
}
