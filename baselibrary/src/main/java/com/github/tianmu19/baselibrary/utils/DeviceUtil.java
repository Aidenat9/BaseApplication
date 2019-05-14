package com.github.tianmu19.baselibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.github.tianmu19.baselibrary.BuildConfig;

import java.io.File;

/**
 * @author sunwei
 * email：tianmu19@gmail.com
 * date：2019/4/11 14:41
 * package：com.example.siffapp.util
 * version：1.0
 * <p>description：              </p>
 */
public class DeviceUtil {
    public static int packageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }
    // 安装下载后的apk文件
    public static void Instanll(File file, Context context) {

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri updateUri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
        } else {
            updateUri = Uri.fromFile(file);
        }
        intent.setDataAndType(updateUri,
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
