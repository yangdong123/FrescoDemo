package com.netease.frescodemo.fresco;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;

import java.io.File;

public class FrescoUtils {

    /**
     * 是否有磁盘缓存
     */
    public static boolean isThereDiskCache(String uri) {
        final CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(uri));
        final BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
        if (resource != null) {
            File file = ((FileBinaryResource) resource).getFile();
            if (file.exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从本地获取File,此url必须是原始的url未经过滤的
     */
    public static Bitmap getBitmapByUrlFromFile(String url, int width, int height) {
        Bitmap mBitmap = null;
        ImageRequest imageRequest = ImageRequest.fromUri(url);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest);
        ImagePipelineFactory instance = ImagePipelineFactory.getInstance();
        BinaryResource resource = instance.getMainDiskStorageCache().getResource(cacheKey);

        if (resource != null) {
            File file = ((FileBinaryResource) resource).getFile();
            if (file.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                mBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                mBitmap = zoomImage(mBitmap,width,height);
            }
        }
        return mBitmap;
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
}
