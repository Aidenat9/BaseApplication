package com.github.tianmu19.baselibrary.utils.imageloader;

import android.widget.ImageView;

import com.github.tianmu19.baselibrary.utils.permission.Top;

/**
 * @author sunwei
 *         邮箱：tianmu19@gmail.com
 *         <p>description:  先在application里初始化，然后活动里可使用          </p>
 */
public class TImage implements ImageLoader {

    private static ImageLoader imageLoader;
    private static TImage mImage;

    public static void init(ImageLoader loader) {
        imageLoader = loader;
    }

    public static TImage getInstance() {
        if (imageLoader == null) {
            throw new NullPointerException("Call YFrame.initXImageLoader(ImageLoader loader) within your Application onCreate() method Or extends YApplication");
        }
        if (mImage == null) {
            mImage = new TImage();
        }
        return mImage;
    }

    @Override
    public void load(ImageView imageView, Object imageUrl) {
        imageLoader.load(imageView, imageUrl);
    }

    @Override
    public void loadOverride(ImageView imageView, Object imageUrl, int oWidth, int oHeight) {
        imageLoader.loadOverride(imageView, imageUrl,oWidth,oHeight);
    }

    @Override
    public void load(ImageView imageView, Object imageUrl, int defaultImage) {
        imageLoader.load(imageView, imageUrl, defaultImage);
    }

    @Override
    public void load(ImageView imageView, Object imageUrl, int defaultImage, int errorImage) {
        imageLoader.load(imageView, imageUrl, defaultImage, errorImage);
    }

    @Override
    public void load(ImageView imageView, Object imageUrl, Object transformation) {
        imageLoader.load(imageView, imageUrl, transformation);
    }
}