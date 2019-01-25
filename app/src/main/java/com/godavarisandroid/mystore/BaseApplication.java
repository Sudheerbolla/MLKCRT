package com.godavarisandroid.mystore;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.godavarisandroid.mystore.models.NextDelivery;
import com.godavarisandroid.mystore.models.Notification;

import org.json.JSONObject;

import java.util.ArrayList;

public class BaseApplication extends Application {

    public static final String TAG = BaseApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static BaseApplication mInstance;
    public static String walletAmount, rechargeAmount, referralAmount, lastMonthDue, totalAmountDue, rechargeMessage;
    public static ArrayList<NextDelivery> mNextDelivery = new ArrayList<>();
    public static ArrayList<Notification> mNotificationList;
    public static String nextDeliveryDate, feedbackId;
    public static JSONObject adsObj = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mNotificationList = new ArrayList<>();
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
