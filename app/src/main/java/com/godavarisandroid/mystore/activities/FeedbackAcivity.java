package com.godavarisandroid.mystore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.adapters.CategoriesAdapter;
import com.godavarisandroid.mystore.adapters.FeedbackQuestionsAdapter;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.FeedbackQuestions;
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
 * Created by UMA on 4/21/2018.
 */

public class FeedbackAcivity extends BaseActivity implements View.OnClickListener, IParseListener {
    private TextView mTxtSubmit, mTxtTitle;
    private EditText mEdtMessage;
    private LinearLayout mLlOtherDetails;
    private ImageView mImgLogo, mImgBack, mImgNotification;
    private RecyclerView mRecyclerView;

    private ArrayList<FeedbackQuestions> mFeedbackQuestions = new ArrayList<>();
    private FeedbackQuestionsAdapter mFeedbackQuestionsAdapter;

    private boolean isSelectedDidntGetDelivery, isSelectedMissingPfroduct, isSelectedWrongItem, isSelectedWrongQuantity, isSelectedOthers;
    private boolean isFeedbackSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initComponents();
    }

    private void initComponents() {
        setReferences();
        setClickListeners();

        /*Requesting for feedback questions service call*/
        if (PopUtils.checkInternetConnection(this)) {
            requestForFeedbackQuestionsWS();
        } else {
            PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
        }
    }

    /*Initializing Views*/
    private void setReferences() {
        mTxtSubmit = (TextView) findViewById(R.id.txtSubmit);
        mTxtTitle = (TextView) findViewById(R.id.txtTitle);
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText("Feedback and Support");

        mEdtMessage = (EditText) findViewById(R.id.edtMessage);
        mLlOtherDetails = (LinearLayout) findViewById(R.id.llOtherDetails);

        mImgLogo = (ImageView) findViewById(R.id.imgLogo);
        mImgLogo.setVisibility(View.GONE);

        mImgBack = (ImageView) findViewById(R.id.imgBack);
        mImgBack.setVisibility(View.VISIBLE);

        mImgNotification = (ImageView) findViewById(R.id.imgNotification);
        mImgNotification.setVisibility(View.VISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mTxtSubmit.setOnClickListener(this);
        mImgLogo.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mImgNotification.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSubmit:
                if (!checkValidation().equalsIgnoreCase("")) {
                    Toast.makeText(this, checkValidation(), Toast.LENGTH_SHORT).show();
                } else {
                    /*Requesting for send feedback service call*/
                    if (PopUtils.checkInternetConnection(this)) {
                        requestForSendFeedbackWS();
                    } else {
                        PopUtils.alertDialog(this, getString(R.string.pls_check_internet), null);
                    }
                }
                break;
            case R.id.imgBack:
               /*Called on back button press*/
                onBackPressed();
                break;
            case R.id.imgNotification:
                /*Navigating to help screen*/
                navigateActivity(new Intent(this, HelpActivity.class), false);
                break;
            default:
                break;
        }
    }

    /*Called when clicking on submit button*/
    private String checkValidation() {
        String message = "";
        if (mFeedbackQuestionsAdapter != null && mFeedbackQuestionsAdapter.getSelectedPosition() == -1) {
            message = "Please select the issue";
        } else if (mEdtMessage.getText().toString().length() < 5) {
            message = "Please share the details of the exact issue";
        }
        return message;
    }

    /*Requesting for send feedback service call*/
    private void requestForSendFeedbackWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "feedback");
            mJsonObject.put("question_id", BaseApplication.feedbackId);
            mJsonObject.put("message", mEdtMessage.getText().toString().trim());
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_FEEDBACK);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Requesting for feedback questions service call*/
    private void requestForFeedbackQuestionsWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "feedback_questions");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_FEEDBACK_QUESTIONS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Called when error occured from service call*/
    @Override
    public void ErrorResponse(String response, int requestCode) {
        hideLoadingDialog();
        PopUtils.alertDialog(this, response.toString(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /*Called when success occured from service call*/
    @Override
    public void SuccessResponse(String response, int requestCode) {
        hideLoadingDialog();
        switch (requestCode) {
            case WsUtils.WS_CODE_FEEDBACK_QUESTIONS:
                responseForFeedbackQuestions(response);
                break;
            case WsUtils.WS_CODE_FEEDBACK:
                responseForFeedback(response);
                break;
            default:
                break;
        }
    }

    /*Response for send feedback service call*/
    private void responseForFeedback(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    PopUtils.alertDialog(this, "Thank you for your feedback. We'll get back to you shortly", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                        }
                    });
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
            }
        }
    }

    /*Response efor feedback questions service call*/
    private void responseForFeedbackQuestions(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray mDataArray = mJsonObject.getJSONArray("data");
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);

                        mFeedbackQuestions.add(new FeedbackQuestions(mDataObject.optString("fbq_id"),
                                mDataObject.optString("question")));
                    }

                    setAdapter();
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Set adapter for feedback questions*/
    private void setAdapter() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mFeedbackQuestionsAdapter = new FeedbackQuestionsAdapter(this, mFeedbackQuestions);
        mRecyclerView.setAdapter(mFeedbackQuestionsAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnItemTouchListener(new CategoriesAdapter(this, new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        }));
    }
}
