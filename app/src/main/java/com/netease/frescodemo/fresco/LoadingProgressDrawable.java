package com.netease.frescodemo.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.netease.frescodemo.FrescoApplication;
import com.netease.frescodemo.R;


public class LoadingProgressDrawable extends Drawable {

    private static int[] Loadings = {R.drawable.load_progress_1, R.drawable.load_progress_3, R.drawable.load_progress_4, R.drawable.load_progress_6, R.drawable.load_progress_7, R.drawable.load_progress_8, R.drawable.load_progress_9, R.drawable.load_progress_10, R.drawable.load_progress_11, R.drawable.load_progress_12};
    BitmapFactory.Options options = new BitmapFactory.Options();
    Context context;
    private Paint mPaint;
    private int mLevel;

    public LoadingProgressDrawable(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        this.context = context;
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), Loadings[getIndex()], options);
        int left = getBounds().right / 2 - options.outWidth / 2;
        int top = getBounds().bottom / 2 - options.outHeight / 2;
        canvas.drawColor(FrescoApplication.getInstance().getResources().getColor(R.color.cffffff));
        canvas.drawBitmap(bitmap, left, top, mPaint);
    }

    private int getIndex() {
        int index = mLevel / 1000;
        if (index < 0) {
            index = 0;
        } else if (index >= Loadings.length) {
            index = Loadings.length - 1;
        }
        return index;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return 0;
    }


    @Override
    protected boolean onLevelChange(int level) {
        this.mLevel = level;
        this.invalidateSelf();
        return true;
    }
}
