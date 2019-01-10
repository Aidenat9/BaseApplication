package com.github.tianmu19.baselibrary.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.github.tianmu19.baselibrary.R;


/**
 * @author sunwei
 *         邮箱：tianmu19@gmail.com
 *         时间：2019/1/9 21:32
 *         包名：com.example.stvfapp.dialog
 *         <p>description:   系统对话框         </p>
 */

public class DialogCreateUtil {
    private static final DialogCreateUtil ourInstance = new DialogCreateUtil();

    public static DialogCreateUtil getInstance() {
        return ourInstance;
    }

    private DialogCreateUtil() {
    }

    /**
     * 确认
     * @param context
     * @param title
     * @param msg
     * @param callback
     */
    public void createConfirm(Context context, String title, String msg, final ConfirmCallback callback){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(context.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(null!=callback)callback.confirm();
                        dialog.dismiss();
                    }
                }).setCancelable(true).show();
    }

    public interface ConfirmCallback{
        void confirm();
    }
    public interface DialogCallback extends DialogCreateUtil.ConfirmCallback{
        void cancel();
    }
}
