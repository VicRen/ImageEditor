package com.juphoon.imgeditor.core.sticker;

public class PictureStickerTouchHelper {

    public interface Listener {

        void onDragStart();

        void onDraging();

        void onDragDone();

        void onReshapeStart();
    }
}
