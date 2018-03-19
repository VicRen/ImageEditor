package com.juphoon.imgeditor.core.sticker;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import com.juphoon.imgeditor.view.PictureStickerView;

public class PictureStickerMoveHelper {

    public interface DragListener {

        void onDragStart();

        void onDragging(float x, float y);

        void onDragDone(float x, float y);
    }

    private static final String TAG = "MoveHelper";

    private static final int MODE_NONE = 0;
    private static final int MODE_DRAG = 1;
    private static final int MODE_ZOOM = 2;

    private static final Matrix M = new Matrix();
    private View mView;
    private float mX, mY;
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float oldRotation = 0;
    private int mMode;
    private DragListener mDragListener;

    public PictureStickerMoveHelper(View view, DragListener listener) {
        mView = view;
        mMode = MODE_NONE;
        mDragListener = listener;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mMode = MODE_DRAG;
                mX = event.getX();
                mY = event.getY();
                M.reset();
                M.setRotate(v.getRotation());
                if (mDragListener != null) {
                    mDragListener.onDragStart();
                }
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                mMode = MODE_ZOOM;
                oldDist = calculateDistance(event);
                oldRotation = rotation(event);
                M.reset();
                M.setRotate(v.getRotation());
                calculateMiddlePoint(mid, event);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mMode == MODE_DRAG) {
                    float[] dxy = {event.getX() - mX, event.getY() - mY};
                    M.mapPoints(dxy);
                    v.setTranslationX(mView.getTranslationX() + dxy[0]);
                    v.setTranslationY(mView.getTranslationY() + dxy[1]);
                    if (mDragListener != null) {
                        mDragListener.onDragging(event.getRawX(), event.getRawY());
                    }
                } else if (mMode == MODE_ZOOM) {
                    float rotation = rotation(event) - oldRotation;
                    mView.setRotation(v.getRotation() + rotation);
                    float newDist = calculateDistance(event);
                    float scale = newDist / oldDist;
                    ((PictureStickerView) mView).addScale(scale);
                    oldDist = newDist;
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mMode = MODE_NONE;
                mDragListener.onDragDone(event.getRawX(), event.getRawY());
                return true;
        }
        return false;
    }

    // 触碰两点间距离
    private float calculateDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取手势中心点
    private void calculateMiddlePoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x/2, y/2);
    }

    // 取旋转角度
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        /**
         * 反正切函数
         * 计算两个坐标点的正切角度
         */
        double radians = Math.atan2(delta_y, delta_x);
        return (float)(Math.toDegrees(radians));
    }
}
