package com.fairphone.cameraswapinfo;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CameraSwapNotificationService extends IntentService {

    static final String ACTION_HANDLE_CAMERA_CHANGED =
                    "com.fairphone.cameraswapinfo.handle_camera_change";
    static final String TAG = "CameraSwapInfo";


    public CameraSwapNotificationService() {
        super("CameraSwapNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_HANDLE_CAMERA_CHANGED.equals(action)) {
                handleCameraChanged(intent);
            }
        }
    }

    private void handleCameraChanged(Intent intent) {
        boolean hasFrontCameraChanged = intent.getBooleanExtra("frontCameraChanged", false);
        boolean hasMainCameraChanged = intent.getBooleanExtra("mainCameraChanged", false);

        if (!hasFrontCameraChanged && !hasMainCameraChanged) {
            Log.w(TAG, "handleCameraChanged: No camera changed. Doing Nothing.");
            return;
        }

        /* persist the changed cameras */
        CameraSwapInfoPreferences.setFrontCameraChanged(this, hasFrontCameraChanged);
        CameraSwapInfoPreferences.setMainCameraChanged(this, hasMainCameraChanged);

        /* we have to be able to remind user on next boot */
        BootUpReceiver.enable(this);

        CameraSwapNotificationUtil.showNotification(this);
    }
}
