package com.juphoon.imgeditor.core.sticker;

import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.View;

public class PictureStickerMoveHelper {

    private static final String TAG = "PictureStickerMoveHelper";

    private View mView;

    private float mX, mY;

    private static final Matrix M = new Matrix();

    public PictureStickerMoveHelper(View view) {
        mView = view;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mX = event.getX();
                mY = event.getY();
                M.reset();
                M.setRotate(v.getRotation());
                return true;
            case MotionEvent.ACTION_MOVE:
                float[] dxy = {event.getX() - mX, event.getY() - mY};
                M.mapPoints(dxy);
                v.setTranslationX(mView.getTranslationX() + dxy[0]);
                v.setTranslationY(mView.getTranslationY() + dxy[1]);
                return true;
        }
        return false;
    }
}
