package com.juphoon.imgeditor.core.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

public class PictureFileDecoder extends PictureDecoder {

    public PictureFileDecoder(Uri uri) {
        super(uri);
    }

    @Override
    public Bitmap decode(BitmapFactory.Options options) {
        Uri uri = getUri();
        if (uri == null) {
            return null;
        }

        String path = uri.getPath();
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        File file = new File(path);
        if (file.exists()) {
            return BitmapFactory.decodeFile(path, options);
        }

        return null;
    }
}
