package com.juphoon.imgeditor.core;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

public class PicturePath {

    protected Path path;

    private int color = Color.RED;

    private float width = BASE_MOSAIC_WIDTH;

    private PictureMode mode = PictureMode.DOODLE;

    public static final float BASE_DOODLE_WIDTH = 20f;

    public static final float BASE_MOSAIC_WIDTH = 72f;

    public PicturePath() {
        this(new Path());
    }

    public PicturePath(Path path) {
        this(path, PictureMode.DOODLE);
    }

    public PicturePath(Path path, PictureMode mode) {
        this(path, mode, Color.RED);
    }

    public PicturePath(Path path, PictureMode mode, int color) {
        this(path, mode, color, BASE_MOSAIC_WIDTH);
    }

    public PicturePath(Path path, PictureMode mode, int color, float width) {
        this.path = path;
        this.mode = mode;
        this.color = color;
        this.width = width;
        if (mode == PictureMode.MOSAIC) {
            path.setFillType(Path.FillType.EVEN_ODD);
        }
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public PictureMode getMode() {
        return mode;
    }

    public void setMode(PictureMode mode) {
        this.mode = mode;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidth() {
        return width;
    }

    public void onDrawDoodle(Canvas canvas, Paint paint) {
        if (mode == PictureMode.DOODLE) {
            paint.setColor(color);
            paint.setStrokeWidth(BASE_DOODLE_WIDTH);
            // rewind
            canvas.drawPath(path, paint);
        }
    }

    public void onDrawMosaic(Canvas canvas, Paint paint) {
        if (mode == PictureMode.MOSAIC) {
            paint.setStrokeWidth(width);
            canvas.drawPath(path, paint);
        }
    }

    public void transform(Matrix matrix) {
        path.transform(matrix);
    }
}
