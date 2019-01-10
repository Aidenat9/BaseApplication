package com.github.tianmu19.utils;

import android.view.Gravity;

import com.github.tianmu19.MyApplication;
import com.github.tianmu19.baselibrary.utils.DpUtils;
import com.hjq.toast.IToastStyle;
import com.hjq.toast.ToastUtils;


/**
 * TODO: 2018/11/2 解决重复toast问题
 */
public class ToastUtil {
    static{
        ToastUtils.initStyle(new DefaultToastStyle());
    }

    public static void show(String msg){
        ToastUtils.show(msg);
    }

    private static class DefaultToastStyle implements IToastStyle {

        @Override
        public int getGravity() {
            return Gravity.BOTTOM;
        }

        @Override
        public int getXOffset() {
            return 0;
        }

        @Override
        public int getYOffset() {
            int offSetY = (int) DpUtils.dp2px(MyApplication.getInstance().getResources(), 64);
            if(offSetY<100)offSetY = 128;
            return offSetY;
        }

        @Override
        public int getZ() {
            return 3;
        }

        @Override
        public int getCornerRadius() {
            return 2;
        }

        @Override
        public int getBackgroundColor() {
            return 0Xe0333333;
        }

        @Override
        public int getTextColor() {
            return 0XFFFFFFFF;
        }

        @Override
        public float getTextSize() {
            return 15;
        }

        @Override
        public int getMaxLines() {
            return 2;
        }

        @Override
        public int getPaddingLeft() {
            return 15;
        }

        @Override
        public int getPaddingTop() {
            return 7;
        }

        @Override
        public int getPaddingRight() {
            return 15;
        }

        @Override
        public int getPaddingBottom() {
            return 7;
        }
    }
}