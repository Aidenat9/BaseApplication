package com.github.tianmu19.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.github.tianmu19.Constants;
import com.github.tianmu19.R;
import com.github.tianmu19.baselibrary.interf.IPermissionCallback;
import com.github.tianmu19.baselibrary.utils.klogutil.KLog;
import com.github.tianmu19.baselibrary.utils.permission.PermissionUtils;
import com.github.tianmu19.utils.SPUtils;
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
        final ImageView imageView = findViewById(R.id.imageview);
        // 首先创建一个SpringSystem对象
        SpringSystem springSystem = SpringSystem.create();
        // 添加一个弹簧到系统
        Spring spring = springSystem.createSpring();
        //设置弹簧属性参数，如果不设置将使用默认值
        //两个参数分别是弹力系数和阻力系数
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(100, 2));
        // 添加弹簧监听器
        spring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring spring) {
                // value是一个符合弹力变化的一个数，我们根据value可以做出弹簧动画
                float value = (float) spring.getCurrentValue();
                //基于Y轴的弹簧阻尼动画
                imageView.setTranslationY(value);

                // 对图片的伸缩动画
                float scale = 1f - (value * 0.5f);
                imageView.setScaleX(scale);
                imageView.setScaleY(scale);
            }
        });

        // 设置动画结束值
        spring.setEndValue(1f);
    }

    @Override
    protected void onStart() {
        super.onStart();

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.putLong("time", System.currentTimeMillis());
                PermissionUtils.getInstance().checkCameraPermission(mContext, new IPermissionCallback() {
                    @Override
                    public void onGranted() {
                        startScan();
                        KLog.e("onGranted");
                        ToastUtil.show(">>>>>> onGranted");
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
