package com.netease.frescodemo.fresco;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextUtils;

import com.netease.frescodemo.R;


public class DefaultNinePatchDrawable extends NinePatchDrawable {
    private String mStr;
    private Paint mPaint;

    public DefaultNinePatchDrawable(Resources res, Bitmap bitmap, byte[] chunk, Rect padding, String srcName, String str, Context context) {
        super(res, bitmap, chunk, padding, srcName);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(context.getResources().getDimension(R.dimen.text_size_16));
        mStr = str;
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (TextUtils.isEmpty(mStr)) {
            return;
        }
        float textHeight = mPaint.descent() - mPaint.ascent();
        float textWidth = mPaint.measureText(mStr);

        float offsetX = textWidth / 2;
        float offsetY = textHeight / 2;

        float disY = getIntrinsicHeight() / 2 - offsetY;
        float disX = getIntrinsicWidth() / 2 - offsetX;

        canvas.drawText(mStr, this.getBounds().right - getIntrinsicWidth() + disX, this.getBounds().bottom - getIntrinsicHeight() + textHeight, mPaint);
    }
}
