package com.juphoon.imgeditor.core.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public abstract class PictureDecoder {

    private Uri uri;

    public PictureDecoder(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public Bitmap decode() {
        return decode(null);
    }

    public abstract Bitmap decode(BitmapFactory.Options options);

}
