package com.juphoon.imgeditor.core.homing;

import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

public class PictureHomingAnimator extends ValueAnimator {

    private boolean isRotate = false;

    private PictureHomingEvaluator mEvaluator;

    public PictureHomingAnimator() {
        setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    public void setObjectValues(Object... values) {
        super.setObjectValues(values);
        if (mEvaluator == null) {
            mEvaluator = new PictureHomingEvaluator();
        }
        setEvaluator(mEvaluator);
    }

    public void setHomingValues(PictureHoming sHoming, PictureHoming eHoming) {
        setObjectValues(sHoming, eHoming);
        isRotate = PictureHoming.isRotate(sHoming, eHoming);
    }

    public boolean isRotate() {
        return isRotate;
    }
}
