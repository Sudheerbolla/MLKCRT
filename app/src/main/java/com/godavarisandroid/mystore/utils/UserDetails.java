package com.godavarisandroid.mystore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Excentd11 on 8/8/2017.
 */

public class UserDetails {
    public static final String PREF_NAME = "Medix";
    private static UserDetails instance;
    private SharedPreferences sh;
    private SharedPreferences.Editor edit;

    private UserDetails(Context mContext) {
        sh = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        edit = sh.edit();
    }

    public static synchronized UserDetails getInstance(Context mContext) {
        if (instance == null) {
            instance = new UserDetails(mContext);
        }
        return instance;
    }

    public void clear() {
        edit.clear().commit();
    }

    public void setUserId(String userId) {
        edit.putString("user_id", userId).commit();
    }

    public void setName(String name) {
        edit.putString("name", name).commit();
    }

    public void setEmailId(String emailId) {
        edit.putString("email_id", emailId).commit();
    }

    public void setPhoneNo(String phoneNo) {
        edit.putString("phone_no", phoneNo).commit();
    }

    public void setCityId(String cityId) {
        edit.putString("city_id", cityId).commit();
    }

    public void setAreaId(String areaId) {
        edit.putString("area_id", areaId).commit();
    }

    public void setApartmentId(String apartmentId) {
        edit.putString("apartment_id", apartmentId).commit();
    }

    public void setBlock(String block) {
        edit.putString("block", block).commit();
    }

    public void setFlat(String flat) {
        edit.putString("flat", flat).commit();
    }

    public void setStreet(String street) {
        edit.putString("street", street).commit();
    }

    public void setLandmark(String landmark) {
        edit.putString("landmark", landmark).commit();
    }

    public void setPincode(String pincode) {
        edit.putString("pincode", pincode).commit();
    }

    public void setCity(String pincode) {
        edit.putString("city", pincode).commit();
    }

    public void setArea(String pincode) {
        edit.putString("area", pincode).commit();
    }

    public void setReferralCode(String referalCode) {
        edit.putString("referal_code", referalCode).commit();
    }

    public void setApartment(String pincode) {
        edit.putString("apartment", pincode).commit();
    }

    public void setCutOffTime(String cutOffTime) {
        edit.putString("cut_of_time", cutOffTime).commit();
    }

    public void setPaymentType(String paymentType) {
        edit.putString("payment_type", paymentType).commit();
    }

    public String getPaymentType() {
        return sh.getString("payment_type", "PrePaid");
    }
    public void setIsReferralCodeEnabled(boolean isReferralCodeEnabled) {
        edit.putBoolean("isReferralCodeEnabled", isReferralCodeEnabled).commit();
    }

    public boolean getIsReferralCodeEnabled() {
        return sh.getBoolean("isReferralCodeEnabled", false);
    }

    public void setIsEnableRechargeButton(boolean isEnableRechargeButton) {
        edit.putBoolean("enable_recharge_btn", isEnableRechargeButton).commit();
    }

    public boolean getIsEnableRechargeButton() {
        return sh.getBoolean("enable_recharge_btn", false);
    }

    public String getCutOffTime() {
        return sh.getString("cut_of_time", "21");
    }

    public String getUserId() {
        return sh.getString("user_id", "");
    }

    public String getName() {
        return sh.getString("name", "");
    }

    public String getReferalCode() {
        return sh.getString("referal_code", "");
    }

    public String getEmailId() {
        return sh.getString("email_id", "");
    }

    public String getPhoneNo() {
        return sh.getString("phone_no", "");
    }

    public String getCityId() {
        return sh.getString("city_id", "");
    }

    public String getAreaId() {
        return sh.getString("area_id", "");
    }

    public String getApartmentId() {
        return sh.getString("apartment_id", "");
    }

    public String getBlock() {
        return sh.getString("block", "");
    }

    public String getFlat() {
        return sh.getString("flat", "");
    }

    public String getStreet() {
        return sh.getString("street", "");
    }

    public String getLandmark() {
        return sh.getString("landmark", "");
    }

    public String getPincode() {
        return sh.getString("pincode", "");
    }

    public String getCity() {
        return sh.getString("city", "");
    }

    public String getArea() {
        return sh.getString("area", "");
    }

    public String getApartment() {
        return sh.getString("apartment", "");
    }


    public void setUserToken(String userToken) {
        edit.putString("user_token", userToken).commit();
    }

    public String getUserToken() {
        return sh.getString("user_token", "");
    }

    public void setPushToken(String pushToken) {
        edit.putString("push_token", pushToken).commit();
    }

    public String getPushToken() {
        return sh.getString("push_token", "");
    }
}
