package com.godavarisandroid.mystore.pushNotifications;

/**
 * Created by USER on 14/11/2017.
 */

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.godavarisandroid.mystore.utils.UserDetails;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

/**
 * Created by USER on 17-11-17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent("REGISTRATION_COMPLETE");
        registrationComplete.putExtra("token", token);
        UserDetails.getInstance(getApplicationContext()).setPushToken(token);
        Log.e(TAG, "Sent token1111: " + token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}