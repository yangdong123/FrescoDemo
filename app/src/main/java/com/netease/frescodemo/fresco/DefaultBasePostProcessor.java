package com.netease.frescodemo.fresco;

import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;

import com.facebook.common.references.CloseableReference;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.request.BasePostprocessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.netease.frescodemo.fresco.FrescoUtils.zoomImage;

public class DefaultBasePostProcessor extends BasePostprocessor {

    Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    CustomDraweeView draweeView;

    public DefaultBasePostProcessor(CustomDraweeView draweeView) {
        this.draweeView = draweeView;
    }

    /**
     * 将bitmap转化为数组
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap, int quality) {
        if (bitmap == null) {
            throw new IllegalArgumentException("bitmap is not null");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

    /**
     * 截取bitmap指定的宽高
     */
    public static Bitmap decodeRegion(Bitmap bitmap, int width, int height) {
        return decodeRegion(bitmap2Bytes(bitmap, 100), width, height);
    }


    public static Bitmap decodeRegionBigWidthSpecial(Bitmap bitmap, int width, int height) {
        return decodeRegion(bitmap, width, height);
    }

    public static Bitmap decodeRegion(byte[] bytes, int width, int height) {
        BitmapRegionDecoder bitmapRegionDecoder = null;
        try {
            bitmapRegionDecoder = BitmapRegionDecoder.newInstance(bytes, 0, bytes.length, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Rect rect = new Rect(0, 0, width, height);
        assert bitmapRegionDecoder != null;
        return bitmapRegionDecoder.decodeRegion(rect, null);
    }

    @Override
    public CloseableReference<Bitmap> process(Bitmap sourceBitmap, PlatformBitmapFactory bitmapFactory) {

        // 默认宽高比例显示 W:H = 1:1
        // 按照宽高比例截取图片区域
        if (sourceBitmap.getHeight() > (int) (sourceBitmap.getWidth() * CustomDraweeView.DEF_RATIO)) {
            Bitmap bitmap = decodeRegion(sourceBitmap, sourceBitmap.getWidth(), (int) (sourceBitmap.getWidth() * CustomDraweeView.DEF_RATIO));
            return super.process(bitmap, bitmapFactory);
        } else if (draweeView.getMyRatio() == 1) {
            int sourceHeight;
            if (sourceBitmap.getWidth() <= sourceBitmap.getHeight())
                sourceHeight = sourceBitmap.getWidth();
            else sourceHeight = sourceBitmap.getHeight();
            Bitmap bitmap = decodeRegionBigWidthSpecial(sourceBitmap, sourceBitmap.getWidth(), sourceHeight);
            return super.process(bitmap, bitmapFactory);
        } else if(draweeView.getMyRatio() == 0.5f) {
            Bitmap bitmap = decodeRegion(sourceBitmap, sourceBitmap.getWidth(), (int)(sourceBitmap.getWidth() * 400.0 / 750.0));
            return super.process(bitmap, bitmapFactory);
        }

        // 将PNG图片转换成JPG，并将背景色设置为指定颜色
        else if (draweeView.getMyRatio() == 1 || draweeView.getMyRatio() == 0.5) {
            return super.process(sourceBitmap, bitmapFactory);
        }

        if (ImageFormat.PNG.equals(draweeView.getmImageFormat()) && draweeView.isReplacePNGBackground() != 0) {
            replaceTransparent2TargetColor(sourceBitmap, draweeView.isReplacePNGBackground());
        }

        // PNG图片，并且设置了图片最大宽高，如果加载的PNG图片宽高超过指定宽高，并截取指定大小
        else if (ImageFormat.PNG.equals(draweeView.getmImageFormat()) && draweeView.getmTargetImageSize() != -1 && (sourceBitmap.getWidth() > draweeView.getmTargetImageSize() || sourceBitmap.getHeight() > draweeView.getmTargetImageSize())) {

            // 压缩图片
//            Bitmap bitmap = PhotoUtil.decodeSampledBitmapFromByteArray(bitmap2Bytes(sourceBitmap, 100), draweeView.getmTargetImageSize(), draweeView.getmTargetImageSize());
            Bitmap bitmap = zoomImage(sourceBitmap, draweeView.getmTargetImageSize(), draweeView.getmTargetImageSize());

            // 截取图片
            Bitmap region = decodeRegion(bitmap, draweeView.getmTargetImageSize(), draweeView.getmTargetImageSize());
            bitmap.recycle();

            return super.process(region, bitmapFactory);
        }
        return super.process(sourceBitmap, bitmapFactory);
    }

    private void replaceTransparent2TargetColor(Bitmap sourceBitmap, int color) {
        Canvas canvas = new Canvas(sourceBitmap);
        canvas.drawColor(color, PorterDuff.Mode.DST_OVER);
        canvas.drawBitmap(sourceBitmap, 0, 0, mPaint);
    }



}
