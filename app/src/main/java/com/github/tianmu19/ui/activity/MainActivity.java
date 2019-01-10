package com.github.tianmu19.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.tianmu19.Constants;
import com.github.tianmu19.R;
import com.github.tianmu19.baselibrary.interf.IPermissionCallback;
import com.github.tianmu19.baselibrary.utils.klogutil.KLog;
import com.github.tianmu19.baselibrary.utils.permission.PermissionUtils;
import com.king.zxing.Intents;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KLog.e("oncreate");
        PermissionUtils.getInstance().checkCameraPermission(this, new IPermissionCallback() {
            @Override
            public void onGranted() {
                KLog.e("onGranted");
                startScan();
            }


            @Override
            public void onDenied() {
                KLog.e("onDenied");
            }
        });
    }

    /**
     * 扫码
     */
    private void startScan() {
        Intent intent = new Intent(this, CustomCaptureActivity.class);
        startActivityForResult(intent, Constants.REQUEST_SCAN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_SCAN_CODE) {
                String result = data.getStringExtra(Intents.Scan.RESULT);
                KLog.e("--> " + result);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}
