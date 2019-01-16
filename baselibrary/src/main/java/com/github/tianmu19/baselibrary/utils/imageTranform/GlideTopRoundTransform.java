package com.github.tianmu19.baselibrary.utils.imageTranform;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

/**
 * Created by Administrator on 2017/9/20.
 */

//制作图片只有头部圆角
public class GlideTopRoundTransform extends BitmapTransformation {
    private static float radius = 0f;

    public GlideTopRoundTransform(Context context) {
        this(context, 4);
    }

    public GlideTopRoundTransform(Context context, int dp) {
        super();
        radius = Resources.getSystem().getDisplayMetrics().density * dp;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
        return roundCrop(pool, bitmap);
    }

    private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        //设置四个角都是圆角
        canvas.drawRoundRect(rectF, radius, radius, paint);
        // 禁止左上为圆角，改为直角
//        canvas.drawRect(0, 0, radius, radius, paint);
        // 禁止右上为圆角，改为直角
//        canvas.drawRect(rectF.right - radius, 0, rectF.right, radius, paint);
        // 禁止左下为圆角，改为直角
        canvas.drawRect(0, rectF.bottom - radius, radius, rectF.bottom, paint);
        // 禁止右下为圆角，改为直角
        canvas.drawRect(rectF.right - radius, rectF.bottom - radius, rectF.right, rectF.bottom, paint);

        return result;
    }

    public String getId() {
        return getClass().getName() + Math.round(radius);
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {

    }
}