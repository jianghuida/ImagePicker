package com.sys.jf.imagepicker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;
import com.sys.jf.imagepicker.R;

import java.io.File;

/**
 * Created by myc on 2016/12/14.
 * More Code on 1101255053@qq.com
 * Description:
 */
public class FileUtils {
    public static int screenWidth = 0;

    public static boolean fileIsExists(String path) {
        if (path == null || path.trim().length() <= 0) {
            return false;
        }
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isSizeFit(String filePath) {
        int[] imgWH = getImageWidthHeight(filePath);
        return (imgWH[0] * imgWH[1] >= 640 * 480);
    }

    public static int[] getImageWidthHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth, options.outHeight};
    }

    public static int getScreenWidth(Context context) {
        if (screenWidth == 0) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            screenWidth = dm.widthPixels;
        }
        return screenWidth;
    }

    public static void CustomCenterToast(Context context, final String text, final int layoutId) {
        //获取自定义视图
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        TextView tvToast = view.findViewById(R.id.tv_toast);
        tvToast.setMaxWidth((int) (getScreenWidth(context) * 0.8));
        tvToast.setMinWidth((int) (getScreenWidth(context) * 0.45));
        tvToast.setTop(14);
        tvToast.setRight(20);
        //设置文本
        tvToast.setText(text);
        ToastUtils.setView(view);
        ToastUtils.show(text);
    }

    public static void CustomCenterToast(Context context, int resId) {
        CustomCenterToast(context, context.getString(resId), R.layout.layout_custom_toast);
    }
}
