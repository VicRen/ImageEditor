package com.juphoon.imgeditor;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.ViewSwitcher;

import com.juphoon.imgeditor.core.PictureMode;
import com.juphoon.imgeditor.core.PictureText;
import com.juphoon.imgeditor.view.PictureColorGroup;
import com.juphoon.imgeditor.view.PictureStrokeGroup;
import com.juphoon.imgeditor.view.PictureView;

abstract class PictureEditBaseActivity extends Activity implements View.OnClickListener,
        PictureTextEditDialog.Callback, RadioGroup.OnCheckedChangeListener,
        DialogInterface.OnShowListener, DialogInterface.OnDismissListener, PictureView.PictureViewListener {

    protected PictureView mPictureView;

    private RadioGroup mModeGroup;

    private PictureColorGroup mColorGroup;

    private PictureStrokeGroup mStrokeGroup;

    private PictureTextEditDialog mTextDialog;

    private View mLayoutOpSub;

    private ViewSwitcher mOpSwitcher, mOpSubSwitcher;

    private ImageButton mBtnUndo;

    public static final int OP_HIDE = -1;

    public static final int OP_NORMAL = 0;

    public static final int OP_CLIP = 1;

    public static final int OP_SUB_DOODLE = 0;

    public static final int OP_SUB_MOSAIC = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap bitmap = getBitmap();
        if (bitmap != null) {
            setContentView(R.layout.image_edit_activity);
            initViews();
            mPictureView.setImageBitmap(bitmap);
            mPictureView.setPictureViewListener(this);
            onCreated();
        } else {
            finish();
        }
    }

    public void onCreated() {
    }

    private void initViews() {
        mPictureView = findViewById(R.id.image_canvas);
        mModeGroup = findViewById(R.id.rg_modes);

        mOpSwitcher = findViewById(R.id.vs_op);
        mOpSubSwitcher = findViewById(R.id.vs_op_sub);

        mColorGroup = findViewById(R.id.cg_colors);
        mColorGroup.setOnCheckedChangeListener(this);
        onColorChanged(mColorGroup.getCheckColor());

        mStrokeGroup = findViewById(R.id.cg_strokes);
        mStrokeGroup.setOnCheckedChangeListener(this);
        onStrokeChanged(57);

        mLayoutOpSub = findViewById(R.id.layout_op_sub);
        mBtnUndo = findViewById(R.id.btn_undo);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.rb_doodle) {
            onModeClick(PictureMode.DOODLE);
        } else if (vid == R.id.btn_text) {
            onModeClick(PictureMode.NONE);
            onTextModeClick();
        } else if (vid == R.id.rb_mosaic) {
            onModeClick(PictureMode.MOSAIC);
        } else if (vid == R.id.btn_clip) {
            onModeClick(PictureMode.CLIP);
        } else if (vid == R.id.btn_undo) {
            onUndoClick();
            updateUndoButton(mPictureView.getMode());
        } else if (vid == R.id.tv_done) {
            onDoneClick();
        } else if (vid == R.id.tv_cancel) {
            onCancelClick();
        } else if (vid == R.id.ib_clip_cancel) {
            onCancelClipClick();
        } else if (vid == R.id.ib_clip_done) {
            onDoneClipClick();
        } else if (vid == R.id.tv_clip_reset) {
            onResetClipClick();
        } else if (vid == R.id.ib_clip_rotate) {
            onRotateClipClick();
        } else if (vid == R.id.ib_clip_rotate_right) {
            onRotateClipRightClick();
        }
    }

    public void updateModeUI() {
        PictureMode mode = mPictureView.getMode();
        switch (mode) {
            case DOODLE:
                mModeGroup.check(R.id.rb_doodle);
                setOpSubDisplay(OP_SUB_DOODLE);
                break;
            case MOSAIC:
                mModeGroup.check(R.id.rb_mosaic);
                setOpSubDisplay(OP_SUB_MOSAIC);
                break;
            case NONE:
                mModeGroup.clearCheck();
                setOpSubDisplay(OP_HIDE);
                break;
        }
        updateUndoButton(mode);
    }

    public void onTextModeClick() {
        if (mTextDialog == null) {
            mTextDialog = new PictureTextEditDialog(this, this);
            mTextDialog.setOnShowListener(this);
            mTextDialog.setOnDismissListener(this);
        }
        mTextDialog.show();
    }

    @Override
    public final void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group instanceof PictureColorGroup) {
            onColorChanged(mColorGroup.getCheckColor());
        } else if (group instanceof PictureStrokeGroup) {
            onStrokeChanged(mStrokeGroup.getCheckStroke());
        }
    }

    public void setOpDisplay(int op) {
        if (op >= 0) {
            mOpSwitcher.setDisplayedChild(op);
        }
    }

    public void setOpSubDisplay(int opSub) {
        if (opSub < 0) {
            mLayoutOpSub.setVisibility(View.GONE);
        } else {
            mOpSubSwitcher.setDisplayedChild(opSub);
            mLayoutOpSub.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        mOpSwitcher.setVisibility(View.GONE);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mOpSwitcher.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMoveBegin(PictureMode mode) {
        if (mode == PictureMode.DOODLE
                || mode == PictureMode.MOSAIC) {
            mOpSwitcher.setVisibility(View.INVISIBLE);
        }
        updateUndoButton(mode);
    }

    @Override
    public void onMoveEnd() {
        if (mOpSwitcher.getVisibility() == View.INVISIBLE) {
            mOpSwitcher.setVisibility(View.VISIBLE);
        }
        updateUndoButton(mPictureView.getMode());
    }

    private void updateUndoButton(PictureMode mode) {
        if (mode == PictureMode.DOODLE) {
            mBtnUndo.setEnabled(!mPictureView.isDoodleEmpty());
        } else if (mode == PictureMode.MOSAIC) {
            mBtnUndo.setEnabled(!mPictureView.isMosaicEmpty());
        }
    }

    public abstract Bitmap getBitmap();

    public abstract void onModeClick(PictureMode mode);

    public abstract void onUndoClick();

    public abstract void onCancelClick();

    public abstract void onDoneClick();

    public abstract void onCancelClipClick();

    public abstract void onDoneClipClick();

    public abstract void onResetClipClick();

    public abstract void onRotateClipClick();

    public abstract void onRotateClipRightClick();

    public abstract void onColorChanged(int checkedColor);

    public abstract void onStrokeChanged(int checkedWidth);

    @Override
    public abstract void onText(PictureText text);
}
