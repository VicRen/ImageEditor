package com.juphoon.imgeditor.core.sticker;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.View;

public interface PictureStickerPortrait {

    boolean show();

    boolean remove();

    boolean dismiss();

    boolean isShowing();

    RectF getFrame();

//    RectF getAdjustFrame();
//
//    RectF getDeleteFrame();

    void onSticker(Canvas canvas);

    void registerCallback(PictureSticker.Callback callback);

    void unregisterCallback(PictureSticker.Callback callback);

    interface Callback {

        <V extends View & PictureSticker> void onDismiss(V stickerView);

        <V extends View & PictureSticker> void onShowing(V stickerView);

        <V extends View & PictureSticker> boolean onRemove(V stickerView);
    }
}
