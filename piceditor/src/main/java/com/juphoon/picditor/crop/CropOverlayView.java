package com.juphoon.picditor.crop;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.juphoon.picditor.crop.gesture.CropBaseHandler;
import com.juphoon.picditor.crop.gesture.impl.AspectRatioCropHandler;
import com.juphoon.picditor.crop.gesture.impl.MoveHandler;
import com.juphoon.picditor.crop.gesture.impl.NoAspectRatioCropHandler;
import com.juphoon.picditor.crop.gesture.impl.TwoFingerHandler;
import com.juphoon.picditor.utils.DrawUtils;
import com.juphoon.picditor.utils.ViewUtils;

import java.util.Locale;

public class CropOverlayView extends View {
    public static final int NO_ASPECT_RATIO = -1;

    private Paint mBgPaint; // 透明黑色背景
    private Paint mTriggerPaint;// 触发器绘制
    private Paint mIndicatorLinePaint; // 边框，指示器网格
    private Paint mTextPaint; // 文字

    private RectF mCenterRectF; // 中间截取的矩形
    private RectF mTopRectF; // 截取区域上方矩形
    private RectF mLeftRectF; // 截取区域左边矩形
    private RectF mBottomRectF; // 截取区域底部矩形
    private RectF mRightRectF; // 截取区域右边矩形

    private int mWidth, mHeight;// 控件的宽高
    private static final int TRIGGER_LENGTH = 100; // 触发器的长度
    private static final int TRIGGER_STROKE_WIDTH = 10; // 触发器器的条纹宽度
    private static final int INDICATOR_LINE_STROKE_WIDTH = 3; // 指示器的条纹宽度

    private static final int MIN_WIDTH = 2 * TRIGGER_LENGTH, MIN_HEIGHT = 2 * TRIGGER_LENGTH; // 最小的宽高，保证触发器不会重叠的初始值
    private static final int TOUCH_ENLARGE_CHECK_REGION = TRIGGER_STROKE_WIDTH * 5; // 触摸时检测方法检测范围，避免检测不到
    private boolean mIsInTouching; // 是否在触摸过程中

    private float mAspectRatio = NO_ASPECT_RATIO; // 确定的比例 ,w/h

    private SparseArray<CropBaseHandler> mHandlerMap; // 触摸处理类的管理，避免多次初始化
    private CropBaseHandler mTouchRegionHandler; // 触摸处理

    public CropOverlayView(Context context) {
        super(context);
    }

