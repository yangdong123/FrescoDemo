package com.netease.frescodemo.fresco;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.netease.frescodemo.R;


@SuppressWarnings("deprecation")
public class CustomDraweeView extends SimpleDraweeView {

    // 默认的宽高比例
    public static final int NORMAL = 0;
    public static final int ROUND = 1;
    public static final int RECTANGLE = 2;
    public static final float DEF_RATIO = 2.0f;
    private float mCustomRatio = 0f;
    private int mCustomWidth = 0;
    private int mCustomHeight = 0;
    private Context mContext;
    private ControllerListener mControllerListener;     // 下载监听器
    private ResizeOptions mResizeOptions;               // 图片缩放，优先级非常高；缩放的尺寸并不是按照指定的尺寸，而是根据内部来计算出一个合适的值；
    private ScalingUtils.ScaleType mDraweeViewScaleType;// 图片缩放类型，默认为CENTER_CROP
    private Drawable mProgressBar;                      // 加载进度
    private Drawable mPlaceholderDrawable;              // 加载背景
    private Drawable mGifChartOverlay;                  // GIF的覆盖物图
    private Drawable mLongChartOverlay;                 // 长图的覆盖物图
    private Drawable mNumberChartOverlay;               // 数字的覆盖物图
    private boolean mAutoPlayAnimations = false;        // 是否自动播放GIF图-不自动播放
    private boolean isCutGif;                           // 是否裁剪GIF
    private double mHeightRatio;                        // 宽高比例
    private int mTargetImageSize = -1;                  // 指定的图片尺寸
    private int mIsReplacePngBg2TargetColor = 0;      // 是否处理PNG图片的透明背景为指定颜色
    private ImageRequest.ImageType mImageType;          // 图片类型-默认
    private ImageFormat mImageFormat = ImageFormat.JPEG;// 图片类型-默认JPEG
    private String uriPathTag;                          // 加载图片的TAG，用于不重复加载图片
    private boolean selectPicFlag = false;              //区分上传图片时，选中图片之后Tag会错乱。

