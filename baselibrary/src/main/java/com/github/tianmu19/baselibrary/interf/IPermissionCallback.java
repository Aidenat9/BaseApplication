package com.github.tianmu19.baselibrary.interf;

/**
 * @author sunwei
 *         邮箱：tianmu19@gmail.com
 *         时间：2019/1/10 10:34
 *         包名：com.github.tianmu19.baselibrary.interf
 *         <p>description:    权限callback        </p>
 */

public interface IPermissionCallback {
    void onGranted();
    void onDenied();
}
