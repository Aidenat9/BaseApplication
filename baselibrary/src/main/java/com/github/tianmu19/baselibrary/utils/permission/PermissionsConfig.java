package com.github.tianmu19.baselibrary.utils.permission;

import android.Manifest;
import android.os.Build;

/**
 * author: tmgg
 * created on: 2017/7/11 11:57
 * description: 6.0需要运行时权限
 */
public class PermissionsConfig {
    /**
     身体传感器
     日历
     摄像头
     通讯录
     地理位置
     麦克风
     电话
     短信
     存储空间
     */
    //存储
//    public static final String  WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
//    public static final String  READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
//    //日历
//    public static final String  READ_CALENDAR = Manifest.permission.READ_CALENDAR;
//    public static final String  WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR;
//    //摄像头
//    public static final String  CAMERA = Manifest.permission.CAMERA;
//    //联系人
//    public static final String  READ_CONTACTS = Manifest.permission.READ_CONTACTS;
//    public static final String  WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS;
//    public static final String  GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
//    //位置
//    public static final String  ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
//    public static final String  ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
//    //麦克风
//    public static final String  RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
//    //电话
    public static final String  READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
//    public static final String  CALL_PHONE = Manifest.permission.CALL_PHONE;
//    public static final String  READ_CALL_LOG = Manifest.permission.READ_CALL_LOG;
//    public static final String  WRITE_CALL_LOG = Manifest.permission.WRITE_CALL_LOG;
//    public static final String  ADD_VOICEMAIL = Manifest.permission.ADD_VOICEMAIL;
//    public static final String  USE_SIP = Manifest.permission.USE_SIP;
//    public static final String  PROCESS_OUTGOING_CALLS = Manifest.permission.PROCESS_OUTGOING_CALLS;
//    //传感器
//    public static final String  BODY_SENSORS = Manifest.permission.BODY_SENSORS;
//    //短信
//    public static final String  SEND_SMS = Manifest.permission.SEND_SMS;
//    public static final String  RECEIVE_SMS = Manifest.permission.RECEIVE_SMS;
//    public static final String  READ_SMS = Manifest.permission.READ_SMS;
//    public static final String  RECEIVE_WAP_PUSH = Manifest.permission.RECEIVE_WAP_PUSH;
//    public static final String  RECEIVE_MMS = Manifest.permission.RECEIVE_MMS;

    /**
     * 权限拒绝提示语
     */
    public static final String TIPS_SCANCODE = "拒绝拍照和使用存储权限，导致扫码不可使用！";
    public static final String TIPS_PHOTO = "拒绝拍照和使用存储权限，导致选择拍摄照片不可使用！";
    public static final String TIPS_STORAGE = "拒绝存储权限，导致app不可使用！";
    public static final String TIPS_CONTACT = "拒绝获取联系人权限，导致获取通讯录不可使用！";
    public static final String TIPS_CALLPHONE = "拒绝电话权限，导致拨打电话不可使用！";

    /**
     * 因此我们总结出一个更优的方案，归根结底就是申请权限时要申请权限组，而不是单一的某个权限。所以我们按照系统权限组
     * 分类，把一个组的常量放到一个数组中，并根据系统版本为这个数组赋值，于是乎产生了这样一个类：
     * 在Android M以前使用某权限是不需要用户授权的，只要在Manifest中注册即可，在Android M之后需要注册并申请用户
     * 授权，所以我们根据系统版本在Android M以前用一个空数组作为权限组，在Android M以后用真实数组权限。
     */
    // TODO: 2017/11/20 适配8.0的分离权限组 申请权限（申请组而不是单独的，防止组中权限没同意造成的崩溃）
    public static final String[] CALENDAR;   // 读写日历。
    public static final String[] CAMERA;     // 相机。
    public static final String[] CONTACTS;   // 读写联系人。
    public static final String[] LOCATION;   // 读位置信息。
    public static final String[] MICROPHONE; // 使用麦克风。
    public static final String[] PHONE;      // 读电话状态、打电话、读写电话记录。
    public static final String[] SENSORS;    // 传感器。
    public static final String[] SMS;        // 读写短信、收发短信。
    public static final String[] STORAGE;    // 读写存储卡。

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            CALENDAR = new String[]{};
            CAMERA = new String[]{};
            CONTACTS = new String[]{};
            LOCATION = new String[]{};
            MICROPHONE = new String[]{};
            PHONE = new String[]{};
            SENSORS = new String[]{};
            SMS = new String[]{};
            STORAGE = new String[]{};
        } else {
            CALENDAR = new String[]{
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR};

            CAMERA = new String[]{
                    Manifest.permission.CAMERA};

            CONTACTS = new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
//                    Manifest.permission.GET_ACCOUNTS
            };

            LOCATION = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};

            MICROPHONE = new String[]{
                    Manifest.permission.RECORD_AUDIO};

            PHONE = new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
//                    Manifest.permission.READ_CALL_LOG,
//                    Manifest.permission.WRITE_CALL_LOG,
//                    Manifest.permission.USE_SIP,
//                    Manifest.permission.PROCESS_OUTGOING_CALLS
            };

            SENSORS = new String[]{
                    Manifest.permission.BODY_SENSORS};

            SMS = new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_WAP_PUSH,
                    Manifest.permission.RECEIVE_MMS};

            STORAGE = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
    }

}
