package com.juphoon.imgeditor.core.homing;

import android.animation.TypeEvaluator;

public class PictureHomingEvaluator implements TypeEvaluator<PictureHoming> {

    private PictureHoming homing;

    public PictureHomingEvaluator() {

    }

    public PictureHomingEvaluator(PictureHoming homing) {
        this.homing = homing;
    }

    @Override
    public PictureHoming evaluate(float fraction, PictureHoming startValue, PictureHoming endValue) {
        float x = startValue.x + fraction * (endValue.x - startValue.x);
        float y = startValue.y + fraction * (endValue.y - startValue.y);
        float scale = startValue.scale + fraction * (endValue.scale - startValue.scale);
        float rotate = startValue.rotate + fraction * (endValue.rotate - startValue.rotate);

        if (homing == null) {
            homing = new PictureHoming(x, y, scale, rotate);
        } else {
            homing.set(x, y, scale, rotate);
        }

        return homing;
    }
}
