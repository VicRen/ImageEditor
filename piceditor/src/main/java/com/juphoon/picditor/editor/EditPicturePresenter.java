package com.juphoon.picditor.editor;

import android.content.Intent;
import android.text.TextUtils;

import java.io.File;

class EditPicturePresenter implements EditPictureContract.Presenter {

    private EditPictureContract.View mView;
    private int mCurrentMode;
    private String mOutputPath;

    EditPicturePresenter() {
        mCurrentMode = EditPictureContract.MODE_NONE;
    }

    @Override
    public void setView(EditPictureContract.View view) {
        mView = view;
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void handleIntent(Intent intent) {
        if (!intent.hasExtra(EditPictureContract.EXTRA_PICTURE_PATH) || !intent.hasExtra(EditPictureContract.EXTRA_OUTPUT_PATH)) {
            mView.onCancelEdit();
            return;
        }
        mOutputPath = intent.getStringExtra(EditPictureContract.EXTRA_OUTPUT_PATH);
        String filePath = intent.getStringExtra(EditPictureContract.EXTRA_PICTURE_PATH);
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(mOutputPath)) {
            mView.onCancelEdit();
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            mView.onCancelEdit();
            return;
        }
        mView.onLoadOriginImage(filePath);
    }

    @Override
    public int getMode() {
        return mCurrentMode;
    }

    @Override
    public void toggleDoodle() {
        mCurrentMode = mCurrentMode == EditPictureContract.MODE_DOODLE ? EditPictureContract.MODE_NONE : EditPictureContract.MODE_DOODLE;
        mView.onModeChanged(mCurrentMode);
    }

    @Override
    public void toggleText() {
        mCurrentMode = mCurrentMode == EditPictureContract.MODE_TEXT ? EditPictureContract.MODE_NONE : EditPictureContract.MODE_TEXT;
        mView.onModeChanged(mCurrentMode);
    }

    @Override
    public void toggleMosaic() {
        mCurrentMode = mCurrentMode == EditPictureContract.MODE_MOSAIC ? EditPictureContract.MODE_NONE : EditPictureContract.MODE_MOSAIC;
        mView.onModeChanged(mCurrentMode);
    }

    @Override
    public void toggleCrop() {
        mCurrentMode = mCurrentMode == EditPictureContract.MODE_CROP ? EditPictureContract.MODE_NONE : EditPictureContract.MODE_CROP;
        mView.onModeChanged(mCurrentMode);
    }

    @Override
    public void cancel() {
        mView.onCancelEdit();
    }

    @Override
    public void save() {
    }
}
