package com.juphoon.imgeditor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import com.juphoon.imgeditor.core.PictureMode;
import com.juphoon.imgeditor.core.PictureText;
import com.juphoon.imgeditor.core.file.PictureAssetFileDecoder;
import com.juphoon.imgeditor.core.file.PictureDecoder;
import com.juphoon.imgeditor.core.file.PictureDefaultDecoder;
import com.juphoon.imgeditor.core.file.PictureFileDecoder;
import com.juphoon.imgeditor.core.util.PictureUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PictureEditActivity extends PictureEditBaseActivity {

    private static final int MAX_WIDTH = 1024;

    private static final int MAX_HEIGHT = 1024;

    public static final String EXTRA_IMAGE_URI = "image_uri";

    public static final String EXTRA_IMAGE_SAVE_PATH = "image_save_path";

    @Override
    public void onCreated() {

    }

    @Override
    public Bitmap getBitmap() {
        Intent intent = getIntent();
        if (intent == null) {
            return null;
        }

        Uri uri = intent.getParcelableExtra(EXTRA_IMAGE_URI);
        if (uri == null) {
            return null;
        }

        PictureDecoder decoder = null;

        String path = uri.getPath();
        if (!TextUtils.isEmpty(path)) {
            switch (uri.getScheme()) {
                case "asset":
                    decoder = new PictureAssetFileDecoder(this, uri);
                    break;
                case "file":
                    decoder = new PictureFileDecoder(uri);
                    break;
                default:
                    decoder = new PictureDefaultDecoder(this, uri);
            }
        }

        if (decoder == null) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;

        decoder.decode(options);

        if (options.outWidth > MAX_WIDTH) {
            options.inSampleSize = PictureUtils.inSampleSize(Math.round(1f * options.outWidth / MAX_WIDTH));
        }

        if (options.outHeight > MAX_HEIGHT) {
            options.inSampleSize = Math.max(options.inSampleSize,
                    PictureUtils.inSampleSize(Math.round(1f * options.outHeight / MAX_HEIGHT)));
        }

        options.inJustDecodeBounds = false;

        Bitmap bitmap = decoder.decode(options);
        if (bitmap == null) {
            return null;
        }

        return bitmap;
    }

    @Override
    public void onText(PictureText text) {
        mPictureView.addStickerText(text);
    }

    @Override
    public void onModeClick(PictureMode mode) {
        PictureMode cm = mPictureView.getMode();
        if (cm == mode) {
            mode = PictureMode.NONE;
        }
        mPictureView.setMode(mode);
        updateModeUI();

        if (mode == PictureMode.CLIP) {
            setOpDisplay(OP_CLIP);
        }
    }

    @Override
    public void onUndoClick() {
        PictureMode mode = mPictureView.getMode();
        if (mode == PictureMode.DOODLE) {
            mPictureView.undoDoodle();
        } else if (mode == PictureMode.MOSAIC) {
            mPictureView.undoMosaic();
        }
    }

    @Override
    public void onCancelClick() {
        finish();
    }

    @Override
    public void onDoneClick() {
        String path = getIntent().getStringExtra(EXTRA_IMAGE_SAVE_PATH);
        if (!TextUtils.isEmpty(path)) {
            Bitmap bitmap = mPictureView.saveBitmap();
            if (bitmap != null) {
                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(path);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fout);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (fout != null) {
                        try {
                            fout.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                setResult(RESULT_OK);
                finish();
                return;
            }
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onCancelClipClick() {
        mPictureView.cancelClip();
        setOpDisplay(mPictureView.getMode() == PictureMode.CLIP ? OP_CLIP : OP_NORMAL);
    }

    @Override
    public void onDoneClipClick() {
        mPictureView.doClip();
        setOpDisplay(mPictureView.getMode() == PictureMode.CLIP ? OP_CLIP : OP_NORMAL);
    }

    @Override
    public void onResetClipClick() {
        mPictureView.resetClip();
    }

    @Override
    public void onRotateClipClick() {
        mPictureView.doRotate();
    }

    @Override
    public void onRotateClipRightClick() {
        mPictureView.doRotateRight();
    }

    @Override
    public void onColorChanged(int checkedColor) {
        mPictureView.setPenColor(checkedColor);
    }

    @Override
    public void onStrokeChanged(int checkedWidth) {
        mPictureView.setPenStrokeWidth(checkedWidth);
    }
}
