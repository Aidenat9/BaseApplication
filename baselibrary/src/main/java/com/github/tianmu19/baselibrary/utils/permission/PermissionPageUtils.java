package com.github.tianmu19.baselibrary.utils.permission;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * @author sunwei
 * 邮箱：tianmu19@gmail.com
 * <p>description:    权限请求页适配，不同手机系统跳转到不同的权限请求页        </p>
 */
public class PermissionPageUtils {
    private final String TAG = "PermissionPageManager";

    private PermissionPageUtils() {
    }

    public static PermissionPageUtils getDefaut() {
        return Holder.instance;
    }
    private static class Holder{
        static PermissionPageUtils instance
                = new PermissionPageUtils();
    }

    /**
     * 调转到通知设置页
     */
    public void gotoNotifictionSetting(Context mContext) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, mContext.getPackageName());
            mContext.startActivity(intent);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", mContext.getPackageName());
            intent.putExtra("app_uid", mContext.getApplicationInfo().uid);
            mContext.startActivity(intent);
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
            mContext.startActivity(intent);
        } else {
            //setting page.
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", mContext.getPackageName(), null));
        }
    }

}