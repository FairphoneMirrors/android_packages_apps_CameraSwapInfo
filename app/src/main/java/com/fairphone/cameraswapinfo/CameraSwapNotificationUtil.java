package com.fairphone.cameraswapinfo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class CameraSwapNotificationUtil {
    static final String PACKAGE_CAMERA_SWAP_INFO = "com.fairphone.cameraswapinfo";
    static final String ACTIVITY_CAMERA_SWAP_INFO_DETAILS =
            "com.fairphone.cameraswapinfo.CameraSwapDetailsActivity";
    static final int NOTIFICATION_ID = 0;

    static protected String getNotificationTitle(Context context) {
        return context.getString(R.string.camera_swap_notification_title);
    }

    static protected String getNotificationShortSummary(Context context) {
        return context.getString(R.string.camera_swap_notification_summary);
    }

    static protected String getNotificationExtendedSummary(Context context) {
        return context.getString(R.string.camera_swap_notification_summary) + " "
                + context.getString(R.string.camera_swap_notification_text);
    }

    public static void showNotification(Context context) {
        Notification notification = getNotification(context);
        CameraSwapInfoPreferences.setNotificationNeedsDismissal(context, true);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public static void destroyNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    static private Notification getNotification(Context context) {
        PendingIntent openIntent = getPendingIntent(context);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_stat_camera_swap)
                        .setContentTitle(getNotificationTitle(context))
                        .setContentText(getNotificationShortSummary(context))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(
                                getNotificationExtendedSummary(context)))
                        .setColor(context.getResources().getColor(R.color.colorPrimary))
                        .setPriority(Notification.PRIORITY_MAX)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setContentIntent(openIntent)
                        .setOngoing(true);
        return mBuilder.build();
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(PACKAGE_CAMERA_SWAP_INFO, ACTIVITY_CAMERA_SWAP_INFO_DETAILS);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
