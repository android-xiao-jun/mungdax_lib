package com.allo.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Desc
 *
 * @author zhangxiaolin
 * Date 2022/4/21
 * Copyright © ALLO
 */
public class AdUtils {
    public static float getScreenWidthDp(Context context){
        final float scale = context.getResources().getDisplayMetrics().density;
        float width = context.getResources().getDisplayMetrics().widthPixels;
        return width / (scale <= 0 ? 1 : scale) + 0.5f;
    }

    public static int getScreenWidthInPx(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return (int) (getScreenHeightInPx(context) + getStatusBarHeight(context));
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int getScreenHeightInPx(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    //获取状态栏高度
    public static float getStatusBarHeight(Context context) {
        float height = 0;
        int resourceId = context.getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = context.getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

}
