package com.godavarisandroid.mystore.activities;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.godavarisandroid.mystore.R;

/**
 * Created by Excentd11 on 5/8/2018.
 */

public class TermsActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mImgLogo, mImgBack, mImgHelpIcon;
    private TextView mTxtTitle;
    private WebView mWebView;
    private String urlToLoad, heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        urlToLoad = "https://www.milkcart.co.in/TandC.html";
        heading = "Terms & Conditions";
        getBundleData();
        initComponents();
    }

    private void getBundleData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("urlToLoad")) {
                urlToLoad = bundle.getString("urlToLoad");
            }
            if (bundle.containsKey("heading")) {
                heading = bundle.getString("heading");
            }
        }
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
        loadWebView();
    }

    /*Initializing Views*/
    private void setReferences() {
        mImgLogo = findViewById(R.id.imgLogo);
        mImgLogo.setVisibility(View.GONE);
        mImgBack = findViewById(R.id.imgBack);
        mImgBack.setVisibility(View.VISIBLE);
        mImgHelpIcon = findViewById(R.id.imgNotification);
        mImgHelpIcon.setVisibility(View.GONE);
        mTxtTitle = findViewById(R.id.txtTitle);
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText(heading);
        mWebView = findViewById(R.id.webView);
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mImgBack.setOnClickListener(this);
    }

    private void loadWebView() {
        mWebView.loadUrl(urlToLoad);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                ProgressBar progressbar = findViewById(R.id.progressBar);
                progressbar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                if (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    finish();
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                }
                break;
            default:
                break;
        }
    }
}
