package com.juphoon.imgeditor.core.sticker;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.View;

public class PictureStickerHelper<StickerView extends View & PictureSticker> implements
        PictureStickerPortrait, PictureStickerPortrait.Callback {

    private RectF mFrame;

    private StickerView mView;

    private Callback mCallback;

    private boolean isShowing = false;

    public PictureStickerHelper(StickerView view) {
        mView = view;
    }

    @Override
    public boolean show() {
        if (!isShowing()) {
            isShowing = true;
            onShowing(mView);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove() {
        return onRemove(mView);
    }

    @Override
    public boolean dismiss() {
        if (isShowing()) {
            isShowing = false;
            onDismiss(mView);
            return true;
        }
        return false;
    }

    @Override
    public boolean isShowing() {
        return isShowing;
    }

    @Override
    public boolean startDrag() {
        return onDragStart(mView);
    }

    @Override
    public boolean dragging(float x, float y) {
        return onDragging(mView, x, y);
    }

    @Override
    public boolean stopDrag(float x, float y) {
        return onDragDone(mView, x, y);
    }

    @Override
    public RectF getFrame() {
        if (mFrame == null) {
            mFrame = new RectF(0, 0, mView.getWidth(), mView.getHeight());
            float pivotX = mView.getX() + mView.getPivotX();
            float pivotY = mView.getY() + mView.getPivotY();

            Matrix matrix = new Matrix();
            matrix.setTranslate(mView.getX(), mView.getY());
            matrix.postScale(mView.getScaleX(), mView.getScaleY(), pivotX, pivotY);
            matrix.mapRect(mFrame);
        }
        return mFrame;
    }

    @Override
    public void onSticker(Canvas canvas) {
        // empty
    }

    @Override
    public void registerCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void unregisterCallback(Callback callback) {
        mCallback = null;
    }

    @Override
    public <V extends View & PictureSticker> boolean onRemove(V stickerView) {
        return mCallback != null && mCallback.onRemove(stickerView);
    }

    @Override
    public <V extends View & PictureSticker> boolean onDragStart(V stickerView) {
        return mCallback != null && mCallback.onDragStart(stickerView);
    }

    @Override
    public <V extends View & PictureSticker> boolean onDragging(V stickerView, float x, float y) {
        return mCallback != null && mCallback.onDragging(stickerView, x, y);
    }

    @Override
    public <V extends View & PictureSticker> boolean onDragDone(V stickerView, float x, float y) {
        return mCallback != null && mCallback.onDragDone(stickerView, x, y);
    }

    @Override
    public <V extends View & PictureSticker> void onDismiss(V stickerView) {
        mFrame = null;
        stickerView.invalidate();
        if (mCallback != null) {
            mCallback.onDismiss(stickerView);
        }
    }

    @Override
    public <V extends View & PictureSticker> void onShowing(V stickerView) {
        stickerView.invalidate();
        if (mCallback != null) {
            mCallback.onShowing(stickerView);
        }
    }
}
