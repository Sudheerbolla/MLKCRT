package com.godavarisandroid.mystore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.adapters.AutoCompleteArrayAdapter;
import com.godavarisandroid.mystore.interfaces.DialogListInterface;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.models.DialogList;
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

public class EditAddressActivity extends BaseActivity implements View.OnClickListener, IParseListener {
    private TextView mTxtSave, mTxtTitle;
    private ImageView mImgLogo, mImgBack, mImgNotification;
    private EditText mEdtCity, mEdtArea, mEdtBlock, mEdtFlat, mEdtStreet, mEdtLandmark, mEdtPincode;
    private AutoCompleteTextView mEdtApartment;

    private String cityId = "", areaId = "", apartmentId = "";

    private ArrayList<DialogList> mDialogList, mApartmentsList;
    private AutoCompleteArrayAdapter autoCompleteArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        mDialogList = new ArrayList<>();
        mApartmentsList = new ArrayList<>();

        cityId = UserDetails.getInstance(this).getCityId();
        areaId = UserDetails.getInstance(this).getAreaId();
        apartmentId = UserDetails.getInstance(this).getApartmentId();

        requestForApartmentsWS();

        initComponents();
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
    }

    /*Initializing Views*/
    private void setReferences() {
        mTxtTitle = findViewById(R.id.txtTitle);
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText("Edit Address");

        mTxtSave = findViewById(R.id.txtSave);
        mImgLogo = findViewById(R.id.imgLogo);
        mImgLogo.setVisibility(View.GONE);

        mImgBack = findViewById(R.id.imgBack);
        mImgBack.setVisibility(View.VISIBLE);
        mImgNotification = findViewById(R.id.imgNotification);
        mImgNotification.setVisibility(View.VISIBLE);

        mEdtCity = findViewById(R.id.edtCity);
        mEdtCity.setText(UserDetails.getInstance(this).getCity());
        mEdtArea = findViewById(R.id.edtArea);
        mEdtArea.setText(UserDetails.getInstance(this).getArea());
        mEdtApartment = findViewById(R.id.edtApartment);
        mEdtApartment.setText(UserDetails.getInstance(this).getApartment());
        mEdtBlock = findViewById(R.id.edtBlock);
        mEdtBlock.setText(UserDetails.getInstance(this).getBlock());
        mEdtFlat = findViewById(R.id.edtFlat);
        mEdtFlat.setText(UserDetails.getInstance(this).getFlat());
        mEdtStreet = findViewById(R.id.edtStreet);
        mEdtStreet.setText(UserDetails.getInstance(this).getStreet());
        mEdtLandmark = findViewById(R.id.edtLandmark);
        mEdtLandmark.setText(UserDetails.getInstance(this).getLandmark());
        mEdtPincode = findViewById(R.id.edtPincode);
        mEdtPincode.setTransformationMethod(null);
        mEdtPincode.setText(UserDetails.getInstance(this).getPincode());
    }

    /*Click listeners for views*/
    private void setClickListeners() {
        mTxtSave.setOnClickListener(this);
        mImgLogo.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mEdtCity.setOnClickListener(this);
        mEdtApartment.setOnClickListener(this);
        mEdtArea.setOnClickListener(this);
        mImgNotification.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSave:
                String message = checkValidation();
                if (!TextUtils.isEmpty(message)) {
                    PopUtils.alertDialog(this, message, null);
                } else {
                    if (PopUtils.checkInternetConnection(EditAddressActivity.this)) {
                        if (!apartmentId.equalsIgnoreCase(UserDetails.getInstance(this).getApartmentId())) {
                            requestForUpdateAddressWS();
                        } else if (!TextUtils.isEmpty(mEdtApartment.getText().toString().trim()) &&
                                !UserDetails.getInstance(this).getApartment().equalsIgnoreCase(mEdtApartment.getText().toString().trim())) {
                            requestForAddApartmentWS();
                        } else {
                            requestForUpdateAddressWS();
                        }
                    } else {
                        PopUtils.alertDialog(EditAddressActivity.this, getString(R.string.pls_check_internet), null);
                    }
                }
                break;
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.edtCity:
                PopUtils.alertTwoButtonDialog(this, "All your existing subscriptions will be end dated if you change City or Area",
                        "OK", "CANCEL", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (PopUtils.checkInternetConnection(EditAddressActivity.this)) {
                                    requestForCityWS();
                                } else {
                                    PopUtils.alertDialog(EditAddressActivity.this, getString(R.string.pls_check_internet), null);
                                }
                            }
                        }, null);
                break;
            case R.id.edtArea:
                if (!TextUtils.isEmpty(mEdtCity.getText().toString().trim())) {
                    PopUtils.alertTwoButtonDialog(this, "All your existing subscriptions will be end dated if you change City or Area",
                            "OK", "CANCEL", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (PopUtils.checkInternetConnection(EditAddressActivity.this)) {
                                        requestForAreaWS();
                                    } else {
                                        PopUtils.alertDialog(EditAddressActivity.this, getString(R.string.pls_check_internet), null);
                                    }
                                }
                            }, null);
                } else {
                    Toast.makeText(this, "Please select city first", Toast.LENGTH_SHORT).show();
                }
                break;