    public CustomDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init(context);
    }

    public CustomDraweeView(Context context) {
        super(context);
        init(context);
    }

    public CustomDraweeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public CustomDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        init(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomDraweeView, defStyle, 0);
        Drawable tempDrawable = a.getDrawable(R.styleable.CustomDraweeView_default_icon_id);
        float tempRatio = a.getFloat(R.styleable.CustomDraweeView_my_ratio, 0f);
        setMyRatio(tempRatio);
        if (tempDrawable != null) {
            setPlaceholderDrawable(tempDrawable);
            loadPicByResId(R.drawable.home_page_default_icon);
        }
        a.recycle();
    }

    public float getMyRatio() {
        return mCustomRatio;
    }

    public void setMyRatio(float ratio) {
        mCustomRatio = ratio;
    }

    public int getMyWidth() {
        return mCustomWidth;
    }

    public void setMyWidth(int customW) {
        this.mCustomWidth = customW;
    }

    public int getMyHeight() {
        return mCustomHeight;
    }

    public void setMyHeight(int customH) {
        this.mCustomHeight = customH;
    }

    public void init(Context context) {
        if (!isInEditMode()) {
            this.mContext = context;
            mPlaceholderDrawable = this.mContext.getResources().getDrawable(R.color.f7f7f7);
        }
        mImageType = ImageRequest.ImageType.DEFAULT;
        mControllerListener = new DefaultBaseControllerListener();
        mDraweeViewScaleType = ScalingUtils.ScaleType.CENTER_CROP;
    }

    //************************************************************************************************
    //***************************************普通图片加载方法*******************************************
    //************************************************************************************************

    /**
     * 加载uri图片
     */
    public void loadPicByUri(final Uri uri) {
        if (isInEditMode()) {
            return;
        }
        setControllerByType(uri, NORMAL);
    }

    /**
     * 加载url图片
     */
    public void loadPicByUrl(final String url) {
        loadPicByUri(Uri.parse(url));
    }

    /**
     * 加载本地资源图片
     */
    public void loadPicByResId(final int resId) {
        loadPicByUri(getUriByResId(resId));
    }

    //************************************************************************************************
    //***************************************圆形图片加载方法*********************************************
    //************************************************************************************************

    /**
     * 加载uri圆形图片
     */
    public void loadRoundPicByUri(final Uri uri) {
        setControllerByType(uri, ROUND);
    }

    /**
     * 加载url圆形图片
     */
    public void loadRoundPicByUrl(final String url) {
        if (TextUtils.isEmpty(url)) {
            loadRoundPicByResId(R.drawable.head_icon);
        } else {
            loadRoundPicByUri(Uri.parse(url));
        }
    }

    /**
     * 加载本地资源圆形图片
     */
    public void loadRoundPicByResId(final int resId) {
        loadRoundPicByUri(getUriByResId(resId));
    }

    //************************************************************************************************
    //***************************************圆角矩形图片加载方法*****************************************
    //************************************************************************************************

    /**
     * 加载uri矩形图片
     */
    public void loadRectangleByUri(final Uri uri) {
        setControllerByType(uri, RECTANGLE);
    }

    /**
     * 加载本地资源圆角矩形图片
     */
    public void loadRectangleByResId(final int resId) {
        loadRectangleByUri(getUriByResId(resId));
    }

    /**
     * 加载网络圆角矩形图片
     *
     * @param url 图片地址
     */
    public void loadRectangleByUrl(final String url) {
        loadRectangleByUri(Uri.parse(url));
    }

    /**
     * 是否设置GIF标识
     */
    public CustomDraweeView setGifChartIdentify(final boolean isShowGifIdentify) {
        if (isShowGifIdentify) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mGifChartOverlay = mContext.getDrawable(R.drawable.identify_gif);
            } else {
                mGifChartOverlay = mContext.getResources().getDrawable(R.drawable.identify_gif);
            }
        } else {
            mGifChartOverlay = null;
        }
        return this;
    }

    /**
     * 设置长图标识
     */
    public CustomDraweeView setLongChartIdentify(int imageWidth, int imageHeight) {
        if (imageHeight > imageWidth * DEF_RATIO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mLongChartOverlay = mContext.getDrawable(R.drawable.identify_long);
            } else {
                mLongChartOverlay = mContext.getResources().getDrawable(R.drawable.identify_long);
            }
        } else {
            mLongChartOverlay = null;
        }
        return this;
    }

    /**
     * 设置数字标识，内部会判断是否大于1
     */
    public CustomDraweeView setNumberChartIdentify(int number) {
        if (number > 1) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.identify_number);
            byte[] chunk = bitmap.getNinePatchChunk();
            if (NinePatch.isNinePatchChunk(chunk)) {
                mNumberChartOverlay = new DefaultNinePatchDrawable(getResources(), bitmap, chunk, NinePatchChunk.deserialize(chunk).mPaddings, null, number + "", getContext());
            } else {
                mNumberChartOverlay = null;
            }
        } else {
            mNumberChartOverlay = null;
        }
        return this;
    }

    /**
     * 根据图片宽高设置控件尺寸
     */
    public CustomDraweeView setWidthAndHeight(final float width, final float height) {
        float picRatio = height / width;
        if (picRatio >= DEF_RATIO) {
            picRatio = DEF_RATIO;
        }
        this.setHeightRatio(picRatio);
        return this;
    }

    public CustomDraweeView setWidthLayoutParams(final int maxWidth, final int width, final int height) {
        float imageAreaWidth = 472;
        float imageAreaHeight = 472;
        if (width >= height) {
            imageAreaHeight = (float) height / (float) width * imageAreaWidth;
        } else if (width < height) {
            imageAreaWidth = (float) width / (float) height * imageAreaHeight;
        }
        // 设置单张图片的布局
        ViewGroup.LayoutParams layoutparams = getLayoutParams();
        layoutparams.height = (int) imageAreaHeight;
        layoutparams.width = (int) imageAreaWidth;
        setLayoutParams(layoutparams);
        return this;
    }

    public CustomDraweeView setMaxWidthLayoutParams(final int maxWidth, final int width, final int height) {
        float widthRatio = (float) width / (float) maxWidth;
        float tmpheight = ((float) height / widthRatio);
        float ratio = tmpheight / maxWidth;
        if (ratio > DEF_RATIO) {
            ratio = DEF_RATIO;
        }
        tmpheight = maxWidth * ratio;
        // 设置单张图片的布局
        ViewGroup.LayoutParams layoutparams = getLayoutParams();
        layoutparams.height = (int) tmpheight;
        layoutparams.width = maxWidth;
        setLayoutParams(layoutparams);
        return this;
    }

    /**
     * 如果不为-1说明需要替换PNG的透明背景色
     */
    public int isReplacePNGBackground() {
        return mIsReplacePngBg2TargetColor;
    }

    /**
     * 是否需要替换PNG图片的透明背景为指定的颜色，需要则设置指定的颜色值
     */
    public CustomDraweeView replacePNGBackground(int targetColor) {
        if (targetColor == 0) {
            throw new RuntimeException("颜色值不能指定为-1");
        }
        this.mIsReplacePngBg2TargetColor = targetColor;
        return this;
    }


    public void setTargetImageSize(int targetImageSize) {
        this.mTargetImageSize = targetImageSize;
    }

    /**
     * 是否剪切GIF的第一帧
     */
    public void setIsCutGif(boolean isCutGif) {
        this.isCutGif = isCutGif;
    }

    private ImageRequest getImageRequest(CustomDraweeView view, Uri uri) {
        switch (mImageFormat) {
            case JPEG:
            case PNG:
                return ImageRequestBuilder.newBuilderWithSource(uri)
                        .setImageType(mImageType)
                        .setResizeOptions(mResizeOptions)
                        .setAutoRotateEnabled(true)
                        .setLocalThumbnailPreviewsEnabled(true)
                        .setProgressiveRenderingEnabled(false)
                        .build();
            case GIF:
//                if ( isCutGif()) {
//                    // 针对本地Gif预览时做特殊处理，裁剪出第一帧并显示
//                    File file = new File(uri.getPath());
//                    File cutFile = FileUtil.getCopyFile(file);
//                    Uri newUri = new Uri.Builder().scheme("file").path(cutFile.getAbsolutePath()).build();
//                    return ImageRequestBuilder.newBuilderWithSource(newUri)
//                            .setResizeOptions(mResizeOptions)
//                            .setAutoRotateEnabled(true)
//                            .build();
//                } else {
//                    return ImageRequestBuilder.newBuilderWithSource(uri).setAutoRotateEnabled(true).build();
//                }
        }
        throw new RuntimeException("must have a ImageRequest");
    }

    /**
     * 设置图片显示的形式
     *
     * @param type NORMAL,ROUND,RECTANGLE
     * @return DraweeViewPicRequestBuilder
     */
    private CustomDraweeView setLoadPicType(int type) {
        if (getContext() == null) {
            throw new IllegalArgumentException("Context is not null");
        }
        GenericDraweeHierarchyBuilder hierarchyBuilder = new GenericDraweeHierarchyBuilder(getContext().getResources()).setOverlay(getMyOverlay()).setActualImageScaleType(mDraweeViewScaleType).setProgressBarImage(mProgressBar, ScalingUtils.ScaleType.CENTER_INSIDE);
        switch (type) {
            case NORMAL:
                break;
            case ROUND:
                setPlaceholderDrawable(getContext().getResources().getDrawable(R.drawable.round_default_icon));
                hierarchyBuilder.setRoundingParams(RoundingParams.asCircle());
                break;
            case RECTANGLE:
                hierarchyBuilder.setRoundingParams(RoundingParams.fromCornersRadius(10.0f));
                break;
        }
        setHierarchy(hierarchyBuilder.setPlaceholderImage(mPlaceholderDrawable, ScalingUtils.ScaleType.CENTER_CROP).build());
        return this;
    }

    private CustomDraweeView setControllerByType(final Uri uri, int type) {
        uriPathTag = uri.toString();
        if (uriPathTag.toLowerCase().endsWith("gif")) {
            mImageFormat = ImageFormat.GIF;
        } else if (uriPathTag.toLowerCase().endsWith("jpeg") || uriPathTag.toLowerCase().endsWith("jpg")) {
            mImageFormat = ImageFormat.JPEG;
        } else if (uriPathTag.toLowerCase().endsWith("png")) {
            mImageFormat = ImageFormat.PNG;
        }
        if (noRepeatLoadImage(uriPathTag)) {
            setLoadPicType(type);
            AbstractDraweeController controller = Fresco.newDraweeControllerBuilder().setAutoPlayAnimations(mAutoPlayAnimations).setControllerListener(mControllerListener).setImageRequest(getImageRequest(this, uri)).setOldController(this.getController()).build();
            setController(controller);
        }
        return this;
    }

    public CustomDraweeView setDraweeViewBorder(float size, int color) {
        try {
            RoundingParams roundingParams = this.getHierarchy().getRoundingParams();
            roundingParams.setBorder(getResources().getColor(color), size);
        } catch (Exception ignored) {

        }
        return this;
    }


    /**
     * 获取遮盖物
     * 数字 > GIF > 长图
     */
    private Drawable getMyOverlay() {
        if (null != mNumberChartOverlay) {
            return mNumberChartOverlay;
        } else if (null != mGifChartOverlay) {
            return mGifChartOverlay;
        } else if (null != mLongChartOverlay) {
            return mLongChartOverlay;
        }
        return new ColorDrawable(Color.TRANSPARENT);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////私有方法///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    public CustomDraweeView setControllerListener(ControllerListener mControllerListener) {
        this.mControllerListener = mControllerListener;
        return this;
    }


    public CustomDraweeView setDraweeViewScaleType(ScalingUtils.ScaleType mDraweeViewScaleType) {
        this.mDraweeViewScaleType = mDraweeViewScaleType;
        return this;
    }


    public CustomDraweeView setPlaceholderDrawable(Drawable placeholderDrawable) {
        this.mPlaceholderDrawable = placeholderDrawable;
        return this;
    }

    public CustomDraweeView setResizeOptions(ResizeOptions resizeOptions) {
        this.mResizeOptions = resizeOptions;
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mHeightRatio > 0.0) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (width * mHeightRatio);
            super.setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 是否自动播放GIF，默认False
     */
    public CustomDraweeView setAutoPlayAnimations(boolean mAutoPlayAnimations) {
        this.mAutoPlayAnimations = mAutoPlayAnimations;
        return this;
    }

    public CustomDraweeView setProgressBar(Drawable mProgressBar) {
        this.mProgressBar = mProgressBar;
        return this;
    }

    /**
     * 判定Tag和Url是否相等，相等代表图片已经加载过，不需要从新加载
     */
    private boolean noRepeatLoadImage(String imgUrl) {
        return !(TextUtils.isEmpty(imgUrl) || TextUtils.isEmpty(this.getTag(R.id.uriPath) + "")) && !(this.getTag(R.id.uriPath) + "").equals(imgUrl);
    }

    public Uri getUriByResId(int resId) {
        // 增加对资源id类型的图片类型判断
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, opts);
        if (opts.outMimeType != null && opts.outMimeType.equals("image/png")) {
            mImageFormat = ImageFormat.PNG;
        }
        return new Uri.Builder().scheme("res").path(String.valueOf(resId)).build();
    }

    private boolean isCutGif() {
        return isCutGif;
    }

    public void setHeightRatio(double ratio) {
        if (ratio != mHeightRatio) {
            mHeightRatio = ratio;
            requestLayout();
        }
    }

    public ImageFormat getmImageFormat() {
        return mImageFormat;
    }

    public int getmTargetImageSize() {
        return mTargetImageSize;
    }

    public boolean isSelectPicFlag() {
        return selectPicFlag;
    }

    public void setSelectPicFlag(boolean selectPicFlag) {
        this.selectPicFlag = selectPicFlag;
    }

    /**
     * 图片加载完成时，为控件设置Tag
     */
    private class DefaultBaseControllerListener extends BaseControllerListener<ImageInfo> {
        @Override
        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            super.onFinalImageSet(id, imageInfo, animatable);
            CustomDraweeView.this.setTag(R.id.uriPath, uriPathTag);
        }
    }
}
