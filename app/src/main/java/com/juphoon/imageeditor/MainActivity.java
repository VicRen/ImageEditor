package com.juphoon.imageeditor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.juphoon.imageeditor.utils.FileUtils;
import com.juphoon.imgeditor.PictureEditActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_PICTURE = 10;
    private static final int REQUEST_EDIT_PICTURE = 11;

    private ImageView mIvChosenImage;
    private String mCurrentChosenPicture;
    private Uri mChosenUri;
    private String mOutputFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIvChosenImage = findViewById(R.id.imgChosen);
        findViewById(R.id.btnChoosePic).setOnClickListener(mOnClickListener);
        findViewById(R.id.btnEditPic).setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_PICTURE) {
            if (resultCode == RESULT_OK) {
                if (!TextUtils.isEmpty(mOutputFilePath)) {
                    File file = new File(mOutputFilePath);
                    if (file.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(mOutputFilePath);
                        mIvChosenImage.setImageBitmap(bitmap);
                        return;
                    }
                }
                Toast.makeText(this, "Edit picture succeeded.", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, "Edit picture failed.", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_PICK_PICTURE) {
            if (resultCode == RESULT_OK) {
                mChosenUri = data.getData();
                mCurrentChosenPicture = FileUtils.handleImageOnKitKat(this, data);
                if (!TextUtils.isEmpty(mCurrentChosenPicture)) {
                    File file = new File(mCurrentChosenPicture);
                    if (file.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentChosenPicture);
                        mIvChosenImage.setImageBitmap(bitmap);
                    }
                }
            } else {
                Toast.makeText(this, "Choose picture failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startEditPicture() {
        if (TextUtils.isEmpty(mCurrentChosenPicture)) {
            Toast.makeText(this, "Please select a picture", Toast.LENGTH_SHORT).show();
            return;
        }
        mOutputFilePath = FileUtils.getOutputDir(this) + File.separator + System.currentTimeMillis() + ".jpg";
        Intent intent = new Intent(this, PictureEditActivity.class);
        intent.putExtra(PictureEditActivity.EXTRA_IMAGE_URI, mChosenUri);
        intent.putExtra(PictureEditActivity.EXTRA_IMAGE_SAVE_PATH, mOutputFilePath);
        startActivityForResult(intent, REQUEST_EDIT_PICTURE);
    }

    private void startChoosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_PICTURE);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btnEditPic) {
                startEditPicture();
            } else if (id == R.id.btnChoosePic) {
                startChoosePicture();
            }
        }
    };
}
