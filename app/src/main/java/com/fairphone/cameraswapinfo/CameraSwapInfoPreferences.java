package com.fairphone.cameraswapinfo;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CameraSwapInfoPreferences {
    private static final String PREF_HAS_FRONT_CAMERA_CHANGED =
            "com.fairphone.cameraswapinfo.pref_has_front_camera_changed";
    private static final String PREF_HAS_MAIN_CAMERA_CHANGED =
            "com.fairphone.cameraswapinfo.pref_has_main_camera_changed";
    private static final String PREF_NOTIFICATION_IS_DISMISSED =
            "com.fairphone.moduledetect.notification_needs_dismissal";


    static void setFrontCameraChanged(Context context, boolean hasChanged) {
        SharedPreferences preferenceManager =
                PreferenceManager.getDefaultSharedPreferences(context);
        preferenceManager.edit().putBoolean(PREF_HAS_FRONT_CAMERA_CHANGED, hasChanged).apply();
    }

    static boolean hasFrontCameraChanged(Context context) {
        SharedPreferences preferenceManager =
                PreferenceManager.getDefaultSharedPreferences(context);
        return preferenceManager.getBoolean(PREF_HAS_FRONT_CAMERA_CHANGED, false);
    }

    static void setMainCameraChanged(Context context, boolean hasChanged) {
        SharedPreferences preferenceManager =
                PreferenceManager.getDefaultSharedPreferences(context);
        preferenceManager.edit().putBoolean(PREF_HAS_MAIN_CAMERA_CHANGED, hasChanged).apply();
    }

    static boolean hasMainCameraChanged(Context context) {
        SharedPreferences preferenceManager =
                PreferenceManager.getDefaultSharedPreferences(context);
        return preferenceManager.getBoolean(PREF_HAS_MAIN_CAMERA_CHANGED, false);
    }

    static int getAmountOfCamerasChanged(Context context) {
        SharedPreferences preferenceManager =
                PreferenceManager.getDefaultSharedPreferences(context);
        int amount = 0;

        if (preferenceManager.getBoolean(PREF_HAS_FRONT_CAMERA_CHANGED, false)) {
            amount += 1;
        }
        if (preferenceManager.getBoolean(PREF_HAS_MAIN_CAMERA_CHANGED, false)) {
            amount += 1;
        }

        return amount;
    }

    static boolean doesNotificationNeedDismissal(Context context) {
        SharedPreferences preferenceManager =
                PreferenceManager.getDefaultSharedPreferences(context);
        return preferenceManager.getBoolean(PREF_NOTIFICATION_IS_DISMISSED, false);
    }


    static void setNotificationNeedsDismissal(Context context, boolean needsDismissal) {
        SharedPreferences preferenceManager =
                PreferenceManager.getDefaultSharedPreferences(context);
        preferenceManager.edit().putBoolean(PREF_NOTIFICATION_IS_DISMISSED, needsDismissal).apply();
    }
}
