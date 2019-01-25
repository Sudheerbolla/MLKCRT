package com.godavarisandroid.mystore.webUtils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.godavarisandroid.mystore.BaseApplication;
import com.godavarisandroid.mystore.interfaces.IParseListener;
import com.godavarisandroid.mystore.utils.UserDetails;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by CHITTURI on 06-Oct-16.
 */

public class ServerResponse {
    private int requestCode;
    private Context mContext;
    private RequestQueue requestQueue;

    public void serviceRequestStringBuilder(String url) {
        String tag_json_obj = "json_obj_req";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void serviceRequestStringBuilder(Context mContext, final String url, StringBuilder params, final IParseListener iParseListener, final int requestCode) {
        this.requestCode = requestCode;
        final HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Token", UserDetails.getInstance(mContext).getUserToken());

        Log.d("the url request is", url + params);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response is", response);
                        iParseListener.SuccessResponse(response, requestCode);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String body = "";
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            if (error.networkResponse.data != null) {
                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                    Log.e("the error is", body);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            iParseListener.ErrorResponse(body, requestCode);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() {
                return hashMap;
            }
        };
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext);
        }
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void serviceRequestString(Context mContext, final String url, final HashMap<String, String> params,
                                     final IParseListener iParseListener, final int requestCode) {
        this.requestCode = requestCode;
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("Token", UserDetails.getInstance(mContext).getUserToken());

        Log.d("the url request is", url + params);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response is", response);
                        iParseListener.SuccessResponse(response, requestCode);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    String body = "";
                    String statusCode = String.valueOf(error.networkResponse.statusCode);
                    if (error.networkResponse.data != null) {
                        try {
                            body = new String(error.networkResponse.data, "UTF-8");
                            Log.e("the error is", body);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    iParseListener.ErrorResponse(body, requestCode);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext);
        }
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void serviceRequestJSonObject(Context mContext, String methodType, final String url, final JSONObject params, final Map<String, String> headers, final IParseListener iParseListener, final int requestCode) {
        this.requestCode = requestCode;
        int method = Request.Method.GET;
        if (methodType.equalsIgnoreCase("POST")) {
            method = Request.Method.POST;
        }

        //Utility.showLog("Params is", ""+params);
        Log.d(TAG, "serviceRequestJSonObject: Params...." + params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response is", response.toString());
                iParseListener.SuccessResponse("" + response, requestCode);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    String body = "";
                    String statusCode = String.valueOf(error.networkResponse.statusCode);
                    if (error.networkResponse.data != null) {
                        try {
                            body = new String(error.networkResponse.data, "UTF-8");
                            Log.e("the error is", body);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    iParseListener.ErrorResponse(body, requestCode);
                } else if (!TextUtils.isEmpty(error.getLocalizedMessage())) {
                    iParseListener.ErrorResponse(error.getLocalizedMessage(), requestCode);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext);
        }
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
        jsonObjectRequest.setRetryPolicy(new MyRetryPolicyWithoutRetry());
    }

    public class MyRetryPolicyWithoutRetry implements RetryPolicy {
        public int getCurrentTimeout() {
            return 200000;/* /200000 /*/
        }

        @Override
        public int getCurrentRetryCount() {
            return 0;
        }

        @Override
        public void retry(VolleyError error) throws VolleyError {
            throw (error);
        }
    }

}
