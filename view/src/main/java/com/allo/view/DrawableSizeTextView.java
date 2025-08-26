package com.allo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @Author: zj
 * @Date: 2021/11/19
 * @description: 带图片的 上下左右 图片设置大小 （防止分辨率不一样展示有拉伸）
 */
public class DrawableSizeTextView extends AppCompatTextView {

    private int mDrawableWidth;
    private int mDrawableHeight;

    public DrawableSizeTextView(@NonNull Context context) {
        this(context, null);
    }

    public DrawableSizeTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableSizeTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DrawableSizeTextView);
        mDrawableWidth = array.getDimensionPixelSize(R.styleable.DrawableSizeTextView_drawableWidth, 0);
        mDrawableHeight = array.getDimensionPixelSize(R.styleable.DrawableSizeTextView_drawableHeight, 0);
        array.recycle();

        refreshDrawablesSize();
    }

    /**
     * 限定 Drawable 大小
     */
    public void setDrawableSize(int width, int height) {
        mDrawableWidth = width;
        mDrawableHeight = height;
        if (!isAttachedToWindow()) {
            return;
        }
        refreshDrawablesSize();
    }

    /**
     * 限定 Drawable 宽度
     */
    public void setDrawableWidth(int width) {
        mDrawableWidth = width;
        if (!isAttachedToWindow()) {
            return;
        }
        refreshDrawablesSize();
    }

    /**
     * 限定 Drawable 高度
     */
    public void setDrawableHeight(int height) {
        mDrawableHeight = height;
        if (!isAttachedToWindow()) {
            return;
        }
        refreshDrawablesSize();
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
        if (!isAttachedToWindow()) {
            return;
        }
        refreshDrawablesSize();
    }

    @Override
    public void setCompoundDrawablesRelative(@Nullable Drawable start, @Nullable Drawable top, @Nullable Drawable end, @Nullable Drawable bottom) {
        super.setCompoundDrawablesRelative(start, top, end, bottom);
        if (!isAttachedToWindow()) {
            return;
        }
        refreshDrawablesSize();
    }

    /**
     * 刷新 Drawable 列表大小
     */
    private void refreshDrawablesSize() {
        if (mDrawableWidth == 0 || mDrawableHeight == 0) {
            return;
        }
        Drawable[] compoundDrawables = getCompoundDrawables();
        if (compoundDrawables[0] != null || compoundDrawables[1] != null) {
            super.setCompoundDrawables(limitDrawableSize(compoundDrawables[0]),
                    limitDrawableSize(compoundDrawables[1]),
                    limitDrawableSize(compoundDrawables[2]),
                    limitDrawableSize(compoundDrawables[3]));
            return;
        }
        compoundDrawables = getCompoundDrawablesRelative();
        super.setCompoundDrawablesRelative(limitDrawableSize(compoundDrawables[0]),
                limitDrawableSize(compoundDrawables[1]),
                limitDrawableSize(compoundDrawables[2]),
                limitDrawableSize(compoundDrawables[3]));
    }

    /**
     * 重新限定 Drawable 宽高
     */
    private Drawable limitDrawableSize(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (mDrawableWidth == 0 || mDrawableHeight == 0) {
            return drawable;
        }
        drawable.setBounds(0, 0, mDrawableWidth, mDrawableHeight);
        return drawable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isClickable()){
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.setAlpha(0.4F);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                this.setAlpha(1.0F);
                break;
            case MotionEvent.ACTION_CANCEL:
                this.setAlpha(1.0F);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
