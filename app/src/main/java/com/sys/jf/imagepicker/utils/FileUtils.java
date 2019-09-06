package com.sys.jf.imagepicker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sys.jf.imagepicker.R;

import java.io.File;

/**
 * Created by myc on 2016/12/14.
 * More Code on 1101255053@qq.com
 * Description:
 */
public class FileUtils {
    public static int screenWidth = 0;
    private static ToastHandler sToastHandler;
    private static Toast sToast;

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
        setView(view);
        show(text);
    }

    public static void CustomCenterToast(Context context, int resId) {
        CustomCenterToast(context, context.getString(resId), R.layout.layout_custom_toast);
    }

    public static void setView(View view) {

        checkToastState();

        // 这个 View 不能为空
        if (view == null) {
            throw new IllegalArgumentException("Views cannot be empty");
        }

        // 当前必须用 Application 的上下文创建的 View，否则可能会导致内存泄露
        if (view.getContext() != view.getContext().getApplicationContext()) {
            throw new IllegalArgumentException("The view must be initialized using the context of the application");
        }

        // 如果吐司已经创建，就重新初始化吐司
        if (sToast != null) {
            // 取消原有吐司的显示
            sToast.cancel();
            sToast.setView(view);
        }
    }

    private static void checkToastState() {
        // 吐司工具类还没有被初始化，必须要先调用init方法进行初始化
        if (sToast == null) {
            throw new IllegalStateException("ToastUtils has not been initialized");
        }
    }

    public static void show(CharSequence text) {

        checkToastState();

        if (text == null || "".equals(text.toString())) return;

        sToastHandler.add(text);
        sToastHandler.show();
    }
}
