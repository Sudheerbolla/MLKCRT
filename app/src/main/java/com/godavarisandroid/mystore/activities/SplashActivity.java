package com.godavarisandroid.mystore.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.utils.PopUtils;
import com.godavarisandroid.mystore.utils.UserDetails;
import com.godavarisandroid.mystore.webUtils.ServerResponse;
import com.godavarisandroid.mystore.webUtils.WebServices;
import com.godavarisandroid.mystore.webUtils.WsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SplashActivity extends BaseActivity implements IParseListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        requestForCheckVersionWS();
    }

    private void setSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(UserDetails.getInstance(SplashActivity.this).getUserId())) {
                    navigateActivity(new Intent(SplashActivity.this, LoginActivity.class), true);
                } else {
                    requestForLoginWS();
                }
            }
        }, 1000);
    }

    private void requestForLoginWS() {
        HashMap<String, String> params = new HashMap<>();
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "user_validation");
            mJsonObject.put("phone_no", UserDetails.getInstance(this).getPhoneNo());
            mJsonObject.put("token_id", UserDetails.getInstance(this).getPushToken());
            mJsonObject.put("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
            mJsonObject.put("device_type", "Android");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params,
                    this, WsUtils.WS_CODE_USER_VALIDATION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getVersionCode() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "0.0.1";
    }

    private void requestForCheckVersionWS() {
        HashMap<String, String> params = new HashMap<>();
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("action", "verstion_control");
            mJsonObject.put("version", getVersionCode());
            mJsonObject.put("device_type", "ANDROID");
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequestJSonObject(this, "POST", WebServices.BASE_URL, mJsonObject, params, this, WsUtils.WS_CODE_VERSION_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ErrorResponse(String response, int requestCode) {
        PopUtils.alertDialog(this, response, null);
    }

    @Override
    public void SuccessResponse(String response, int requestCode) {
        switch (requestCode) {
            case WsUtils.WS_CODE_VERSION_CODE:
                responseForCheckVersionCode(response);
                break;
            case WsUtils.WS_CODE_USER_VALIDATION:
                responseForLoginResponse(response);
                break;
            default:
                break;
        }
    }

    private void responseForLoginResponse(String response) {
        if (response != null) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                String message = mJsonObject.optString("message");
                if (mJsonObject.getString("status").equalsIgnoreCase("200")) {
                    UserDetails.getInstance(this).setUserToken(mJsonObject.optString("token"));
                    UserDetails.getInstance(this).setCutOffTime(mJsonObject.optString("cut_of_time"));
                    JSONArray mDataArray = mJsonObject.getJSONArray("data");
                    JSONObject mDataObject = mDataArray.getJSONObject(0);
                    UserDetails.getInstance(this).setUserId(mDataObject.optString("u_id"));
                    UserDetails.getInstance(this).setName(mDataObject.optString("name"));
                    UserDetails.getInstance(this).setEmailId(mDataObject.optString("email"));
                    UserDetails.getInstance(this).setPhoneNo(mDataObject.optString("phone_no"));
                    UserDetails.getInstance(this).setCityId(mDataObject.optString("city"));
                    UserDetails.getInstance(this).setAreaId(mDataObject.optString("area"));
                    UserDetails.getInstance(this).setApartmentId(mDataObject.optString("apartment"));
                    UserDetails.getInstance(this).setBlock(mDataObject.optString("block"));
                    UserDetails.getInstance(this).setFlat(mDataObject.optString("flat"));
                    UserDetails.getInstance(this).setStreet(mDataObject.optString("street"));
                    UserDetails.getInstance(this).setLandmark(mDataObject.optString("landmark"));
                    UserDetails.getInstance(this).setPincode(mDataObject.optString("pincode"));
                    UserDetails.getInstance(this).setCity(mDataObject.optString("city_name"));
                    UserDetails.getInstance(this).setArea(mDataObject.optString("area_name"));
                    UserDetails.getInstance(this).setPaymentType(mDataObject.optString("payment_type"));
                    UserDetails.getInstance(this).setReferralCode(mDataObject.optString("referal_code"));
                    if (mJsonObject.has("is_enable_referal_code")) {
                        UserDetails.getInstance(this).setIsReferralCodeEnabled(Boolean.parseBoolean(mJsonObject.optString("is_enable_referal_code")));
                    }
                    if (mDataObject.optString("apartment_name").equalsIgnoreCase("null")) {
                        UserDetails.getInstance(this).setApartment("");
                    } else {
                        UserDetails.getInstance(this).setApartment(mDataObject.optString("apartment_name"));
                    }

                    try {
                        boolean needToShowRechargeBtn = mJsonObject.optBoolean("enable_recharge_btn");
                        UserDetails.getInstance(this).setIsEnableRechargeButton(needToShowRechargeBtn);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                } else if (mJsonObject.getString("status").equalsIgnoreCase("400")) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                navigateActivity(new Intent(this, HomeActivity.class), true);
            }
        }
    }

    private void responseForCheckVersionCode(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject mJsonObject = new JSONObject(response);
                if (mJsonObject.has("status")) {
                    if (mJsonObject.getInt("status") == 200) {
                        // no need
                        setSplashScreen();
                    } else if (mJsonObject.getInt("status") == 401) {
                        //need to update
                        String isMand = ((JSONObject) mJsonObject.optJSONArray("data").get(0)).optString("is_mandatry");
                        openPlayStore(TextUtils.isEmpty(isMand) ? "No" : isMand);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void openPlayStore(String isMandatry) {
        boolean isMandatory = !isMandatry.equalsIgnoreCase("No");
        try {
            PopUtils.versionUpdateDialog(this, "New Version Avaiable. Please update it from Playstore", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                    intent.setPackage("com.android.vending");
                    startActivity(intent);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSplashScreen();
                }
            }, isMandatory);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "New Version Avaiable. Please update it from Playstore", Toast.LENGTH_LONG).show();
            setSplashScreen();
        }
    }
}
