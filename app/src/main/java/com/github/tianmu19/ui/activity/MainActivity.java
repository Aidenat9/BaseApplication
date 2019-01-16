package com.github.tianmu19.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.tianmu19.Constants;
import com.github.tianmu19.R;
import com.github.tianmu19.baselibrary.interf.IPermissionCallback;
import com.github.tianmu19.baselibrary.utils.klogutil.KLog;
import com.github.tianmu19.baselibrary.utils.permission.PermissionUtils;
import com.github.tianmu19.utils.ToastUtil;
import com.king.zxing.Intents;

public class MainActivity extends AppCompatActivity {

    private MainActivity mContext;
    private View btn_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        KLog.e("oncreate");
        btn_scan = findViewById(R.id.btn_scan);
    }

    @Override
    protected void onStart() {
        super.onStart();
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtils.getInstance().checkCameraPermission(mContext, new IPermissionCallback() {
                    @Override
                    public void onGranted() {
                        KLog.e("onGranted");
                        ToastUtil.show(">>>>>> onGranted");
                        startScan();
                    }


                    @Override
                    public void onDenied() {
                        KLog.e("onDenied");
                    }
                });
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
