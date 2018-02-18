package com.juphoon.picditor.crop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.juphoon.picditor.R;
import com.juphoon.picditor.crop.rotate.RotateFrameLayout;

public class CropFragment extends Fragment {

    private RotateFrameLayout mContainer;
    private CropOverlayView mCropView;
    private ImageView mTvImgOrigin;
    private String mFilePath;

    public static CropFragment newInstance(String filePath) {
        CropFragment cropFragment = new CropFragment();
        cropFragment.mFilePath = filePath;
        return cropFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_crop, container, false);
        mTvImgOrigin = rootView.findViewById(R.id.imgOrigin);
        Bitmap bitmap = BitmapFactory.decodeFile(mFilePath);
        mTvImgOrigin.setImageBitmap(bitmap);
        mCropView = rootView.findViewById(R.id.cropView);
        mContainer = rootView.findViewById(R.id.container);
        rootView.findViewById(R.id.imgLeft).setOnClickListener(mClickListener);
        rootView.findViewById(R.id.imgRight).setOnClickListener(mClickListener);
        rootView.findViewById(R.id.tvRestore).setOnClickListener(mClickListener);
        mCropView.attachImage(bitmap, mTvImgOrigin.getWidth(), mTvImgOrigin.getHeight(), 1, mTvImgOrigin);
        return rootView;
    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.imgLeft) {
                mContainer.rotateLeft();
            } else if (id == R.id.imgRight) {
                mContainer.rotateRight();
            } else if (id == R.id.tvRestore) {
                mContainer.reset();
            }
        }
    };
}