    public CropOverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mWidth == 0 || mHeight == 0) {
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();

            setMeasuredDimension(mWidth, mHeight);
            mCenterRectF = new RectF(0, 0, mWidth, mHeight);

            mTopRectF = new RectF();
            mLeftRectF = new RectF();
            mRightRectF = new RectF();
            mBottomRectF = new RectF();

            updateBackgroundRectF();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mCenterRectF == null)
            return;

        // draw bg
        canvas.drawRect(mTopRectF, mBgPaint);
        canvas.drawRect(mLeftRectF, mBgPaint);
        canvas.drawRect(mRightRectF, mBgPaint);
        canvas.drawRect(mBottomRectF, mBgPaint);

        // 四角触发器
        float verticalBottomCornerTriggerFromY = mCenterRectF.top + mCenterRectF.height() - TRIGGER_LENGTH / 2; // 垂直的 底部 角触发器开始的Y
        float horizontalRightCornerTriggerFromX = mCenterRectF.left + mCenterRectF.width() - TRIGGER_LENGTH / 2; // 水平的 右边的 角触发器开始的X

        DrawUtils.drawHLine(canvas, mTriggerPaint, mCenterRectF.top, mCenterRectF.left - TRIGGER_STROKE_WIDTH / 2, TRIGGER_LENGTH / 2); // top-left-h
        DrawUtils.drawVLine(canvas, mTriggerPaint, mCenterRectF.left, mCenterRectF.top - TRIGGER_STROKE_WIDTH / 2, TRIGGER_LENGTH / 2); // top-left-v

        DrawUtils.drawHLine(canvas, mTriggerPaint, mCenterRectF.bottom, mCenterRectF.left - TRIGGER_STROKE_WIDTH / 2, TRIGGER_LENGTH / 2); // bottom-left-h
        DrawUtils.drawVLine(canvas, mTriggerPaint, mCenterRectF.left, verticalBottomCornerTriggerFromY + TRIGGER_STROKE_WIDTH / 2, TRIGGER_LENGTH / 2); // bottom-left-v

        DrawUtils.drawHLine(canvas, mTriggerPaint, mCenterRectF.top, horizontalRightCornerTriggerFromX + TRIGGER_STROKE_WIDTH / 2, TRIGGER_LENGTH / 2); // top-right-h
        DrawUtils.drawVLine(canvas, mTriggerPaint, mCenterRectF.right, mCenterRectF.top - TRIGGER_STROKE_WIDTH / 2, TRIGGER_LENGTH / 2); // top-right-v

        DrawUtils.drawHLine(canvas, mTriggerPaint, mCenterRectF.bottom, horizontalRightCornerTriggerFromX + TRIGGER_STROKE_WIDTH / 2, TRIGGER_LENGTH / 2); // bottom-right-h
        DrawUtils.drawVLine(canvas, mTriggerPaint, mCenterRectF.right, verticalBottomCornerTriggerFromY + TRIGGER_STROKE_WIDTH / 2, TRIGGER_LENGTH / 2); // bottom-right-v

        // 边框
        canvas.drawRect(mCenterRectF, mIndicatorLinePaint);

        // 指示线
        for (int i = 1; i < 3; i++) {
            DrawUtils.drawHLine(canvas, mIndicatorLinePaint, mCenterRectF.top + mCenterRectF.height() / 3 * i, mCenterRectF.left, mCenterRectF.width());
        }
        for (int i = 1; i < 3; i++) {
            DrawUtils.drawVLine(canvas, mIndicatorLinePaint, mCenterRectF.left + mCenterRectF.width() / 3 * i, mCenterRectF.top, mCenterRectF.height());
        }
        // 文字
        String msg = String.format(Locale.CHINA, "%.0fx%.0f", mCenterRectF.width(), mCenterRectF.height());
        canvas.drawText(msg, mCenterRectF.left + mCenterRectF.width() / 2, mCenterRectF.top + mCenterRectF.height() / 2 + DrawUtils.measureTextHeight(mTextPaint) / 2, mTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        }
        int actionMasked = event.getActionMasked();

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mIsInTouching = true;
                mTouchRegionHandler = findTouchHandlerOnTouchDown(event);
                postInvalidate();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mIsInTouching = true;
                mTouchRegionHandler = findTouchHandlerOnTouchDown(event);
                postInvalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchRegionHandler == null) {
                    return false;
                }
                mTouchRegionHandler.onTouchMove(event);
                updateBackgroundRectF();
                postInvalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsInTouching = false;
        }
        return true;
    }

    public void attachImage(Bitmap bitmap, int maxWidth, int maxHeight, float scale, View... views) {
        reset();
        int width;
        int height;
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        if (bitmapWidth == bitmapHeight) {
            width = height = (int) (Math.min(maxWidth, maxHeight) * scale);
        } else if (bitmapWidth > bitmapHeight) {
            width = (int) (maxWidth * scale);
            height = (int) (width * (bitmapHeight * 1f / bitmapWidth));
        } else {
            height = (int) (maxHeight * scale);
            width = (int) (height * (bitmapWidth * 1f / bitmapHeight));
        }
        ViewUtils.setLayoutParam(width, height, views);
        ViewUtils.setLayoutParam(width, height, this);
    }

    public void reset() {
        mWidth = 0;
        mHeight = 0;
        mAspectRatio = NO_ASPECT_RATIO;
        postInvalidate();
    }

    private void init() {
        mBgPaint = DrawUtils.newPaint(Color.parseColor("#5f000000"), 0, Paint.Style.FILL_AND_STROKE);
        mTriggerPaint = DrawUtils.newPaint(Color.WHITE, TRIGGER_STROKE_WIDTH, Paint.Style.STROKE);
        mIndicatorLinePaint = DrawUtils.newPaint(Color.WHITE, INDICATOR_LINE_STROKE_WIDTH, Paint.Style.STROKE);
        mTextPaint = DrawUtils.newPaint(Color.WHITE, 2, Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(45);
    }

    private boolean hasNoAspectRatio() {
        return mAspectRatio == NO_ASPECT_RATIO;
    }

    // 根据确定的中间区域矩形更新其他矩形
    private void updateBackgroundRectF() {
        //judgeAspectRatioCompensation();
        mTopRectF.set(0, 0, mWidth, mCenterRectF.top);
        mLeftRectF.set(0, mCenterRectF.top, mCenterRectF.left, mCenterRectF.bottom);
        mRightRectF.set(mCenterRectF.right, mCenterRectF.top, mWidth, mCenterRectF.bottom);
        mBottomRectF.set(0, mCenterRectF.bottom, mWidth, mHeight);
    }

    private CropBaseHandler findTouchHandlerOnTouchDown(MotionEvent event) {
        float initX = event.getX();
        float initY = event.getY();
        CropBaseHandler handler = null;
        // 双指缩放
        if (event.getPointerCount() == 2) {
            handler = getTouchRegionHandler(CropBaseHandler.TWO_FINGER);
        } else if (hasNoAspectRatio() && createRectF(mCenterRectF.left - TOUCH_ENLARGE_CHECK_REGION / 2,
                mCenterRectF.top + TRIGGER_LENGTH / 2, TOUCH_ENLARGE_CHECK_REGION,
                mCenterRectF.height() - TRIGGER_LENGTH).contains(initX, initY)) {
            handler = getTouchRegionHandler(CropBaseHandler.LEFT);
        } else if (hasNoAspectRatio() && createRectF(mCenterRectF.left + TRIGGER_LENGTH / 2,
                mCenterRectF.top - TOUCH_ENLARGE_CHECK_REGION / 2,
                mCenterRectF.width() - TRIGGER_LENGTH,
                TOUCH_ENLARGE_CHECK_REGION).contains(initX, initY)) {
            handler = getTouchRegionHandler(CropBaseHandler.TOP);
        } else if (hasNoAspectRatio() && createRectF(mCenterRectF.right - TRIGGER_LENGTH / 2,
                mCenterRectF.top + TRIGGER_LENGTH / 2, TOUCH_ENLARGE_CHECK_REGION,
                mCenterRectF.height() - TRIGGER_LENGTH).contains(initX, initY)) {
            handler = getTouchRegionHandler(CropBaseHandler.RIGHT);
        } else if (hasNoAspectRatio() && createRectF(mCenterRectF.left + TRIGGER_LENGTH / 2,
                mCenterRectF.bottom + TOUCH_ENLARGE_CHECK_REGION / 2,
                mCenterRectF.width() - TRIGGER_LENGTH,
                TOUCH_ENLARGE_CHECK_REGION).contains(initX, initY)) {
            handler = getTouchRegionHandler(CropBaseHandler.BOTTOM);
        } else if (createRectF(mCenterRectF.left - TOUCH_ENLARGE_CHECK_REGION / 2,
                mCenterRectF.top - TOUCH_ENLARGE_CHECK_REGION / 2,
                TRIGGER_LENGTH / 2 + TOUCH_ENLARGE_CHECK_REGION / 2,
                TRIGGER_LENGTH / 2 + TOUCH_ENLARGE_CHECK_REGION / 2).contains(initX, initY)) {
            handler = getTouchRegionHandler(CropBaseHandler.LEFT_TOP);
        } else if (createRectF(mCenterRectF.left - TOUCH_ENLARGE_CHECK_REGION / 2,
                mCenterRectF.bottom - TRIGGER_LENGTH / 2,
                TRIGGER_LENGTH / 2 + TOUCH_ENLARGE_CHECK_REGION / 2,
                TRIGGER_LENGTH / 2 + TOUCH_ENLARGE_CHECK_REGION / 2).contains(initX, initY)) {
            handler = getTouchRegionHandler(CropBaseHandler.LEFT_BOTTOM);
        } else if (createRectF(mCenterRectF.left + mCenterRectF.width() - TRIGGER_LENGTH / 2,
                mCenterRectF.bottom - TRIGGER_LENGTH / 2,
                TRIGGER_LENGTH / 2 + TOUCH_ENLARGE_CHECK_REGION / 2,
                TRIGGER_LENGTH / 2 + TOUCH_ENLARGE_CHECK_REGION / 2).contains(initX, initY)) {
            handler = getTouchRegionHandler(CropBaseHandler.RIGHT_BOTTOM);
        } else if (createRectF(mCenterRectF.left + mCenterRectF.width() - TRIGGER_LENGTH / 2,
                mCenterRectF.top - TOUCH_ENLARGE_CHECK_REGION / 2,
                TRIGGER_LENGTH / 2 + TOUCH_ENLARGE_CHECK_REGION / 2,
                TRIGGER_LENGTH / 2 + TOUCH_ENLARGE_CHECK_REGION / 2).contains(initX, initY)) {
            handler = getTouchRegionHandler(CropBaseHandler.RIGHT_TOP);
        } else if (createRectF(mCenterRectF.left + TOUCH_ENLARGE_CHECK_REGION / 2,
                mCenterRectF.top + TOUCH_ENLARGE_CHECK_REGION / 2,
                mCenterRectF.width() - TOUCH_ENLARGE_CHECK_REGION,
                mCenterRectF.height() - TOUCH_ENLARGE_CHECK_REGION).contains(initX, initY)) {
            handler = getTouchRegionHandler(CropBaseHandler.CENTER);
        }
        if (handler != null) {
            handler.onTouchDown(event);
        }
        return handler;
    }

    private RectF createRectF(float left, float top, float width, float height) {
        return new RectF(left, top, left + width, top + height);
    }

    // 获取触摸处理器
    private CropBaseHandler getTouchRegionHandler(int touchRegion) {
        if (mHandlerMap == null) {
            mHandlerMap = new SparseArray<>();
        }
        CropBaseHandler handler = mHandlerMap.get(touchRegion);
        if (handler == null) {
            if (touchRegion == CropBaseHandler.TWO_FINGER) {
                handler = new TwoFingerHandler();
            } else if (touchRegion == CropBaseHandler.CENTER) {
                handler = new MoveHandler();
            } else if (hasNoAspectRatio()) {
                handler = new NoAspectRatioCropHandler();
            } else {
                handler = new AspectRatioCropHandler();
            }
            mHandlerMap.put(touchRegion, handler);
        }
        handler.init(touchRegion, mCenterRectF, mWidth, mHeight, MIN_WIDTH, MIN_HEIGHT, mAspectRatio);
        return handler;
    }

    private void animToTargetRectF(final RectF targetRectF) {
        if (targetRectF == null) {
            return;
        }
        final RectF currentRectF = new RectF(mCenterRectF);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                mCenterRectF.set(currentRectF.left + (targetRectF.left - currentRectF.left) * progress,
                        currentRectF.top + (targetRectF.top - currentRectF.top) * progress,
                        currentRectF.right + (targetRectF.right - currentRectF.right) * progress,
                        currentRectF.bottom + (targetRectF.bottom - currentRectF.bottom) * progress);
                updateBackgroundRectF();
                postInvalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setEnabled(true);
                animation.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setDuration(250);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
        setEnabled(false);
    }
}
