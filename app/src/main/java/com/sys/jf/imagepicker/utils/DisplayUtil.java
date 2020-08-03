package com.sys.jf.imagepicker.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class DisplayUtil {
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int dp2px(float dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, Resources.getSystem().getDisplayMetrics());
    }
}
