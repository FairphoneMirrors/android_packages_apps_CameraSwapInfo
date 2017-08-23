package com.fairphone.cameraswapinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;

/**
 * Display information and instructions what to do after a camera replacement.
 *
 * Defaults to loading a support article in a WebView. Shows limited information in case of
 * lacking internet connection.
 */
public class CameraSwapDetailsActivity extends Activity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private static final String MODULE_DETECT_PACKAGE = "com.fairphone.moduledetect";
    private static final String CAMERA_SWAP_INTENT_SERVICE_CLASS =
            "com.fairphone.moduledetect.CameraSwapIntentService";
    private static final String NOTIFICATION_DISMISS_ACTION =
            "com.fairphone.moduledetect.notification_dismiss";

    Button mButtonClose;
    CompoundButton mDontShowAgainCheckbox;
    boolean mDontShowAgain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Try loading an online support article by default. */
        setContentView(R.layout.camera_swap_details_activity);
        WebView webview = (WebView) findViewById(R.id.camera_swap_webview);

        webview.setWebViewClient(new WebViewClient() {
            private boolean shouldDismissNotification = true;

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (shouldDismissNotification) {
                    /* We do not want to bother users again if they have seen the information.
                       Do not show notification again if support article has successfully loaded. */
                    dismissNotification();
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request,
                                        WebResourceError error) {
                shouldDismissNotification = false;
                /* Show a limited offline view in case of connectivity problems. */
                replaceWithOfflineContent();
            }
        });

        webview.loadUrl(getString(R.string.camera_swap_url));
    }

    @Override
    public void onClick(View view) {
        if (view == mButtonClose) {
            if (mDontShowAgain) {
                dismissNotification();
            }
            finish();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == mDontShowAgainCheckbox) {
            mDontShowAgain = b;
        }
    }

    private void replaceWithOfflineContent() {
        View view = findViewById(R.id.camera_swap_webview_fragment);
        ViewGroup parent = (ViewGroup) view.getParent();
        int index = parent.indexOfChild(view);
        parent.removeView(view);

        View replacement = getLayoutInflater().inflate(R.layout.offline_details_fragment,
                parent, false);
        parent.addView(replacement, index);

        mButtonClose = (Button) findViewById(R.id.button_close);
        mButtonClose.setOnClickListener(CameraSwapDetailsActivity.this);
        mDontShowAgainCheckbox = (CompoundButton) findViewById(R.id.dont_show_again_checkbox);
        mDontShowAgainCheckbox.setOnCheckedChangeListener(CameraSwapDetailsActivity.this);
    }

    private void dismissNotification() {
        Intent dismissIntent = new Intent(NOTIFICATION_DISMISS_ACTION);
        dismissIntent.setClassName(MODULE_DETECT_PACKAGE, CAMERA_SWAP_INTENT_SERVICE_CLASS);
        startService(dismissIntent);
    }
}
