package com.github.tianmu19.baselibrary.utils.permission;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.github.tianmu19.baselibrary.R;
import com.github.tianmu19.baselibrary.interf.IPermissionCallback;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sunwei
 *         邮箱：tianmu19@gmail.com
 *         时间：2019/1/10 10:35
 *         包名：com.github.tianmu19.baselibrary.utils.permission
 *         <p>description:    权限判断类        </p>
 */

public class PermissionUtils {
    private static final PermissionUtils ourInstance = new PermissionUtils();

    public static PermissionUtils getInstance() {
        return ourInstance;
    }

    private PermissionUtils() {
    }

    /**
     * 存储
     */
    public void checkStoragePermission(Context context, @NonNull final IPermissionCallback callback) {
        checkXPermission(context, callback, Permission.Group.STORAGE);
    }

    /**
     * 相机
     */
    public void checkCameraPermission(Context context, @NonNull final IPermissionCallback callback) {
        checkXPermission(context, callback, Permission.CAMERA);
    }

    /**
     * location
     */
    public void checkLocationPermission(Context context, @NonNull final IPermissionCallback callback) {
        checkXPermission(context, callback, Permission.Group.LOCATION);
    }

    /**
     * 电话
     */
    public void checkCallPermission(Context context, @NonNull  final IPermissionCallback callback) {
        checkXPermission(context, callback, Permission.CALL_PHONE);
    }

    /**
     * 手机imei
     */
    public void checkPhoneStatePermission(Context context, @NonNull final IPermissionCallback callback) {
        checkXPermission(context, callback, Permission.READ_PHONE_STATE);
    }

    /**
     * 安装apk
     * Installing unknown source apk on Android 8.0 or higher.
     * android.permission.REQUEST_INSTALL_PACKAGES.
     */
    public void checkInstallPermission(Context context, File apkFile, @NonNull final IPermissionCallback callback) {
        if (null == context) return;
        AndPermission.with(context).install()
                .file(apkFile).onGranted(new Action<File>() {
            @Override
            public void onAction(File data) {
                callback.onGranted();
            }
        }).onDenied(new Action<File>() {
            @Override
            public void onAction(File data) {
                callback.onDenied();
            }
        }).start();

    }

    /**
     * Drawing at the top of other apps on Android 6.0 or higher.
     * android.permission.SYSTEM_ALERT_WINDOW.
     */
    public void checkOverLayPermission(Context context, @NonNull final IPermissionCallback callback) {
        AndPermission.with(context).overlay().onGranted(new Action<Void>() {
            @Override
            public void onAction(Void data) {
                callback.onGranted();
            }
        }).onDenied(new Action<Void>() {
            @Override
            public void onAction(Void data) {
                callback.onDenied();
            }
        }).start();
    }


    /**
     * 单个权限
     */
    private void checkXPermission(final Context context, final IPermissionCallback callback, String... groups) {
        if (null == context) return;
        int versionCodes = Build.VERSION.SDK_INT;//取得SDK版本
        if (versionCodes >= 23) {
            AndPermission.with(context).runtime()
                    .permission(groups)
                    .rationale(new Rationale<List<String>>() {
                        @Override
                        public void showRationale(Context context, List<String> data, final RequestExecutor executor) {
                            //tip
                            List<String> permissionNames = Permission.transformText(context, data);
                            String permissionText = TextUtils.join(",", permissionNames);
                            String please_open_permission = context.getResources().getString(R.string.you_haved_denied_permission_recently);
                            String targetTips = String.format(please_open_permission, permissionText);
                            SpannableString spannableString = formatTips(context, targetTips);
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(context.getResources().getString(R.string.title_tip)).setMessage(spannableString)
                                    .setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            executor.execute();
                                        }
                                    })
                                    .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            executor.cancel();
                                        }
                                    }).setCancelable(false).show();
                        }
                    })
                    .onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            if (null != callback) {
                                callback.onGranted();
                            }
                        }
                    }).onDenied(new Action<List<String>>() {
                @Override
                public void onAction(List<String> deniedPermissions) {
                    if (AndPermission.hasAlwaysDeniedPermission(context, deniedPermissions)) {
                        //tip
                        List<String> permissionNames = Permission.transformText(context, deniedPermissions);
                        String permissionText = TextUtils.join(",", permissionNames);
                        String please_open_permission = context.getResources().getString(R.string.please_open_these_permissions);
                        String targetTips = String.format(please_open_permission, permissionText);
                        SpannableString spannableString = formatTips(context, targetTips);
                        // 这些权限被用户总是拒绝。
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getResources().getString(R.string.title_tip)).setMessage(spannableString)
                                .setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PermissionPageUtils pageUtils = new PermissionPageUtils(context);
                                        pageUtils.jumpPermissionPage();
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setCancelable(false).show();
                    }
                    if (null != callback) {
                        callback.onDenied();
                    }
                }
            }).start();
        }else{
            if (null != callback) {
                callback.onGranted();
            }
        }
    }

    private SpannableString formatTips(Context context, String targetTips) {
        SpannableString spannableString = new SpannableString(targetTips);
        spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.color_permissions))
                , targetTips.indexOf("["), targetTips.indexOf("]") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD)
                , targetTips.indexOf("["), targetTips.indexOf("]") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 权限组
     */
    private void checkXPermission(Context context, final IPermissionCallback callback, String[]... groups) {
        if (null == context) return;
        List<String> permissionList = new ArrayList<>();
        for (String[] group : groups) {
            permissionList.addAll(Arrays.asList(group));
        }
        String[] permissions = permissionList.toArray(new String[permissionList.size()]);
        checkXPermission(context, callback, permissions);
    }

    /**
     * 用户往往会拒绝一些权限，而程序的继续运行又必须使用这些权限，此时我们应该友善的提示用户。
     * 例如，当用户拒绝Permission.WRITE_EXTERNAL_STORAGE一次，在下次请求Permission.WRITE_EXTERNAL_STORAGE时，
     * 我们应该展示为什么需要此权限的说明，以便用户判断是否需要授权给我们。在AndPermission中我们可以使用Rationale。
     */

}
