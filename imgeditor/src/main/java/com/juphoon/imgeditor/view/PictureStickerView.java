package com.juphoon.imgeditor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.juphoon.imgeditor.core.sticker.PictureSticker;
import com.juphoon.imgeditor.core.sticker.PictureStickerHelper;
import com.juphoon.imgeditor.core.sticker.PictureStickerMoveHelper;

public abstract class PictureStickerView extends ViewGroup implements PictureSticker, PictureStickerMoveHelper.DragListener {

    private static final String TAG = "PictureStickerView";

    private View mContentView;

    private float mScale = 1f;

    // TODO
    private int mDownShowing = 0;

    private PictureStickerMoveHelper mMoveHelper;

    private PictureStickerHelper<PictureStickerView> mStickerHelper;

    private float mMaxScaleValue = MAX_SCALE_VALUE;

    private Paint PAINT;

    private Matrix mMatrix = new Matrix();

    private RectF mFrame = new RectF();

    private Rect mTempFrame = new Rect();

    private boolean mEnableInterceptTouch = true;

    private static final float MAX_SCALE_VALUE = 4f;

    private static final int ANCHOR_SIZE = 48;

    private static final int ANCHOR_SIZE_HALF = ANCHOR_SIZE >> 1;

    private static final float STROKE_WIDTH = 3f;

    {
        PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
        PAINT.setColor(Color.WHITE);
        PAINT.setStyle(Paint.Style.STROKE);
        PAINT.setStrokeWidth(STROKE_WIDTH);
    }

    public PictureStickerView(Context context) {
        this(context, null, 0);
    }

    public PictureStickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PictureStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onInitialize(context);
    }

    public void onInitialize(Context context) {
        setBackgroundColor(Color.TRANSPARENT);

        mContentView = onCreateContentView(context);
        addView(mContentView, getContentLayoutParams());

        mStickerHelper = new PictureStickerHelper<>(this);
        mMoveHelper = new PictureStickerMoveHelper(this, this);
    }

    public abstract View onCreateContentView(Context context);

    @Override
    public void enableInterceptTouch(boolean enabled) {
        mEnableInterceptTouch = enabled;
    }

    @Override
    public float getScale() {
        return mScale;
    }

    @Override
    public void setScale(float scale) {
        if (scale > 6f || scale < 0.3f) {
            return;
        }
        mScale = scale;

        mContentView.setScaleX(mScale);
        mContentView.setScaleY(mScale);

        int pivotX = (getLeft() + getRight()) >> 1;
        int pivotY = (getTop() + getBottom()) >> 1;

        mFrame.set(pivotX, pivotY, pivotX, pivotY);
        mFrame.inset(-(mContentView.getMeasuredWidth() >> 1), -(mContentView.getMeasuredHeight() >> 1));

        mMatrix.setScale(mScale, mScale, mFrame.centerX(), mFrame.centerY());
        mMatrix.mapRect(mFrame);

        mFrame.round(mTempFrame);

        layout(mTempFrame.left, mTempFrame.top, mTempFrame.right, mTempFrame.bottom);
    }

    @Override
    public void addScale(float scale) {
        setScale(getScale() * scale);
    }

    private LayoutParams getContentLayoutParams() {
        return new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
    }

    private LayoutParams getAnchorLayoutParams() {
        return new LayoutParams(ANCHOR_SIZE, ANCHOR_SIZE);
    }

    @Override
    public void draw(Canvas canvas) {
        // disable the rect when chosen
//        if (isShowing()) {
//            canvas.drawRect(ANCHOR_SIZE_HALF, ANCHOR_SIZE_HALF,
//                    getWidth() - ANCHOR_SIZE_HALF,
//                    getHeight() - ANCHOR_SIZE_HALF, PAINT);
//        }
        super.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.measure(widthMeasureSpec, heightMeasureSpec);

                maxWidth = Math.round(Math.max(maxWidth, child.getMeasuredWidth() * child.getScaleX()));
                maxHeight = Math.round(Math.max(maxHeight, child.getMeasuredHeight() * child.getScaleY()));

                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        mFrame.set(left, top, right, bottom);

        int count = getChildCount();
        if (count == 0) {
            return;
        }

        int centerX = (right - left) >> 1, centerY = (bottom - top) >> 1;
        int hw = mContentView.getMeasuredWidth() >> 1;
        int hh = mContentView.getMeasuredHeight() >> 1;

        mContentView.layout(centerX - hw, centerY - hh, centerX + hw, centerY + hh);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return isShowing() && super.drawChild(canvas, child, drawingTime);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mEnableInterceptTouch) {
            return super.onInterceptTouchEvent(ev);
        }
        if (!isShowing() && ev.getAction() == MotionEvent.ACTION_DOWN) {
            mDownShowing = 0;
            show();
            return true;
        }
        return isShowing() && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mEnableInterceptTouch) {
            return super.onTouchEvent(event);
        }

        boolean handled = mMoveHelper.onTouch(this, event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownShowing++;
                break;
            case MotionEvent.ACTION_UP:
                if (mDownShowing > 1 && event.getEventTime() - event.getDownTime() < ViewConfiguration.getTapTimeout()) {
                    onContentTap();
                    return true;
                }
                break;
        }

        return handled | super.onTouchEvent(event);
    }

    public void onRemove() {
        mStickerHelper.remove();
    }

    public void onContentTap() {

    }

    @Override
    public boolean show() {
        return mStickerHelper.show();
    }

    @Override
    public boolean remove() {
        return mStickerHelper.remove();
    }

    @Override
    public boolean dismiss() {
        return mStickerHelper.dismiss();
    }

    @Override
    public boolean startDrag() {
        return mStickerHelper.startDrag();
    }

    @Override
    public boolean dragging(float x, float y) {
        return mStickerHelper.dragging(x, y);
    }

    @Override
    public boolean stopDrag(float x, float y) {
        return mStickerHelper.stopDrag(x, y);
    }

    @Override
    public boolean isShowing() {
        return mStickerHelper.isShowing();
    }

    @Override
    public RectF getFrame() {
        return mStickerHelper.getFrame();
    }

    @Override
    public void onSticker(Canvas canvas) {
        canvas.translate(mContentView.getX(), mContentView.getY());
        mContentView.draw(canvas);
    }

    @Override
    public void registerCallback(Callback callback) {
        mStickerHelper.registerCallback(callback);
    }

    @Override
    public void unregisterCallback(Callback callback) {
        mStickerHelper.unregisterCallback(callback);
    }

    @Override
    public void onDragStart() {
        mStickerHelper.startDrag();
    }

    @Override
    public void onDragging(float x, float y) {
        mStickerHelper.dragging(x, y);
    }

    @Override
    public void onDragDone(float x, float y) {
        mStickerHelper.stopDrag(x, y);
    }
}
