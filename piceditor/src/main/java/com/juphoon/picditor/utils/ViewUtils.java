package com.juphoon.picditor.utils;

import android.view.View;
import android.view.ViewGroup;

public class ViewUtils {

    public static void setVisibility(View view, int visibility) {
        if (view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

    public static void setLayoutParam(int width, int height, View... views) {
        ViewGroup.LayoutParams layoutParams;
        for (View view : views) {
            layoutParams = view.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);
        }
    }
}
