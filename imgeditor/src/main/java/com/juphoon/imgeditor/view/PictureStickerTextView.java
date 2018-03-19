package com.juphoon.imgeditor.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.juphoon.imgeditor.PictureTextEditDialog;
import com.juphoon.imgeditor.core.PictureText;

public class PictureStickerTextView extends PictureStickerView implements PictureTextEditDialog.Callback {

    private static final String TAG = "PictureStickerTextView";

    private TextView mTextView;

    private PictureText mText;

    private PictureTextEditDialog mDialog;

    private static float mBaseTextSize = -1f;

    private static final int PADDING = 26;

    private static final float TEXT_SIZE_SP = 8f;

    public PictureStickerTextView(Context context) {
        this(context, null, 0);
    }

    public PictureStickerTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PictureStickerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onInitialize(Context context) {
        if (mBaseTextSize <= 0) {
            mBaseTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TEXT_SIZE_SP, context.getResources().getDisplayMetrics());
        }
        super.onInitialize(context);
    }

    @Override
    public View onCreateContentView(Context context) {
        mTextView = new TextView(context);
        mTextView.setTextSize(mBaseTextSize);
        mTextView.setPadding(PADDING, PADDING, PADDING, PADDING);
        mTextView.setTextColor(Color.WHITE);

        return mTextView;
    }

    public void setText(PictureText text) {
        mText = text;
        if (mText != null && mTextView != null) {
            mTextView.setText(mText.getText());
            mTextView.setTextColor(mText.getColor());
        }
    }

    public PictureText getText() {
        return mText;
    }

    @Override
    public void onContentTap() {
        PictureTextEditDialog dialog = getDialog();
        dialog.setText(mText);
        dialog.show();
    }

    private PictureTextEditDialog getDialog() {
        if (mDialog == null) {
            mDialog = new PictureTextEditDialog(getContext(), this);
        }
        return mDialog;
    }

    @Override
    public void onText(PictureText text) {
        mText = text;
        if (mText != null && mTextView != null) {
            mTextView.setText(mText.getText());
            mTextView.setTextColor(mText.getColor());
        }
    }
}