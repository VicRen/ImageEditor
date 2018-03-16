package com.juphoon.imgeditor.core.homing;

public class PictureHoming {

    public float x, y;

    public float scale;

    public float rotate;

    public PictureHoming(float x, float y, float scale, float rotate) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotate = rotate;
    }

    public void set(float x, float y, float scale, float rotate) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotate = rotate;
    }

    public void concat(PictureHoming homing) {
        this.scale *= homing.scale;
        this.x += homing.x;
        this.y += homing.y;
    }

    public void rConcat(PictureHoming homing) {
        this.scale *= homing.scale;
        this.x -= homing.x;
        this.y -= homing.y;
    }

    public static boolean isRotate(PictureHoming sHoming, PictureHoming eHoming) {
        return Float.compare(sHoming.rotate, eHoming.rotate) != 0;
    }

    @Override
    public String toString() {
        return "PictureHoming{" +
                "x=" + x +
                ", y=" + y +
                ", scale=" + scale +
                ", rotate=" + rotate +
                '}';
    }
}
