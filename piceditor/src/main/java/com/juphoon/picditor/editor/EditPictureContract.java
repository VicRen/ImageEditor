package com.juphoon.picditor.editor;

import android.content.Intent;

public class EditPictureContract {


    public static final String EXTRA_PICTURE_PATH = "picturePath";
    public static final String EXTRA_OUTPUT_PATH = "outputPath";

    static final int MODE_NONE = 0;
    static final int MODE_CROP = 1;
    static final int MODE_TEXT = 2;
    static final int MODE_DOODLE = 3;
    static final int MODE_MOSAIC = 4;

    interface View {

        void onLoadOriginImage(String filePath);

        void onEditDone();

        void onCancelEdit();

        void onModeChanged(int mode);

        void onSetMenuVisibility(int visibility);
    }

    interface Presenter {

        void setView(View view);

        void resume();

        void pause();

        void destroy();

        void handleIntent(Intent intent);

        int getMode();

        void toggleDoodle();

        void toggleText();

        void toggleMosaic();

        void toggleCrop();

        void cancel();

        void save();
    }
}
