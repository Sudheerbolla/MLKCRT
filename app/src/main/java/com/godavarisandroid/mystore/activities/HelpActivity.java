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
 * Created by Excentd11 on 5/31/2018.
 */

public class HelpActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mImgLogo, mImgBack, mImgHelpIcon;
    private TextView mTxtTitle;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initComponents();
    }

    private void initComponents() {
        setReferences();
        setClickListeners();

        loadWebView();
    }

    /*Initializing Views*/
    private void setReferences() {
        mImgLogo = (ImageView) findViewById(R.id.imgLogo);
        mImgLogo.setVisibility(View.GONE);
        mImgBack = (ImageView) findViewById(R.id.imgBack);
        mImgBack.setVisibility(View.VISIBLE);
        mImgHelpIcon = (ImageView) findViewById(R.id.imgNotification);
        mImgHelpIcon.setVisibility(View.GONE);
        mTxtTitle = (TextView) findViewById(R.id.txtTitle);
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText("Help");
        mWebView = (WebView) findViewById(R.id.webView);
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mImgBack.setOnClickListener(this);
    }

    /*Loading help URL to webview*/
    private void loadWebView() {
        mWebView.loadUrl("https://www.milkcart.co.in/help.html");

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressBar);
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
