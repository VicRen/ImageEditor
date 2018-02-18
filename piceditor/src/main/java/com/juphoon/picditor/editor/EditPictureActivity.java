package com.juphoon.picditor.editor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.juphoon.picditor.R;
import com.juphoon.picditor.crop.CropFragment;
import com.juphoon.picditor.utils.ActivityUtils;

public class EditPictureActivity extends AppCompatActivity implements EditPictureContract.View {

    public static void startEditPicture(Activity activity, int requestCode, String filePath, String outputPath) {
        Intent intent = new Intent(activity, EditPictureActivity.class);
        intent.putExtra(EditPictureContract.EXTRA_PICTURE_PATH, filePath);
        intent.putExtra(EditPictureContract.EXTRA_OUTPUT_PATH, outputPath);
        activity.startActivityForResult(intent, requestCode);
    }

    private ImageView mIvOrigin;
    private View mMenuContainer;
    private View mMenuDoodle;
    private View mMenuText;
    private View mMenuMosaic;
    private View mMenuCrop;
    private View mTextMenuView;
    private View mDoodleMenuView;
    private View mMosaicMenuView;
    private View mCropContainer;

    private Fragment mCropFragment;

    private EditPicturePresenter mPresenter;

    private int angle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);

        mIvOrigin = findViewById(R.id.imgOrigin);
        mMenuContainer = findViewById(R.id.menuContainer);
        mMenuDoodle = findViewById(R.id.imgDoodle);
        mMenuDoodle.setOnClickListener(mOnClickListener);
        mMenuText = findViewById(R.id.imgText);
        mMenuText.setOnClickListener(mOnClickListener);
        mMenuMosaic = findViewById(R.id.imgMosaic);
        mMenuMosaic.setOnClickListener(mOnClickListener);
        mMenuCrop = findViewById(R.id.imgCrop);
        mMenuCrop.setOnClickListener(mOnClickListener);
        findViewById(R.id.tvCancel).setOnClickListener(mOnClickListener);
        findViewById(R.id.tvSave).setOnClickListener(mOnClickListener);
        mCropContainer = findViewById(R.id.cropContainer);
        mTextMenuView = findViewById(R.id.textMenu);
        mDoodleMenuView = findViewById(R.id.doodleMenu);
        mMosaicMenuView = findViewById(R.id.mosaicMenu);

        mPresenter = new EditPicturePresenter();
        mPresenter.setView(this);
        mPresenter.handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void onLoadOriginImage(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        mIvOrigin.setImageBitmap(bitmap);

        mCropFragment = CropFragment.newInstance(filePath);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mCropFragment,
                R.id.cropContainer);
    }

    @Override
    public void onEditDone() {

    }

    @Override
    public void onCancelEdit() {
        finish();
    }

    @Override
    public void onModeChanged(int mode) {
        mMenuDoodle.setSelected(mode == EditPictureContract.MODE_DOODLE);
        mMenuText.setSelected(mode == EditPictureContract.MODE_TEXT);
        mMenuMosaic.setSelected(mode == EditPictureContract.MODE_MOSAIC);
        mMenuCrop.setSelected(mode == EditPictureContract.MODE_CROP);
        mCropContainer.setVisibility(mode == EditPictureContract.MODE_CROP ? View.VISIBLE : View.INVISIBLE);
        mIvOrigin.setVisibility(mode == EditPictureContract.MODE_CROP ? View.INVISIBLE : View.VISIBLE);
        mTextMenuView.setVisibility(mode == EditPictureContract.MODE_TEXT ? View.VISIBLE : View.INVISIBLE);
        mDoodleMenuView.setVisibility(mode == EditPictureContract.MODE_DOODLE ? View.VISIBLE : View.INVISIBLE);
        mMosaicMenuView.setVisibility(mode == EditPictureContract.MODE_MOSAIC ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onSetMenuVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            onModeChanged(mPresenter.getMode());
        } else {
            mMenuContainer.setVisibility(View.INVISIBLE);
            mCropContainer.setVisibility(View.INVISIBLE);
            mTextMenuView.setVisibility(View.INVISIBLE);
            mDoodleMenuView.setVisibility(View.INVISIBLE);
            mMosaicMenuView.setVisibility(View.INVISIBLE);
        }
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.imgDoodle) {
                mPresenter.toggleDoodle();
            } else if (id == R.id.imgText) {
                mPresenter.toggleText();
            } else if (id == R.id.imgMosaic) {
                mPresenter.toggleMosaic();
            } else if (id == R.id.imgCrop) {
                mPresenter.toggleCrop();
            } else if (id == R.id.tvCancel) {
                mPresenter.cancel();
            } else if (id == R.id.tvSave) {
                mPresenter.save();
            } else if (id == R.id.imgLeft) {
                // TODO turn left
            } else if (id == R.id.imgRight) {
                // TODO turn right
            } else if (id == R.id.tvRestore) {
                // TODO restore
            }
        }
    };
}
