package com.elementary.mualijpro.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.elementary.mualijpro.R;
import com.elementary.mualijpro.dialogs.DialogChat;
import com.elementary.mualijpro.ui.activities.launcher.SplashActivity;
import com.elementary.mualijpro.ui.activities.main.MainActivity;
import com.elementary.mualijpro.utils.AppSP;
import com.elementary.mualijpro.utils.AppUtils;
import com.elementary.mualijpro.utils.Calender;
import com.elementary.mualijpro.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONObject;


import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    int min = 20;
    int max = 1000;
    String notificationType = "";
    String appointmentId = "";
    int notifyId = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage == null)
            return;


        if (remoteMessage.getData().size() > 0) {
            try {

                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {

            }
        }

    }

    private void handleDataMessage(JSONObject json) {
        String title = "";
        String message = "";

        try {


            JSONObject data = json.getJSONObject("notification");
            JSONObject obj = data.getJSONObject("obj");
            if (obj.has("notification_type"))
                notificationType = obj.getString("notification_type");
            else
                notificationType = Constants.appointmentType;
            if (obj.has("appointment_id"))
                appointmentId = obj.getString("appointment_id");
            if (!(obj.getString("title").equalsIgnoreCase(""))) {
                title = obj.getString("title");
            }
            if (!(obj.getString("message").equalsIgnoreCase(""))) {
                message = obj.getString("message");
            }

            if (obj.has("id"))
                notifyId = obj.getInt("id");
            else
                notifyId = (int) System.currentTimeMillis();

            if (AppSP.Companion.getInstance(this).readBool(Constants.isLoggedIn, false)) {
                if (notificationType.equalsIgnoreCase(Constants.chatType)) {
                    if (!DialogChat.Companion.isChatForeground())
                        sendNotification(notificationType, appointmentId, title, message, notifyId);
                    else if (!DialogChat.Companion.getAppointmentID().equalsIgnoreCase(appointmentId) && !DialogChat.Companion.getAppointmentID().isEmpty()) {
                        sendNotification(notificationType, appointmentId, title, message, notifyId);
                    }
                } else {
                    sendNotification(notificationType, appointmentId, title, message, notifyId);
                }
            }


        } catch (Exception e) {
        }
    }

    private void sendNotification(String notificationType, String appointmentId, String title, String message, int notifyId) {

      /*  int notificationId;
        if (notificationType.equalsIgnoreCase(Constants.chatType))
            notificationId = 0;
        else*/
        int notificationId = /*new Random().nextInt((max - min) + 1) + min*/notifyId;

        final Uri alarmSound = Uri.parse("android.resource://"
                + getPackageName() + "/"
                + R.raw.aa_notify_sound);

        PendingIntent pendingIntent;
        Intent intent;

        if (AppUtils.Companion.appIsInBackground())
            intent = new Intent(this, SplashActivity.class);
        else
            intent = new Intent(this, MainActivity.class);


        intent.putExtra("isComingFromNotification", true);
        intent.putExtra("notificationType", notificationType);
        intent.putExtra("appointmentId", appointmentId);
        intent.putExtra(Constants.intentConstKey, Constants.intentConstValue);
        pendingIntent = PendingIntent.getActivity(this, notifyId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon_noti);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setLargeIcon(icon)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setGroupSummary(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLights(getResources().getColor(R.color.colorPrimary), 1000, 300)
                .setSmallIcon(R.drawable.app_round_icon_noti);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());

        // Notification Channel is required for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            NotificationChannel channel = new NotificationChannel(
                    "channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(message);
            channel.setShowBadge(false);
            channel.enableLights(true);
            channel.setLightColor(getResources().getColor(R.color.colorPrimary));
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            channel.setSound(alarmSound, attributes);
            notificationManager.createNotificationChannel(channel);

        }

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
}
