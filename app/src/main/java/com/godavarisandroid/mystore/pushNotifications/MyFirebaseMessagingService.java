package com.godavarisandroid.mystore.pushNotifications;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by USER on 17/11/17.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("getBody", "" + remoteMessage.getNotification().getBody());
        Log.e("getTitle", "" + remoteMessage.getNotification().getTitle());
        Log.e("getIcon", "" + remoteMessage.getNotification().getIcon());
        Log.e("getSound", "" + remoteMessage.getData());
    }

    @Override
    public void zzm(Intent intent) {
        Log.d("title", "" + intent.getStringExtra("gcm.notification.title"));
        Log.d("body", "" + intent.getStringExtra("gcm.notification.body"));

        /*On click on notification, app will open with splash screen*/
        Intent homeIntent = new Intent(this, SplashActivity.class);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, homeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        showNotification(intent, resultPendingIntent);
    }

    private void showNotification(Intent intent, PendingIntent resultPendingIntent) {
        int color = 0x2F5190;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = null;

       /* Bitmap bitmap = getBitmapFromURL(intent.getStringExtra("gcm.notification.icon"));
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.bigPicture(bitmap);
*/
        /* Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Hello";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = null;
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                mChannel = new NotificationChannel("1", name, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            notificationBuilder = new NotificationCompat.Builder(this, "1")
                    .setSmallIcon(R.mipmap.app_icon)
                    /* .setLargeIcon(rawBitmap)*/
                    .setColor(color)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(resultPendingIntent)
                    .setContentTitle(intent.getStringExtra("gcm.notification.title"))
                    .setContentText(intent.getStringExtra("gcm.notification.body"))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(intent.getStringExtra("gcm.notification.body")))
                    .setAutoCancel(true);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.app_icon)
                    /*.setLargeIcon(rawBitmap)*/
                    .setColor(color)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(resultPendingIntent)
                    .setContentTitle(intent.getStringExtra("gcm.notification.title"))
                    .setContentText(intent.getStringExtra("gcm.notification.body"))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(intent.getStringExtra("gcm.notification.body")))
                    .setAutoCancel(true);
        }

      /*  if (intent.getStringExtra("gcm.notification.icon") != null && !intent.getStringExtra("gcm.notification.icon").equalsIgnoreCase("")) {
            notificationBuilder.setStyle(bigPictureStyle);
        }*/

        notificationManager.notify(0, notificationBuilder.build());
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}