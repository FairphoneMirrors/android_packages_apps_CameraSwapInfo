package com.fairphone.cameraswapinfo;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CameraSwapNotificationService extends IntentService {

    private static final String TAG = "CameraSwapInfo";

    private static final boolean DEBUG = false;

    /**
     * Grace period before firing a reminder (in milliseconds): 1 day.
     */
    private static final long REMINDER_GRACE_PERIOD_MS = 1000 * 60 * 60 * 24;

    /**
     * Action to handle a change of camera(s).
     */
    private static final String ACTION_HANDLE_CAMERA_CHANGED =
            "com.fairphone.cameraswapinfo.handle_camera_change";

    /**
     * Action to acknowledge there was a change of camera(s).
     */
    private static final String ACTION_ACKNOWLEDGE_CAMERA_CHANGED =
            "com.fairphone.cameraswapinfo.acknowledge_camera_changed";

    /**
     * Action to remind the user at a later point (next reboot or after a grace period) that there
     * was a change of camera(s).
     */
    private static final String ACTION_REMIND_CAMERA_CHANGED_LATER =
            "com.fairphone.cameraswapinfo.remind_camera_changed_later";

    public static void startActionCameraChanged(Context context) {
        boolean hasFrontCameraChanged = CameraSwapInfoPreferences.hasFrontCameraChanged(context);
        boolean hasMainCameraChanged = CameraSwapInfoPreferences.hasMainCameraChanged(context);

        context.startService(
                new Intent(context, CameraSwapNotificationService.class)
                        .setAction(ACTION_HANDLE_CAMERA_CHANGED)
                        .putExtra("frontCameraChanged", hasFrontCameraChanged)
                        .putExtra("mainCameraChanged", hasMainCameraChanged));
    }

    public static void startActionAcknowledgeCameraChanged(Context context) {
        context.startService(
                new Intent(context, CameraSwapNotificationService.class)
                        .setAction(ACTION_ACKNOWLEDGE_CAMERA_CHANGED));
    }

    public static void startActionRemindCameraChangedLater(Context context) {
        context.startService(
                new Intent(context, CameraSwapNotificationService.class)
                        .setAction(ACTION_REMIND_CAMERA_CHANGED_LATER));
    }

    public CameraSwapNotificationService() {
        super("CameraSwapNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_HANDLE_CAMERA_CHANGED.equals(action)) {
                handleCameraChanged(intent);
            } else if (ACTION_ACKNOWLEDGE_CAMERA_CHANGED.equals(action)) {
                handleAcknowledgeCameraChanged();
            } else if (ACTION_REMIND_CAMERA_CHANGED_LATER.equals(action)) {
                handleRemindCameraChangedLater();
            }
        }
    }

    private void handleCameraChanged(Intent intent) {
        boolean hasFrontCameraChanged = intent.getBooleanExtra("frontCameraChanged", false);
        boolean hasMainCameraChanged = intent.getBooleanExtra("mainCameraChanged", false);

        if (!hasFrontCameraChanged && !hasMainCameraChanged) {
            Log.w(TAG, "No change of camera detected, ignoring");
            return;
        }

        /* persist the changed cameras */
        CameraSwapInfoPreferences.setFrontCameraChanged(this, hasFrontCameraChanged);
        CameraSwapInfoPreferences.setMainCameraChanged(this, hasMainCameraChanged);

        /* we have to be able to remind user on next boot */
        BootUpReceiver.enable(this);

        CameraSwapNotificationUtil.showNotification(this);
    }

    private void handleAcknowledgeCameraChanged() {
        CameraSwapNotificationUtil.destroyNotification(this);

        // The camera change is complete
        CameraSwapInfoPreferences.setFrontCameraChanged(this, false);
        CameraSwapInfoPreferences.setMainCameraChanged(this, false);

        // We do not need to remind the user on the next boot
        BootUpReceiver.disable(this);

        if (DEBUG) Log.d(TAG, "Acknowledged the camera change");
    }

    private void handleRemindCameraChangedLater() {
        boolean hasFrontCameraChanged = CameraSwapInfoPreferences.hasFrontCameraChanged(this);
        boolean hasMainCameraChanged = CameraSwapInfoPreferences.hasMainCameraChanged(this);

        CameraSwapNotificationUtil.destroyNotification(this);

        // Trigger the camera changed action after the grace period
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, System.currentTimeMillis() + REMINDER_GRACE_PERIOD_MS,
                        PendingIntent.getService(this, 0,
                                new Intent(this, CameraSwapNotificationService.class)
                                        .setAction(ACTION_HANDLE_CAMERA_CHANGED)
                                        .putExtra("frontCameraChanged", hasFrontCameraChanged)
                                        .putExtra("mainCameraChanged", hasMainCameraChanged),
                                PendingIntent.FLAG_UPDATE_CURRENT));
        if (DEBUG) Log.d(TAG, "Alarm set to trigger " + ACTION_HANDLE_CAMERA_CHANGED
                + " once again in " + REMINDER_GRACE_PERIOD_MS / 1000 + " seconds");
    }
}