//            case R.id.edtApartment:
//                if (!TextUtils.isEmpty(mEdtArea.getText().toString().trim())) {
//                    if (PopUtils.checkInternetConnection(EditAddressActivity.this)) {
//                        requestForApartmentsWS();
//                    } else {
//                        PopUtils.alertDialog(EditAddressActivity.this, getString(R.string.pls_check_internet), null);
//                    }
//                } else {
//                    Toast.makeText(this, "Please select area first", Toast.LENGTH_SHORT).show();
//                }
//                break;
            case R.id.imgNotification:
                navigateActivity(new Intent(this, HelpActivity.class), false);
                break;
            default:
                break;
        }
    }

    /*Called when clicking on submit button*/
    private String checkValidation() {
        String message = "";
        if (TextUtils.isEmpty(mEdtCity.getText().toString().trim())) {
            message = "Please enter your city";
        } else if (TextUtils.isEmpty(mEdtArea.getText().toString().trim())) {
            message = "Please enter your area";
        } else if (TextUtils.isEmpty(mEdtApartment.getText().toString().trim())) {
            message = "Please enter your apartment";
        } else if (TextUtils.isEmpty(mEdtFlat.getText().toString().trim())) {
            message = "Please enter your flat";
        } else if (TextUtils.isEmpty(mEdtStreet.getText().toString().trim())) {
            message = "Please enter your street";
        } else if (TextUtils.isEmpty(mEdtLandmark.getText().toString().trim())) {
            message = "Please enter your landmark";
//        } else if (TextUtils.isEmpty(mEdtPincode.getText().toString().trim())) {
//            message = "Please enter your pincode";
        } else if (TextUtils.isEmpty(mEdtPincode.getText().toString().trim()) || mEdtPincode.getText().toString().trim().length() < 6) {
            message = "Please enter valid 6 digit Pincode";
        }
        return message;
    }

    /*Requesting service call for city*/
    private void requestForCityWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "cities");
            mJsonObject.put("loc_id", "");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_CITIES);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Requesting service call for area*/
    private void requestForAreaWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "cities");
            mJsonObject.put("loc_id", cityId);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_AREAS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Requesting service call for apartments*/
    private void requestForApartmentsWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "apartments");
            mJsonObject.put("loc_id", areaId);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_APARTMENTS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Requesting service call for updating address*/
    private void requestForUpdateAddressWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "edit_address");
            if (!cityId.equalsIgnoreCase(UserDetails.getInstance(this).getCityId()) || !areaId.equalsIgnoreCase(UserDetails.getInstance(this).getAreaId())) {
                mJsonObject.put("city", cityId);
                mJsonObject.put("area", areaId);
            }
            mJsonObject.put("apartment", apartmentId);
            mJsonObject.put("block", mEdtBlock.getText().toString().trim());
            mJsonObject.put("flat", mEdtFlat.getText().toString().trim());
            mJsonObject.put("street", mEdtStreet.getText().toString().trim());
            mJsonObject.put("landmark", mEdtLandmark.getText().toString().trim());
            mJsonObject.put("pincode", mEdtPincode.getText().toString().trim());
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params, this, WsUtils.WS_CODE_UPDATE_ADDRESS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestForAddApartmentWS() {
        showLoadingDialog("Loading...", false);
        HashMap<String, String> params = new HashMap<>();
        params.put("Token", UserDetails.getInstance(this).getUserToken());
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "add_apartments");
            mJsonObject.put("apartment_name", mEdtApartment.getText().toString().trim());
            mJsonObject.put("location_id", areaId);
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params, this, WsUtils.WS_CODE_ADD_APARTMENT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Called when error occured from service call*/
    @Override
    public void ErrorResponse(String response, int requestCode) {
        hideLoadingDialog();
        PopUtils.alertDialog(this, response, new View.OnClickListener() {
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
            case WsUtils.WS_CODE_UPDATE_ADDRESS:
                responseForUpdateAddress(response);
                break;
            case WsUtils.WS_CODE_CITIES:
                responseForCities(response);
                break;
            case WsUtils.WS_CODE_AREAS:
                responseForAreas(response);
                break;
            case WsUtils.WS_CODE_APARTMENTS:
                responseForApartments(response);
                break;
            case WsUtils.WS_CODE_ADD_APARTMENT:
                responseForAddApartment(response);
                break;
            default:
                break;
        }
    }

    private void responseForAddApartment(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    UserDetails.getInstance(this).setCity(mEdtCity.getText().toString().trim());
                    UserDetails.getInstance(this).setCityId(cityId);
                    UserDetails.getInstance(this).setArea(mEdtArea.getText().toString().trim());
                    UserDetails.getInstance(this).setAreaId(areaId);
                    UserDetails.getInstance(this).setApartment(mEdtApartment.getText().toString().trim());
                    UserDetails.getInstance(this).setApartmentId(apartmentId);
                    UserDetails.getInstance(this).setBlock(mEdtBlock.getText().toString().trim());
                    UserDetails.getInstance(this).setFlat(mEdtFlat.getText().toString().trim());
                    UserDetails.getInstance(this).setStreet(mEdtStreet.getText().toString().trim());
                    UserDetails.getInstance(this).setLandmark(mEdtLandmark.getText().toString().trim());
                    UserDetails.getInstance(this).setPincode(mEdtPincode.getText().toString().trim());

                    PopUtils.alertDialog(this, "Your details are saved successfully", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("city", mEdtCity.getText().toString().trim());
                            returnIntent.putExtra("area", mEdtArea.getText().toString().trim());
                            returnIntent.putExtra("apartment", mEdtApartment.getText().toString().trim());
                            returnIntent.putExtra("block", mEdtBlock.getText().toString().trim());
                            returnIntent.putExtra("flat", mEdtFlat.getText().toString().trim());
                            returnIntent.putExtra("street", mEdtStreet.getText().toString().trim());
                            returnIntent.putExtra("landmark", mEdtLandmark.getText().toString().trim());
                            returnIntent.putExtra("pincode", mEdtPincode.getText().toString().trim());
                            setResult(RESULT_OK, returnIntent);
                            finish();
                            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                        }
                    });

                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Response for cities service call*/
    private void responseForCities(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray mDataArray = mJsonObject.getJSONArray("data");

                    mDialogList.clear();
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);

                        mDialogList.add(new DialogList(mDataObject.optString("loc_id"), mDataObject.optString("location_name")));
                    }
                    PopUtils.alertDialogList(this, mDialogList, new DialogListInterface() {
                        @Override
                        public void DialogListInterface(String id, String value) {
                            cityId = id;
                            mEdtCity.setText(value);
                            mEdtArea.setText("");
                            mEdtApartment.setText("");
                            mEdtBlock.setText("");
                            mEdtFlat.setText("");
                            mEdtStreet.setText("");
                            mEdtLandmark.setText("");
                            mEdtPincode.setText("");

                            if (PopUtils.checkInternetConnection(EditAddressActivity.this)) {
                                requestForAreaWS();
                            } else {
                                PopUtils.alertDialog(EditAddressActivity.this, getString(R.string.pls_check_internet), null);
                            }
                        }
                    }, "left");
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Response for areas service call*/
    private void responseForAreas(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray mDataArray = mJsonObject.getJSONArray("data");

                    mDialogList.clear();
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);
                        mDialogList.add(new DialogList(mDataObject.optString("loc_id"), mDataObject.optString("location_name")));
                    }

                    PopUtils.alertDialogList(this, mDialogList, new DialogListInterface() {
                        @Override
                        public void DialogListInterface(String id, String value) {
                            areaId = id;
                            mEdtArea.setText(value);
                            requestForApartmentsWS();
                        }
                    }, "left");
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Response for apartments service call*/
    private void responseForApartments(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray mDataArray = mJsonObject.getJSONArray("data");

                    mApartmentsList.clear();
                    for (int i = 0; i < mDataArray.length(); i++) {
                        JSONObject mDataObject = mDataArray.getJSONObject(i);
                        mApartmentsList.add(new DialogList(mDataObject.optString("ap_id"), mDataObject.optString("apartment_name")));
                    }

                    autoCompleteArrayAdapter = new AutoCompleteArrayAdapter(this, R.layout.item_auto_suggest, mApartmentsList);
                    mEdtApartment.setAdapter(autoCompleteArrayAdapter);
                    mEdtApartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            apartmentId = ((DialogList) parent.getItemAtPosition(position)).id;
                        }
                    });

                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Response for update address service call*/
    private void responseForUpdateAddress(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    UserDetails.getInstance(this).setCity(mEdtCity.getText().toString().trim());
                    UserDetails.getInstance(this).setCityId(cityId);
                    UserDetails.getInstance(this).setArea(mEdtArea.getText().toString().trim());
                    UserDetails.getInstance(this).setAreaId(areaId);
                    UserDetails.getInstance(this).setApartment(mEdtApartment.getText().toString().trim());
                    UserDetails.getInstance(this).setApartmentId(apartmentId);
                    UserDetails.getInstance(this).setBlock(mEdtBlock.getText().toString().trim());
                    UserDetails.getInstance(this).setFlat(mEdtFlat.getText().toString().trim());
                    UserDetails.getInstance(this).setStreet(mEdtStreet.getText().toString().trim());
                    UserDetails.getInstance(this).setLandmark(mEdtLandmark.getText().toString().trim());
                    UserDetails.getInstance(this).setPincode(mEdtPincode.getText().toString().trim());

                    PopUtils.alertDialog(this, "Your details are saved successfully", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("city", mEdtCity.getText().toString().trim());
                            returnIntent.putExtra("area", mEdtArea.getText().toString().trim());
                            returnIntent.putExtra("apartment", mEdtApartment.getText().toString().trim());
                            returnIntent.putExtra("block", mEdtBlock.getText().toString().trim());
                            returnIntent.putExtra("flat", mEdtFlat.getText().toString().trim());
                            returnIntent.putExtra("street", mEdtStreet.getText().toString().trim());
                            returnIntent.putExtra("landmark", mEdtLandmark.getText().toString().trim());
                            returnIntent.putExtra("pincode", mEdtPincode.getText().toString().trim());
                            setResult(RESULT_OK, returnIntent);
                            finish();
                            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                        }
                    });

                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Called on back button press*/
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
}
