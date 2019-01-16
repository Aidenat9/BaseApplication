package com.github.tianmu19;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.tianmu19.baselibrary.utils.imageloader.ImageLoader;
import com.github.tianmu19.baselibrary.utils.imageloader.TImage;
import com.github.tianmu19.baselibrary.utils.klogutil.KLog;
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
        KLog.init(BuildConfig.LOG_DEBUG);
        initImageLoader();

    }

    @SuppressLint("CheckResult")
    private void initImageLoader() {
        final RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .encodeQuality(95)
                .centerCrop().override(400, 400);
        TImage.init(new ImageLoader() {
            @SuppressLint("CheckResult")
            @Override
            public void load(ImageView imageView, Object imageUrl) {
                //默认的加载图和错误图
                options.placeholder(R.mipmap.ic_placeholder).error(R.mipmap.ic_error_placeholder);
                loadImage(imageView, imageUrl);
            }

            @Override
            public void loadOverride(ImageView imageView, Object imageUrl, int width, int height) {
                options.override(width, height);
                loadImage(imageView, imageUrl);
            }

            @Override
            public void load(ImageView imageView, Object imageUrl, int defaultImage) {
                options.placeholder(defaultImage);
                loadImage(imageView, imageUrl);
            }

            private void loadImage(ImageView imageView, Object imageUrl) {
                Glide.with(getApplicationContext()).load(imageUrl)
                        .thumbnail(0.2f).apply(options).into(imageView);
            }

            @Override
            public void load(ImageView imageView, Object imageUrl, int defaultImage, int errorImage) {
                options.placeholder(defaultImage).error(errorImage);
                loadImage(imageView, imageUrl);
            }

            @Override
            public void load(ImageView imageView, Object imageUrl, Object transformation) {
                options.transform((Transformation<Bitmap>) transformation);
                loadImage(imageView, imageUrl);
            }
        });
    }

    public static MyApplication getInstance() {
        return mInstance;
    }


}
