package com.fairphone.cameraswapinfo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            CameraSwapNotificationService.startActionCameraChanged(context);
        }
    }

    public static void disable(Context context) {
        PackageManager pm = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, BootUpReceiver.class);
        pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void enable(Context context) {
        PackageManager pm = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, BootUpReceiver.class);
        pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
}
