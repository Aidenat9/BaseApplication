package com.github.tianmu19;

import android.app.Application;

import com.hjq.toast.ToastUtils;


/**
 * @author sunwei
 *         邮箱：tianmu19@gmail.com
 *         时间：2019/1/10 10:10
 *         包名：com.github.tianmu19.baselibrary
 *         <p>description:            </p>
 */

public class MyApplication extends Application {
    private static MyApplication mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        ToastUtils.init(this);

    }
    public static MyApplication getInstance() {
        return mInstance;
    }
}
