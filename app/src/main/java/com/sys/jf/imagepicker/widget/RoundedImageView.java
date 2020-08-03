package com.sys.jf.imagepicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sys.jf.imagepicker.R;
import com.sys.jf.imagepicker.utils.DisplayUtil;

public class RoundedImageView extends ImageView {

    private float leftTopRadius, rightTopRadius, leftBottomRadius, rightBottomRadius;

    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView);
        leftTopRadius = array.getDimensionPixelSize(R.styleable.RoundedImageView_radiusLeftTop, 0);
        rightTopRadius = array.getDimensionPixelSize(R.styleable.RoundedImageView_radiusRightTop, 0);
        leftBottomRadius = array.getDimensionPixelSize(R.styleable.RoundedImageView_radiusLeftBottom, 20);
        rightBottomRadius = array.getDimensionPixelSize(R.styleable.RoundedImageView_radiusRightBottom, 20);
        array.recycle();
    }


    /**
     * 画图
     *
     * @param canvas
     */
    protected void onDraw(Canvas canvas) {
        /*圆角的半径，依次为左上角xy半径，右上角，右下角，左下角*/
        float[] rids = {leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius, rightBottomRadius, leftBottomRadius, leftBottomRadius};
        Path path = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        /*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/
        path.addRoundRect(new RectF(0, 0, w, h), rids, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }

    public void setCorner(int dp) {
        leftTopRadius = rightTopRadius = leftBottomRadius = rightBottomRadius = DisplayUtil.dp2px(dp);
    }
}
