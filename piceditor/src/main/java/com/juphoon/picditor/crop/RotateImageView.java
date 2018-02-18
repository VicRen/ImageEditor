package com.juphoon.picditor.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.juphoon.picditor.utils.DrawUtils;

public class RotateImageView extends View {
    private Rect srcRect;
    private RectF dstRect;
    private Rect maxRect;// 最大限制矩形框

    private Bitmap bitmap;
    private Matrix matrix = new Matrix();// 辅助计算矩形

    private float scale;// 缩放比率
    private int rotateAngle;

    private RectF wrapRect = new RectF();// 图片包围矩形
    private Paint bottomPaint;
    private RectF originImageRect;

    public RotateImageView(Context context) {
        super(context, null);
    }

    public RotateImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (bitmap == null)
            return;
        maxRect.set(0, 0, getWidth(), getHeight());// 最大边界矩形

        calculateWrapBox();
        scale = 1;
        if (wrapRect.width() > getWidth()) {
            scale = getWidth() / wrapRect.width();
        }

        // 绘制图形
        canvas.save();
        canvas.scale(scale, scale, canvas.getWidth() >> 1,
                canvas.getHeight() >> 1);
        canvas.drawRect(wrapRect, bottomPaint);
        canvas.rotate(rotateAngle, canvas.getWidth() >> 1,
                canvas.getHeight() >> 1);
        canvas.drawBitmap(bitmap, srcRect, dstRect, null);
        canvas.restore();
    }

    public void addBitmap(Bitmap bit, RectF imageRect) {
        bitmap = bit;
        srcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        dstRect = imageRect;

        originImageRect.set(0, 0, bit.getWidth(), bit.getHeight());
        this.invalidate();
    }

    public void rotateImage(int angle) {
        rotateAngle = angle;
        this.invalidate();
    }

    public void reset() {
        rotateAngle = 0;
        scale = 1;
        invalidate();
    }

    /**
     * 取得旋转后新图片的大小
     *
     * @return
     */
    public RectF getImageNewRect() {
        Matrix m = new Matrix();
        m.postRotate(this.rotateAngle, originImageRect.centerX(),
                originImageRect.centerY());
        m.mapRect(originImageRect);
        return originImageRect;
    }

    /**
     * 缩放比率
     * @return
     */
    public synchronized float getScale() {
        return scale;
    }

    /**
     * 旋转角度
     * @return
     */
    public synchronized int getRotateAngle() {
        return rotateAngle;
    }

    private void init(Context context) {
        srcRect = new Rect();
        dstRect = new RectF();
        maxRect = new Rect();
        bottomPaint = DrawUtils.newPaint(Color.WHITE, 3, Paint.Style.FILL);
        originImageRect = new RectF();
    }

    /**
     * 计算出矩形包围盒
     */
    private void calculateWrapBox() {
        wrapRect.set(dstRect);
        matrix.reset();// 重置矩阵为单位矩阵
        int centerX = getWidth() >> 1;
        int centerY = getHeight() >> 1;
        matrix.postRotate(rotateAngle, centerX, centerY);// 旋转后的角度
        // System.out.println("旋转之前-->" + wrapRect.left + "    " + wrapRect.top
        // + "    " + wrapRect.right + "   " + wrapRect.bottom);
        matrix.mapRect(wrapRect);
        // System.out.println("旋转之后-->" + wrapRect.left + "    " + wrapRect.top
        // + "    " + wrapRect.right + "   " + wrapRect.bottom);
    }
}
