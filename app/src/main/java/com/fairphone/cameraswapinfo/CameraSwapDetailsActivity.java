package com.fairphone.cameraswapinfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Display information and instructions on what to do after a camera replacement.
 */
public class CameraSwapDetailsActivity extends Activity implements View.OnClickListener {

    Button mButtonGotIt;
    Button mButtonRemindLater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camera_swap_details_activity);
        setInstructionsText();
        configureButtons();
    }

    @Override
    public void onClick(View view) {
        if (view == mButtonGotIt) {
            CameraSwapNotificationService.startActionAcknowledgeCameraChanged(this);
        } else if (view == mButtonRemindLater) {
            CameraSwapNotificationService.startActionRemindCameraChangedLater(this);
        }
        finish();
    }

    private void configureButtons() {
        mButtonGotIt = (Button) findViewById(R.id.button_got_it);
        mButtonGotIt.setOnClickListener(CameraSwapDetailsActivity.this);
        mButtonRemindLater = (Button) findViewById(R.id.button_remind_later);
        mButtonRemindLater.setOnClickListener(CameraSwapDetailsActivity.this);
    }

    private void setInstructionsText() {
        TextView titleView = (TextView) findViewById(R.id.title);
        TextView part1View = (TextView) findViewById(R.id.part1);

        int amountOfCameras = 1;

        if (!CameraSwapInfoPreferences.hasFrontCameraChanged(this)) {
            part1View.setText(getString(R.string.camera_swap_text_part1_main));
        } else if (!CameraSwapInfoPreferences.hasMainCameraChanged(this)) {
            part1View.setText(getString(R.string.camera_swap_text_part1_front));
        } else {
            amountOfCameras = 2;
            part1View.setText(getString(R.string.camera_swap_text_part1_both));
        }

        titleView.setText(getResources().getQuantityString(R.plurals.camera_swap_title, amountOfCameras));
    }
}
